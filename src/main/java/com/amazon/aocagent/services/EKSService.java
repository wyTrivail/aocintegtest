package com.amazon.aocagent.services;

import com.amazon.aocagent.fileconfigs.EksKubeConfigTemplate;
import com.amazon.aocagent.helpers.MustacheHelper;
import com.amazon.aocagent.models.Context;
import com.amazonaws.services.eks.AmazonEKS;
import com.amazonaws.services.eks.AmazonEKSClient;
import com.amazonaws.services.eks.model.Cluster;
import com.amazonaws.services.eks.model.DescribeClusterRequest;
import com.amazonaws.services.eks.model.DescribeClusterResult;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

@Log4j2
public class EKSService {
  private AmazonEKS eksClient;

  public EKSService(final String region) {
    this.eksClient = AmazonEKSClient.builder().withRegion(region).build();
  }

  /**
   * generate kubernetes client.
   *
   * @param context test context
   */
  public ApiClient generateKubeClient(Context context) throws IOException {
    // composite kubeConfig
    Cluster cluster = getCluster(context);
    context.setEksCertificate(cluster.getCertificateAuthority().getData());
    context.setEksEndpoint(cluster.getEndpoint());
    String kubeConfigContent =
        new MustacheHelper().render(EksKubeConfigTemplate.KUBE_CONFIG_TEMPLATE, context);

    log.info("kubeConfigContent: \n" + kubeConfigContent);

    // generate kubernetes client
    KubeConfig config = KubeConfig.loadKubeConfig(new StringReader(kubeConfigContent));
    // "KubeConfig" class need a "file path" of kubeconfig to resolve the path of
    // aws-iam-authenticator, otherwise a NullPointerException will be raised.
    // This is a workaround. As aws-iam-authenticator is specified with absolute path in our
    // kubeconfig, it doesn't matter which "file path" of kubeconfig is set.
    config.setFile(new File(this.getClass().getResource("/").getPath()));

    return ClientBuilder.kubeconfig(config).build();
  }

  private Cluster getCluster(Context context) {
    DescribeClusterRequest describeClusterRequest =
        new DescribeClusterRequest().withName(context.getEksClusterName());
    DescribeClusterResult describeClusterResult = eksClient.describeCluster(describeClusterRequest);
    if (null == describeClusterResult.getCluster()) {
      throw new RuntimeException("eks cluster doesn't exist: ");
    }
    return describeClusterResult.getCluster();
  }
}
