package com.amazon.aocagent.services;

import com.amazonaws.services.xray.AWSXRay;
import com.amazonaws.services.xray.AWSXRayClientBuilder;
import com.amazonaws.services.xray.model.BatchGetTracesRequest;
import com.amazonaws.services.xray.model.BatchGetTracesResult;
import com.amazonaws.services.xray.model.Trace;

import java.util.List;

public class XRayService {
  private AWSXRay awsxRay;

  public XRayService(String region) {
    awsxRay = AWSXRayClientBuilder.standard().withRegion(region).build();
  }

  /**
   * List trace objects by ids.
   * @param traceIdList trace id list
   * @return trace object list
   */
  public List<Trace> listTraceByIds(List<String> traceIdList) {
    BatchGetTracesResult batchGetTracesResult =
        awsxRay.batchGetTraces(new BatchGetTracesRequest().withTraceIds(traceIdList));

    return batchGetTracesResult.getTraces();
  }
}
