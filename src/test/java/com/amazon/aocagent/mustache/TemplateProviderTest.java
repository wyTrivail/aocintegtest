package com.amazon.aocagent.mustache;

import com.amazon.aocagent.mustache.models.EC2ConfigTemplate;
import org.junit.Test;
import java.io.IOException;

public class TemplateProviderTest {

  @Test
  public void testTemplateCouldBeFound() throws IOException {
    System.out.println("####################################");
    System.out.println(new EC2ConfigTemplate().getTemplateFileName());
    TemplateProvider templateProvider = new TemplateProvider();
    templateProvider.renderTemplate(new EC2ConfigTemplate());
  }
}
