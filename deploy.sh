export AWS_PROFILE=AWSAdministratorAccess-998150297714
export AWS_REGION=eu-west-1
./gradlew shadowJar
#(cd infra; cdk deploy --require-approval never --hotswap-fallback)
(cd infra; cdk deploy --require-approval never)
