package com.amazon.aocagent.services;

import com.amazon.aocagent.enums.GenericConstants;
import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.Cluster;
import com.amazonaws.services.ecs.model.CreateClusterRequest;
import com.amazonaws.services.ecs.model.CreateClusterResult;
import com.amazonaws.services.ecs.model.DeleteClusterRequest;
import com.amazonaws.services.ecs.model.DeregisterTaskDefinitionRequest;
import com.amazonaws.services.ecs.model.DescribeClustersRequest;
import com.amazonaws.services.ecs.model.DescribeClustersResult;
import com.amazonaws.services.ecs.model.DesiredStatus;
import com.amazonaws.services.ecs.model.ListTaskDefinitionsResult;
import com.amazonaws.services.ecs.model.ListTasksRequest;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionRequest;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionResult;
import com.amazonaws.services.ecs.model.RunTaskRequest;
import com.amazonaws.services.ecs.model.RunTaskResult;
import com.amazonaws.services.ecs.model.StopTaskRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ECSService {
  private AmazonECS ecsClient;
  private ObjectMapper jsonMapper;

  public ECSService(final String region) {
    this.ecsClient = AmazonECSClientBuilder.standard().withRegion(region).build();
    this.jsonMapper = new ObjectMapper();
  }

  /**
   * create ECS cluster for integ testing.
   * @return cluster creation result
   */
  public CreateClusterResult createCluster(final String clusterName) {
    CreateClusterRequest request =
        new CreateClusterRequest().withClusterName(clusterName);
    log.info("creating ECS cluster: {}", clusterName);
    return ecsClient.createCluster(request);
  }

  /**
   * create and register ecs task definition for both EC2 and fargate running mode.
   * @param taskDef ecs task definition
   * @return {@link RegisterTaskDefinitionResult} for registering ecs container defs
   */
  public RegisterTaskDefinitionResult registerTaskDefinition(String taskDef) throws BaseException {
    RegisterTaskDefinitionRequest request;

    try {
      request = jsonMapper.readValue(taskDef, RegisterTaskDefinitionRequest.class);
    } catch (Exception e) {
      throw new BaseException(ExceptionCode.ECS_TASK_EXECUTION_FAIL, e.getMessage());
    }
    return ecsClient.registerTaskDefinition(request);
  }

  /**
   * create and run ECS task definitions based on the launch type.
   * @param runTaskRequest ecs run task request
   */
  public void runTaskDefinition(RunTaskRequest runTaskRequest) throws BaseException {

    final RunTaskResult runTaskResult = ecsClient.runTask(runTaskRequest);
    runTaskResult
        .getTasks()
        .forEach(task -> log.info("Successfully running task [{}]", task.getTaskArn()));

    if (!runTaskResult.getFailures().isEmpty()) {
      throw new BaseException(
          ExceptionCode.ECS_TASK_EXECUTION_FAIL, runTaskResult.getFailures().toString());
    }
  }

  /**
   * clean ECS tasks resources.
   * @param clusterName cluster name
   * @throws InterruptedException fail to clean exception
   */
  public void cleanTasks(String clusterName) throws InterruptedException {
    // clean up tasks
    for (String taskArn :
        ecsClient
            .listTasks(
                new ListTasksRequest()
                    .withCluster(clusterName)
                    .withDesiredStatus(DesiredStatus.RUNNING))
            .getTaskArns()) {
      ecsClient.stopTask(new StopTaskRequest().withTask(taskArn).withCluster(clusterName));
    }

    // wait for task clean
    int retryCount = Integer.parseInt(GenericConstants.MAX_RETRIES.getVal());
    while (retryCount-- > 0) {
      if (ecsClient
          .listTasks(
              new ListTasksRequest()
                  .withCluster(clusterName)
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

  /**
   * clean ECS cluster.
   * @param clusterName cluster name
   */
  public void cleanCluster(String clusterName) {
    DeleteClusterRequest request = new DeleteClusterRequest().withCluster(clusterName);
    ecsClient.deleteCluster(request);
  }

  /**
   * clean ECS tasks with prefix.
   * @param prefix task prefix
   */
  public void cleanTaskDefinitions(String prefix) {
    ListTaskDefinitionsResult result = ecsClient.listTaskDefinitions();
    result.getTaskDefinitionArns().stream()
        .filter(arn -> arn.indexOf(prefix) > 0)
        .forEach(
            arn ->
                ecsClient.deregisterTaskDefinition(
                    new DeregisterTaskDefinitionRequest().withTaskDefinition(arn)));
    log.info("result {}", result.getTaskDefinitionArns());
  }

  /**
   * check if ECS Cluster has available container instance.
   * @param clusterName cluster name
   * @throws Exception fail to wait for container instance ready
   */
  public void waitForContainerInstanceRegistered(String clusterName) throws Exception {
    RetryHelper.retry(
        () -> {
          Optional<Cluster> clusterOpt = describeCluster(clusterName);
          if (!clusterOpt.isPresent()) {
            log.warn("{} is not created", clusterName);
            throw new BaseException(ExceptionCode.ECS_CLUSTER_NOT_EXIST);
          }
          Cluster ecsCluster = clusterOpt.get();
          if (ecsCluster.getRegisteredContainerInstancesCount() == 0) {
            log.warn(
                "waiting for ecs container instance to be ready - {}", clusterName);
            throw new BaseException(ExceptionCode.ECS_INSTANCE_NOT_READY);
          }
        });
  }

  /**
   * describe ECS cluster.
   * @param clusterName cluster name
   * @return
   */
  public Optional<Cluster> describeCluster(String clusterName) {
    DescribeClustersRequest request = new DescribeClustersRequest().withClusters(clusterName);
    DescribeClustersResult response = ecsClient.describeClusters(request);
    List<Cluster> clusters = response.getClusters();
    if (clusters.isEmpty()) {
      return Optional.of(null);
    }
    return Optional.of(clusters.get(0));
  }

  /**
   * check if container instances are ready for using.
   * @param clusterName cluster name
   * @return
   */
  public boolean isContainerInstanceAvail(String clusterName) {
    Optional<Cluster> clusterOpt = describeCluster(clusterName);
    if (!clusterOpt.isPresent()) {
      return false;
    }
    Cluster cluster = clusterOpt.get();
    return cluster.getRegisteredContainerInstancesCount() > 0;
  }
}
