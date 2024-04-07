package eu.luminis.debugging.report;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import software.amazon.awssdk.http.crt.AwsCrtHttpClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;

@SuppressWarnings("unused")
public class Handler implements RequestHandler<SNSEvent, String> {

    private S3Client s3 = S3Client.builder()
            .httpClientBuilder(AwsCrtHttpClient.builder()
                    .connectionTimeout(Duration.ofSeconds(3))
                    .maxConcurrency(100)
            )
            .build();

    @Override
    public String handleRequest(SNSEvent event, Context context) {
        for (SNSEvent.SNSRecord record : event.getRecords()) {
            String message = record.getSNS().getMessage();
            ObservationEvent observationEvent = Json.parse(message, ObservationEvent.class);
            System.out.println("Observations: " + observationEvent.getObservations());
            System.out.println("Date: " + observationEvent.getDate());
        }
        return "Success";
    }
}