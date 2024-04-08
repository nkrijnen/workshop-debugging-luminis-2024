package stack;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class LambdaApp {
    public static void main(final String[] args) {

        App app = new App();
        String userName = getOsUserName();
        new LambdaStack(app, "DebuggingLikeAPro-" + userName, StackProps.builder().build(), userName);
        app.synth();
    }

    private static String getOsUserName() {
        return sanitize(System.getenv().get("USER"));
    }

    private static String sanitize(String osUserName) {
        return osUserName.replaceAll("[^a-zA-Z0-9]", "");
    }
}