package eu.luminis.debugging.report;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import eu.luminis.debugging.report.model.ObservationEvent;
import eu.luminis.debugging.report.model.WeatherCondition;
import eu.luminis.debugging.report.model.WeatherStation;
import eu.luminis.debugging.report.util.Json;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.crt.AwsCrtHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

import static eu.luminis.debugging.report.model.WeatherStation.stations;

@SuppressWarnings("unused")
public class Handler implements RequestHandler<SNSEvent, String> {

    private final String bucketPrefix = Objects.toString(System.getenv().get("BUCKET_PREFIX"), System.getenv().get("USER"));

    private final S3Client s3 = S3Client.builder()
            .region(Region.EU_WEST_1)
            .httpClientBuilder(AwsCrtHttpClient.builder()
                            .connectionTimeout(Duration.ofSeconds(3))
                            .maxConcurrency(100)
//            ).credentialsProvider(
//                    ProfileCredentialsProvider.builder().profileName("AWSAdministratorAccess-998150297714").build()
            )
            .build();

    @Override
    public String handleRequest(SNSEvent event, Context context) {
        var logger = context.getLogger();
        logger.log("EVENT: " + Json.format(event));

        if (event.getRecords().isEmpty()) {
            return "Success";
        }
        var lastRecord = event.getRecords().get(event.getRecords().size() - 1);
        var message = lastRecord.getSNS().getMessage();
        var observationEvent = Json.parse(message, ObservationEvent.class);

        logger.log("Observations: " + observationEvent);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bout, true, StandardCharsets.UTF_8);
        try {
            out.append("<table>\n");
            out.append("<tr><th>Station</th><th>Weather Condition</th><th></th></tr>\n");
            for (WeatherStation station : stations) {
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
                        "<td>" + condition.getImageEmoji() + "</td>" +
                        "<td><a href=\"" + "https://earth.google.com/web/@" + station.latitude() + "," + station.longitude() + "\" target=\"_blank\">show map</a></td></tr>\n");
            }
            out.append("</table>");
        } catch (Exception ignored) {
            out.append("<tr><td>ERROR</td></tr>\n");
        }

        byte[] bytes = bout.toByteArray();
        String key = bucketPrefix + "/index.html";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket("debugging-like-a-pro.weather-reports")
                .key(key)
                .cacheControl("no-store")
                .contentLength((long) bytes.length)
                .contentType("text/html; charset=utf-8")
                .build();
        s3.putObject(request, RequestBody.fromBytes(bytes));
        logger.log("Weather report updated ➡️ http://debugging-like-a-pro.weather-reports.s3-website-eu-west-1.amazonaws.com/" + key);

        return "Success";
    }

}