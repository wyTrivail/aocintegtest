# AOC IntegTest
## Run as Command Line

### Prerequisites

1. Configure aws credentials(.aws/credentials) on your host.
2. Setup the related resources(S3 bucket, iam roles, sshkey, security group) in your aws account by running this command 

``
gradle run --args="setup" 
``

### Run EC2 Integ-test

````
gradle run --args="integ-test -t=EC2Test --package-version={the version you want to test}"
````

### Run S3 Uploading

````
gradle run --args="release -t=S3Release --local-packages-dir={the path to the packages you want to upload}"
````

an example for the local-package-dir:

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

## Run as Github Action



1. Run as Github Action

1.1 description

This action prints "Hello World" or "Hello" + the name of a person to greet to the log.

1.2 Inputs

### `who-to-greet`

**Required** The name of the person to greet. Default `"World"`.

1.3 Outputs

### `time`

The time we greeted you.

1.4 Example usage

uses: actions/hello-world-docker-action@v1
with:
  who-to-greet: 'Mona the Octocat'
