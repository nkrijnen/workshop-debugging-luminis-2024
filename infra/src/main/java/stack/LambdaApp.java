package stack;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class LambdaApp {
    public static void main(final String[] args) {

        App app = new App();
        new LambdaStack(app, "DebuggingLikeAPro-" + getOsUserName(), StackProps.builder().build());
        app.synth();
    }

    private static String getOsUserName() {
        return sanitize(System.getenv().get("USER"));
    }

    private static String sanitize(String osUserName) {
        return osUserName.replaceAll("[^a-zA-Z0-9]", "");
    }
}