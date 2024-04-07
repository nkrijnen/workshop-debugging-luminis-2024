package stack;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.sns.ITopic;
import software.amazon.awscdk.services.sns.Subscription;
import software.amazon.awscdk.services.sns.SubscriptionProtocol;
import software.amazon.awscdk.services.sns.Topic;
import software.constructs.Construct;

public class LambdaStack extends Stack {
    public LambdaStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        String jarPath = "../lambda/build/libs/lambda-1.0-SNAPSHOT-all.jar";
        String handlerClass = "eu.luminis.debugging.report.Handler";
        String topicArn = "arn:aws:sns:eu-west-1:998150297714:debugging-like-a-pro_weather-observations";
        String roleName = "DebuggingLikeAProLambdaRole";

        Function weatherStationLambda = new Function(this, "Lambda",
                FunctionProps.builder()
                        .role(Role.fromRoleName(this, "LambdaRole", roleName))
                        .runtime(Runtime.JAVA_17)
                        .handler(handlerClass)
                        .code(Code.fromAsset(jarPath))
                        .build());

        ITopic topic = Topic.fromTopicArn(this, "topic", topicArn);

        Subscription.Builder.create(this, "DebuggingLikeAPro")
                .topic(topic)
                .protocol(SubscriptionProtocol.LAMBDA)
                .endpoint(weatherStationLambda.getFunctionArn())
                .build();
    }
}