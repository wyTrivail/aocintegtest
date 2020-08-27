package com.amazon.aocagent.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TraceFromEmitter implements Serializable {
  private String traceId;
  private List<String> spanIdList;
}
