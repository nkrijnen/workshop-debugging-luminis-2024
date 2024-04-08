package eu.luminis.debugging.report;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import eu.luminis.debugging.report.model.ObservationEvent;
import eu.luminis.debugging.report.model.ProducerWeatherStation;
import eu.luminis.debugging.report.util.Json;
import software.amazon.awssdk.http.crt.AwsCrtHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;
import java.util.List;

import static java.lang.Double.parseDouble;

@SuppressWarnings("unused")
public class Handler implements RequestHandler<SNSEvent, String> {

    private final S3Client s3 = S3Client.builder()
            .httpClientBuilder(AwsCrtHttpClient.builder()
                    .connectionTimeout(Duration.ofSeconds(3))
                    .maxConcurrency(100)
            )
            .build();

    @Override
    public String handleRequest(SNSEvent event, Context context) {
        List<ProducerWeatherStation> weatherStations = getWeatherStationInfo();

        LambdaLogger logger = context.getLogger();
        logger.log("ENVIRONMENT VARIABLES: " + Json.format(System.getenv()));
        logger.log("CONTEXT: " + Json.format(context));
        logger.log("EVENT: " + Json.format(event));

        for (SNSEvent.SNSRecord record : event.getRecords()) {
            String message = record.getSNS().getMessage();
            ObservationEvent observationEvent = Json.parse(message, ObservationEvent.class);
            System.out.println("Observations: " + observationEvent);
        }


        return "Success";
    }

    private List<ProducerWeatherStation> getWeatherStationInfo() {
        ScanResponse scanResponse;
        try (DynamoDbClient dbClient = DynamoDbClient.builder().build()) {

            scanResponse = dbClient
                    .scan(ScanRequest.builder().tableName("debugging-like-a-pro.weather-stations").build());
        }

        return scanResponse.items().stream()
                .map(dynamoItem -> new ProducerWeatherStation(dynamoItem.get("WeatherStation").s(), parseDouble(dynamoItem.get("lat").s()), parseDouble(dynamoItem.get("long").s())))
                .toList();
    }
}