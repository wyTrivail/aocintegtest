package com.amazon.aocagent.mustache.models;

public class MustacheTemplate {
  public String getTemplateFileName() {
    return "/mustache/" + this.getClass().getSimpleName() + ".mustache";
  }
}
