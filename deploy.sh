export AWS_PROFILE=AWSAdministratorAccess-998150297714
./gradlew shadowJar
(cd infra; cdk deploy)
