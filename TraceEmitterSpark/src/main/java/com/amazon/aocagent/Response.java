package com.amazon.aocagent;

import java.util.List;

public class Response {
  private String traceId;
  private List<String> spanIdList;

  public Response(String traceId, List<String> spanIdList) {
    this.traceId = traceId;
    this.spanIdList = spanIdList;
  }

  public String getTraceId(){
    return traceId;
  }

  public List<String> getSpanIdList(){
    return spanIdList;
  }
}
