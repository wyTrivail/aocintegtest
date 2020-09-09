# AOC IntegTest
## Run as Command Line

### Prerequisites

1. Configure aws credentials(.aws/credentials) on your host.
2. Setup the related resources(S3 bucket, iam roles, sshkey, security group) in your aws account by running this command 

``
gradle run --args="setup" 
``

this command will generate a file .aoc-stack.yml in the current work dir. 
you can also provide .aoc-stack.yml in the current work dir before setup so that the setup command will create the resources base on your stack file, below is an example of the stack file(you will need to adjust the s3 bucket name as it's global unique and someone has already occupy it):

```
---
testingRegion: "us-west-2"
s3ReleaseCandidateBucketName: "aoc-release-candidate"
s3BucketName: "aws-opentelemetry-collector-test"
sshKeyS3BucketName: "aoc-ssh-key"
traceDataS3BucketName: "trace-expected-data"
```

### Run S3 Uploading

Firstly, you may want to upload the packages to s3 for testing.

run below command for uploading

````
gradle run --args="release -t=S3Release --local-packages-dir={the path to the packages you want to upload}"
````

an example for the local-package-dir, the VERSION file contains the package version like "v0.1.10".

````
local-packages
|-- debian
|   |-- amd64
|   |   `-- aws-opentelemetry-collector.deb
|   `-- arm64
|       `-- aws-opentelemetry-collector.deb
|-- linux
|   |-- amd64
|   |   `-- aws-opentelemetry-collector.rpm
|   `-- arm64
|       `-- aws-opentelemetry-collector.rpm
|-- windows
|   `-- amd64
|       `-- aws-opentelemetry-collector.msi
|-- GITHUB_SHA
|-- VERSION
`-- awscollector.tar
````

### Run EC2 Integ-test

````
gradle run --args="integ-test -t=EC2_TEST --package-version={the version you want to test}"
````

### Run ECS Integ-test with EC2 on Sidecar mode (EMF Metrics)
```
gradle run --args="integ-test -t=ECS_TEST -a=ECS_OPTIMIZED --package-version={the version you want to test} -e ecsLaunchType=EC2"
```

### Run ECS Integ-test with Fargate on Sidecar mode (EMF Metrics)
```
gradle run --args="integ-test -t=ECS_TEST --package-version={the version you want to test} -e ecsLaunchType=FARGATE"
```

### Clean ECS testing resources
```
gradle run --args="clean -t=ECSClean --package-version={the version you want to test}"
```

### Run EKS Integ-test on Sidecar mode (EMF Metrics)
```
gradle run --args="integ-test -t=EKS_TEST --package-version={the version you want to test} -k eksClusterName=my-cluster-name -k awsAuthenticatorPath=/my/authenticator/path"
```


### Command Help

`
gradle run --args="-h"
`

`
gradle run --args="integ-test -h"
`

`
gradle run --args="release -h"
`

## Run as Github Action

### description

this action wraps the command lines, so it could be used in github workflow to perform integ-test and release for aoc.

### Inputs

#### `running_type`

value could be `integ-test`, `release`, `candidate` 
value is `integ-test` by default

#### `opts`

the remaining options for command
the value is "-t=EC2Test -s=build/packages/.aoc-stack-test.yml" by default.

#### Example usage

```yaml
uses: wyTrivail/aocintegtest@master
with:
  running_type: integ-test
  opts: "-t=EC2Test -s=build/packages/.aoc-stack-test.yml"
```

## Contributing

We have collected notes on how to contribute to this project in CONTRIBUTING.md.
