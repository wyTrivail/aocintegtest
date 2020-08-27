package com.amazon.aocagent;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class Response {
  private String traceId;
  private List<String> spanIdList;

  public Response(String traceId, List<String> spanIdList) {
    this.traceId = traceId;
    this.spanIdList = spanIdList;
  }

  public String toJson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper(new JsonFactory());
    return mapper.writeValueAsString(this);

  }

  public String getTraceId(){
    return traceId;
  }

  public List<String> getSpanIdList(){
    return spanIdList;
  }


}
