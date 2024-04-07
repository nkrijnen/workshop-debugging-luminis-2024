package eu.luminis.debugging.report;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import software.amazon.awssdk.http.crt.AwsCrtHttpClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;
import java.util.List;

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
        List<String> messages = event.getRecords().stream()
                .map(SNSEvent.SNSRecord::getEventSource).toList();

        messages.forEach(message ->
                System.out.println(message)
        );

//        s3.putObject()

        return "ok";
    }
}
