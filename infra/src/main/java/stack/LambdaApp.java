package stack;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class LambdaApp {
    public static void main(final String[] args) {
        App app = new App();
        String userName = getOsUserName();

        // Increment te scenario nr when you've completed an assignment, then deploy again.
        int scenario = 1;

        new LambdaStack(app, "ObservabilityByDesign-" + userName, StackProps.builder().build(), userName, scenario);
        app.synth();
    }

    private static String getOsUserName() {
        return sanitize(System.getenv().get("USER"));
    }

    private static String sanitize(String osUserName) {
        return osUserName.replaceAll("[^a-zA-Z0-9]", "");
    }
}
