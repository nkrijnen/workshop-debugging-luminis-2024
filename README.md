# Observability by Design

![weather-station.jpg](weather-station.jpg)

## Setup

... cdk, aws-cli, intellij community, jdk 17 (through intellij)

## AWS Authentication

First [make sure you have access](https://luminiseu.atlassian.net/wiki/spaces/LUMINF/pages/12025923/AWS+Sandbox+accounts) to the `aws-amsterdam-sandbox` account.

Next [setup an AWS SSO for on your machine](https://luminiseu.atlassian.net/wiki/spaces/LUMINF/pages/12026255/AWS+SSO+Credentials).
Which should setup a the `AWSAdministratorAccess-998150297714` profile.

## Local AWS Authentication

```shell
aws sso login --profile AWSAdministratorAccess-998150297714
```

When after a while, you get errors that the token is invalid you may need to run that again in a terminal.

## Run Tests

Use the `Run Tests` run configuration in IDEA that is included with this project.

## Build & Deploy

Use the `Deploy Lambda` run configuration. Or run:

```shell
./deploy.sh
```

## View in AWS

Open https://luminis.awsapps.com/start#/ to login to the AWS Console.

Make sure you have access to the `aws-amsterdam-sandbox` account.

### Things to do in AWS

You can view the stack that you deployed in [CloudFormation](https://eu-west-1.console.aws.amazon.com/cloudformation/home?region=eu-west-1#/stacks). Make sure you select the same region that you deployed to (should be Ireland: eu-west-1).

In the cloudformation resources you can find a link to your Lambda and from there you can view and tail logs.

## Switch assignments

When you've completed an assignment, you can switch to the next one by increasing `scenario` nr in `LambdaApp`.

## After the workshop

Let's tidy up! Run:

```shell
./destroy.sh
```
