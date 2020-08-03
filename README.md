# AOC IntegTest
## Run as Command Line

### Prerequisites

1. Configure aws credentials(.aws/credentials) on your host.
2. Setup the related resources(S3 bucket, iam roles, sshkey, security group) in your aws account by running this command 

``
gradle run --args="setup" 
``

this command will generate a file .aoc-stack.yml in the current work dir. 
you can also provide .aoc-stack.yml in the current work dir before setup so that the setup command will create the resources base on your stack file.

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