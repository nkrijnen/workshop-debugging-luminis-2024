package eu.luminis.observability.report;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import eu.luminis.observability.report.model.ObservationEvent;
import eu.luminis.observability.report.model.WeatherCondition;
import eu.luminis.observability.report.model.WeatherStation;
import eu.luminis.observability.report.util.Json;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.crt.AwsCrtHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static java.lang.Double.parseDouble;

@SuppressWarnings("unused")
public class Handler implements RequestHandler<SNSEvent, String> {

    private final String bucketPrefix = Objects.toString(System.getenv().get("BUCKET_PREFIX"), System.getenv().get("USER"));

    private final S3Client s3 = S3Client.builder()
            .region(Region.EU_WEST_1)
            .httpClientBuilder(AwsCrtHttpClient.builder()
                    .connectionTimeout(Duration.ofSeconds(3))
                    .maxConcurrency(100)
            )
            .build();

    private final DynamoDbClient dynamoDb = DynamoDbClient.builder()
            .region(Region.EU_WEST_1)
            .httpClientBuilder(AwsCrtHttpClient.builder()
                    .connectionTimeout(Duration.ofSeconds(3))
                    .maxConcurrency(100)
            )
            .build();

    @Override
    public String handleRequest(SNSEvent event, Context context) {
        var logger = context.getLogger();
//        logger.log("EVENT: " + Json.format(event));

        if (event.getRecords().isEmpty()) {
            return "Success";
        }
        var lastRecord = event.getRecords().get(event.getRecords().size() - 1);
        var message = lastRecord.getSNS().getMessage();
        var observationEvent = Json.parse(message, ObservationEvent.class);

        logger.log("Observations: " + observationEvent);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bout, true, StandardCharsets.UTF_8);
        out.append("<h1>Team: " + bucketPrefix + "</h1>\n");
        out.append("<div style=\"position: fixed; top: 0; right: 0; text-align: right; color: #999; font-size: 80%;\">Last message received:<br/>" + lastRecord.getSNS().getMessageId() + "<br/>" + lastRecord.getSNS().getTimestamp() + "</div>\n");
        out.append("<table>\n");
        out.append("<tr><th>Station</th><th>Weather Condition</th><th>🌡️</th><th>🌡️ last year</th><th></th></tr>\n");

        ScanResponse scanResponse = dynamoDb.scan(ScanRequest.builder().tableName("observability-by-design.weather-stations").build());
        List<WeatherStation> weatherStations = scanResponse.items().stream()
                .map(dynamoItem -> new WeatherStation(dynamoItem.get("WeatherStation").s(), parseDouble(dynamoItem.get("lat").s()), parseDouble(dynamoItem.get("long").s()), dynamoItem.get("historicTemperatureData").s()))
                .toList();
//            for (WeatherStation station : weatherStations) {
        for (WeatherStation station : WeatherStation.stations) {
            try {
                var observation = observationEvent.observations().stream().filter(o -> o.station().equals(station.name())).findAny().orElse(null);

                WeatherCondition condition;
                if (observation.temperature() > 10 && observation.humidity() < 0.5) {
                    condition = WeatherCondition.SUNNY;
                } else if (observation.temperature() > 10 && observation.humidity() >= 0.5) {
                    condition = WeatherCondition.CLOUDY;
                } else if (observation.temperature() < 10 && observation.humidity() > 0.8) {
                    condition = WeatherCondition.POURING;
                } else if (observation.temperature() < 0) {
                    condition = WeatherCondition.SNOW;
                } else {
                    condition = null;
                }

                out.append("<tr><td>" + observation.station() + "</td>" +
                        "<td style=\"text-align: center\">" + condition.getImageEmoji() + "</td>" +
                        "<td style=\"text-align: right\">" + "🌡️%5.2f".formatted(observation.temperature()) + "</td>" +
                        "<td style=\"text-align: right\">" + "🌡️%5.2f".formatted(station.getHistoricTemperatures().get("%d-%02d-%02d".formatted(observationEvent.date().getYear() - 1, observationEvent.date().getMonthValue(), observationEvent.date().getDayOfMonth()))) + "</td>" +
                        "<td><a href=\"https://maps.google.com/?q=\"" + station.latitude() + "," + station.longitude() + "\" target=\"_blank\">show map</a></td></tr>\n");

            } catch (Exception ignored) {
                out.append("<tr><td>ERROR</td></tr>\n");
//                logger.log("ERROR " + ignored);
            }
        }
        out.append("</table>");

        byte[] bytes = bout.toByteArray();
        String key = bucketPrefix + "/index.html";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket("observability-by-design.weather-reports")
                .key(key)
                .cacheControl("no-store")
                .contentLength((long) bytes.length)
                .contentType("text/html; charset=utf-8")
                .build();
        s3.putObject(request, RequestBody.fromBytes(bytes));
        logger.log("Weather report updated ➡️ http://observability-by-design.weather-reports.s3-website-eu-west-1.amazonaws.com/" + key);

        return "Success";
    }

}
