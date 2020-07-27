package com.amazon.aocagent.mustache.models;

import lombok.Data;

@Data
public class ExpectedMetricsTemplate extends MustacheTemplate {
  private String instanceId;
}
