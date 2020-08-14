package com.amazon.aocagent.services;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.models.Context;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsAsyncClientBuilder;
import com.amazonaws.services.logs.model.CreateLogGroupRequest;
import com.amazonaws.services.logs.model.DescribeLogGroupsRequest;
import com.amazonaws.services.logs.model.DescribeLogGroupsResult;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ECSService {
    private AmazonECS ecsClient;
    private AWSLogs awsLogsClient;

    private static final String AWS_LOGS_REGION_KEY = "awslogs-region";
    private static final String AWS_LOGS_GROUP_KEY = "awslogs-group";
    private static final String AWS_LOGS_STREAM_PREFIX_KEY = "awslogs-stream-prefix";
    private static final String ECS_PREFIX = "ecs";
    private static final String AOC_EMITTER = "aoc-emitter";
    private static final String EC2 = "EC2";
    private static final String INSTANCE_ID = "otlp_instance_id";
    private static final String OTLP_ENDPOINT = "otlp_endpoint";

    public ECSService(final String region) {
        this.ecsClient = AmazonECSClientBuilder.standard()
                .withRegion(region).build();
        this.awsLogsClient = AWSLogsAsyncClientBuilder.standard()
                .withRegion(region).build();
    }

    public CreateClusterResult createCluster() {
        CreateClusterRequest request = new CreateClusterRequest()
                .withClusterName(GenericConstants.ECS_SIDECAR_CLUSTER.getVal());
        log.info("creating ECS cluster: {}", GenericConstants.ECS_SIDECAR_CLUSTER.getVal());
        return ecsClient.createCluster(request);
    }

    /**
     * create and register ecs task definition for both EC2 and
     * fargate running mode
     *
     * @param context
     * @return {@link RegisterTaskDefinitionResult} for registering ecs container defs
     */
    public RegisterTaskDefinitionResult registerTaskDefinition(Context context) {
        RegisterTaskDefinitionRequest request;

        String launchType = context.getLaunchType();
        List<ContainerDefinition> containerDefinitionList = this.buildContainerDefinition(context);
        if (launchType.equalsIgnoreCase(EC2)) {
            request = new RegisterTaskDefinitionRequest()
                    .withFamily("aoc-" + launchType)
                    .withTaskRoleArn(GenericConstants.ECS_TASK_ROLE.getVal())
                    .withExecutionRoleArn(GenericConstants.ECS_TASK_EXECUTION_ROLE.getVal())
                    .withRequiresCompatibilities(Compatibility.EC2)
                    .withNetworkMode(NetworkMode.Bridge)
                    .withCpu("256")
                    .withMemory("512")
                    .withContainerDefinitions(containerDefinitionList);

        } else {
            request = new RegisterTaskDefinitionRequest()
                    .withFamily("aoc-" + launchType)
                    .withTaskRoleArn(GenericConstants.ECS_TASK_ROLE.getVal())
                    .withExecutionRoleArn(GenericConstants.ECS_TASK_EXECUTION_ROLE.getVal())
                    .withRequiresCompatibilities(Compatibility.FARGATE)
                    .withNetworkMode(NetworkMode.Awsvpc)
                    .withCpu("256")
                    .withMemory("512")
                    .withContainerDefinitions(containerDefinitionList);
        }
        return ecsClient.registerTaskDefinition(request);
    }

    private List<ContainerDefinition> buildContainerDefinition(Context context) {
        List<ContainerDefinition> definitions = new ArrayList<>();

        PortMapping portMapping = new PortMapping()
                .withContainerPort(55680)
                .withHostPort(55680)
                .withProtocol(TransportProtocol.Tcp);

        ContainerDefinition emitterDef = new ContainerDefinition()
                .withName(AOC_EMITTER)
                .withImage(GenericConstants.DATA_EMITTER_IMAGE.getVal())
                .withEssential(true)
                .withLogConfiguration(getLogConfiguration(context, "emitter"));
        if (context.getLaunchType().equals(EC2)) {
            String instanceId = GenericConstants.ECS_EC2_INSTANCE_ID.getVal() + "-" + System.currentTimeMillis();
            context.setInstanceId(instanceId);
            emitterDef.withEnvironment(
                    new KeyValuePair()
                            .withName(OTLP_ENDPOINT)
                            .withValue("172.17.0.1:55680"),
                    new KeyValuePair()
                            .withName(INSTANCE_ID)
                            .withValue(instanceId));

        } else {
            String instanceId = GenericConstants.ECS_FARGATE_INSTANCE_ID.getVal() + "-" + System.currentTimeMillis();
            context.setInstanceId(instanceId);
            emitterDef.withEnvironment(
                    new KeyValuePair()
                            .withName(OTLP_ENDPOINT)
                            .withValue("localhost:55680"),
                    new KeyValuePair()
                            .withName(INSTANCE_ID)
                            .withValue(instanceId));
        }

        ContainerDefinition aocDef = new ContainerDefinition()
                .withName("aoc-collector")
                .withImage(GenericConstants.AOC_IMAGE.getVal())
                .withEssential(true)
                .withPortMappings(portMapping)
                .withLogConfiguration(getLogConfiguration(context, "collector"));

        definitions.add(emitterDef);
        definitions.add(aocDef);
        return definitions;
    }

    /**
     * create log groups for ECS task instances
     *
     * @param context
     * @param image
     * @return {@link LogConfiguration} for ECS tasks
     */
    private LogConfiguration getLogConfiguration(Context context, String image){
        final Map<String, String> logConfigurationOptions = new HashMap<>();
        String logGroupName = String.format("/%s/%s", ECS_PREFIX, "ecs-cwagent-sidecar-" + image);
        logConfigurationOptions.put(AWS_LOGS_REGION_KEY, context.getStack().getTestingRegion());
        logConfigurationOptions.put(AWS_LOGS_GROUP_KEY, logGroupName);
        logConfigurationOptions.put(AWS_LOGS_STREAM_PREFIX_KEY, ECS_PREFIX);
        log.info("Getting log group: {}", logGroupName);

        createLogGroupIfNotPresent(logGroupName);

        return new LogConfiguration()
                .withLogDriver(LogDriver.Awslogs)
                .withOptions(logConfigurationOptions);
    }

    private void createLogGroupIfNotPresent(String logGroupName) {
        DescribeLogGroupsRequest describeLogGroupsRequest = new DescribeLogGroupsRequest()
                .withLogGroupNamePrefix(logGroupName);
        DescribeLogGroupsResult describeLogGroupsResult = awsLogsClient.describeLogGroups(describeLogGroupsRequest);
        if (describeLogGroupsResult.getLogGroups().isEmpty()) {
            log.info("Creating log group: {}", logGroupName);
            CreateLogGroupRequest createLogGroupRequest = new CreateLogGroupRequest()
                    .withLogGroupName(logGroupName);
            awsLogsClient.createLogGroup(createLogGroupRequest);
        }
    }

    /**
     * create and run ECS task definitions based on
     * the launch type
     *
     * @param context
     */
    public void createAndRunTaskDefinition(Context context) throws BaseException {
        this.registerTaskDefinition(context);

        RunTaskRequest runTaskRequest;
        String launchType = context.getLaunchType();
        if (launchType.equalsIgnoreCase(EC2)) {
            runTaskRequest = new RunTaskRequest()
                    .withLaunchType(LaunchType.EC2)
                    .withTaskDefinition("aoc-" + launchType)
                    .withCluster(GenericConstants.ECS_SIDECAR_CLUSTER.getVal())
                    .withCount(1);
        } else {
            runTaskRequest = new RunTaskRequest()
                    .withLaunchType(LaunchType.FARGATE)
                    .withTaskDefinition("aoc-" + launchType)
                    .withCluster(GenericConstants.ECS_SIDECAR_CLUSTER.getVal())
                    .withCount(1)
                    .withNetworkConfiguration(new NetworkConfiguration()
                            .withAwsvpcConfiguration(new AwsVpcConfiguration()
                                    .withAssignPublicIp(AssignPublicIp.ENABLED)
                                    .withSecurityGroups(context.getDefaultSecurityGrpId()) // sg-8c455fd7  sg-09e935a236100e514
                                    .withSubnets(context.getDefaultSubnets().get(0).getSubnetId())));
        }

        final RunTaskResult runTaskResult = ecsClient.runTask(runTaskRequest);

        runTaskResult.getTasks().forEach(task ->
                log.info("Successfully running task [{}]", task.getTaskArn()));

        if (!runTaskResult.getFailures().isEmpty()) {
            throw new BaseException(ExceptionCode.ECS_TASK_EXECUTION_FAIL, runTaskResult.getFailures().toString());
        }
    }

    public void cleanTasks(String clusterName) throws InterruptedException {
        // clean up tasks
        for (String taskArn : ecsClient.listTasks(new ListTasksRequest()
                .withCluster(clusterName)
                .withDesiredStatus(DesiredStatus.RUNNING))
                .getTaskArns()) {
            ecsClient.stopTask(new StopTaskRequest()
                    .withTask(taskArn)
                    .withCluster(clusterName)
            );
        }

        // wait for task clean
        int retryCount = Integer.parseInt(GenericConstants.MAX_RETRIES.getVal());
        while (retryCount-- > 0) {
            if (ecsClient.listTasks(new ListTasksRequest().withCluster(clusterName)
                    .withDesiredStatus(DesiredStatus.RUNNING))
                    .getTaskArns()
                    .isEmpty()) {
                return;
            }
            TimeUnit.SECONDS.sleep(Integer.parseInt(GenericConstants.SLEEP_IN_MILLISECONDS.getVal()));

            log.info("wait for task cleaning, {}/{}", retryCount, GenericConstants.MAX_RETRIES.getVal());
        }

        throw new RuntimeException("failed to wait task cleaning");
    }

    public void cleanCluster(String clusterName) {
        DeleteClusterRequest request = new DeleteClusterRequest().withCluster(clusterName);
        ecsClient.deleteCluster(request);
    }

    public void cleanTaskDefinitions() {
        ListTaskDefinitionsResult result = ecsClient.listTaskDefinitions();
        result.getTaskDefinitionArns().stream()
                .filter(arn -> arn.indexOf("aoc-") > 0)
                .forEach(arn -> ecsClient.deregisterTaskDefinition(
                        new DeregisterTaskDefinitionRequest().withTaskDefinition(arn)));
        log.info("result {}", result.getTaskDefinitionArns());

    }

    /**
     * check if ECS Cluster has available container instance
     * @param context
     * @throws Exception
     */
    public void waitForContainerInstanceRegistered(Context context) throws Exception {
        RetryHelper.retry(
                () -> {
                    Optional<Cluster> clusterOpt = describeCluster(GenericConstants.ECS_SIDECAR_CLUSTER.getVal());
                    if (!clusterOpt.isPresent()) {
                        log.warn("{} is not created",
                                GenericConstants.ECS_SIDECAR_CLUSTER.getVal());
                        throw new BaseException(ExceptionCode.ECS_CLUSTER_NOT_EXIST);
                    }
                    Cluster ecsCluster = clusterOpt.get();
                    if (ecsCluster.getRegisteredContainerInstancesCount() == 0) {
                        log.warn("waiting for ecs container instance to be ready - {}",
                                GenericConstants.ECS_SIDECAR_CLUSTER.getVal());
                        throw new BaseException(ExceptionCode.ECS_INSTANCE_NOT_READY);
                    }
                }
        );
    }

    public Optional<Cluster> describeCluster(String clusterName) {
        DescribeClustersRequest request = new DescribeClustersRequest()
                .withClusters(clusterName);
        DescribeClustersResult response = ecsClient.describeClusters(request);
        List<Cluster> clusters =  response.getClusters();
        if (clusters.isEmpty()) {
            return Optional.of(null);
        }
        return Optional.of(clusters.get(0));
    }

    public boolean isContainerInstanceAvail(String clusterName) {
        Optional<Cluster> clusterOpt = describeCluster(clusterName);
        if (!clusterOpt.isPresent()) {
            return false;
        }
        Cluster cluster = clusterOpt.get();
        return cluster.getRegisteredContainerInstancesCount() > 0;
    }
}
