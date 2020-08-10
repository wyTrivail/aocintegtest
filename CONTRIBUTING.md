# How to contribute

Bug reports and pull requests from users are what keep this project working.

## Basics

1. Create an issue and describe your idea
2. Fork it
3. Create your feature branch (git checkout -b my-new-feature)
4. Commit your changes (git commit -am 'Add some feature')
5. Publish the branch (git push origin my-new-feature)
6. Create a new Pull Request

## typical cases

### I want to add an new AMI for ec2 test.

You will need to add new enum into TestAMI

### I want to add a new test case like test aoc in ECS

1. add an new testcase in `/com/amazon/aocagent/enums/TestCase`
2. if the current components like EC2TestBed can't fulfill your requirement, you can go ahead to a new testbed class by implementing `com/amazon/aocagent/testbeds/TestBed`
3. ditto for the other components.

### I want to add a new configuration for aoc

1. add an new config file under resources/mustache, this file will be interpret with mustache, so you can use mustache format placeholder inside it: https://mustache.github.io/
2. add an new config in `com/amazon/aocagent/enums/OTConfig`
3. ditto for ExpectedMetric config.

### Write documentation

Add the usage into README.md.

## Running for development

please check README.md: Run as Command Line

### Intellij idea plugins

if you are using Intellij idea for development, we recommend you to install this plugin so that it helps you to reformat your code. https://github.com/google/google-java-format




