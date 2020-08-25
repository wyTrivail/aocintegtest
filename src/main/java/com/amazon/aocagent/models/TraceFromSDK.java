package com.amazon.aocagent.models;

import lombok.Data;

import java.util.List;

@Data
public class TraceFromSDK {
  private String traceId;
  private List<String> spanIdList;
}
