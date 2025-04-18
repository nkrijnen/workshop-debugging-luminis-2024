package stack;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.sns.Subscription;
import software.amazon.awscdk.services.sns.SubscriptionProtocol;
import software.amazon.awscdk.services.sns.Topic;
import software.constructs.Construct;

import java.util.Map;

public class LambdaStack extends Stack {
    public LambdaStack(final Construct scope, final String id, final StackProps props, final String userName, int scenario) {
        super(scope, id, props);

        if (scenario < 1 || scenario > 8) {
            throw new IllegalArgumentException("Scenario must be between 1 and 8");
        }

        String jarPath = "../lambda/build/libs/lambda-1.0-SNAPSHOT-all.jar";
        String handlerClass = "eu.luminis.observability.report.Handler";

        String topicArn = "arn:aws:sns:eu-west-1:998150297714:observability-by-design_weather-observations-scenario-" + scenario;
        String roleName = "ObservabilityByDesignLambdaRole";

        Function weatherStationLambda = new Function(this, "Lambda",
                FunctionProps.builder()
                        .role(Role.fromRoleName(this, "LambdaRole", roleName))
                        .memorySize(1024)
                        .timeout(Duration.seconds(20))
                        .logRetention(RetentionDays.ONE_WEEK)
                        .runtime(Runtime.JAVA_17)
                        .handler(handlerClass)
                        .code(Code.fromAsset(jarPath))
                        .environment(Map.of(
                                "BUCKET_PREFIX", userName
                        ))
                        .build());

        CfnPermission.Builder.create(this, "SNSToLambda")
                .action("lambda:InvokeFunction")
                .principal("sns.amazonaws.com")
                .functionName(weatherStationLambda.getFunctionArn())
                .sourceArn(topicArn)
                .build();

        Subscription.Builder.create(this, "Subscription")
                .topic(Topic.fromTopicArn(this, "Topic", topicArn))
                .protocol(SubscriptionProtocol.LAMBDA)
                .endpoint(weatherStationLambda.getFunctionArn())
                .build();
    }
}
