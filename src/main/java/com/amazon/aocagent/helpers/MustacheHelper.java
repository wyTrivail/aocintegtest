package com.amazon.aocagent.helpers;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

@Log4j2
public class MustacheHelper {
  private MustacheFactory mustacheFactory = new DefaultMustacheFactory();

  /**
   * Render the template file with injecting the data.
   * @param templateName templateName, Ex. EC2Config
   * @param dataToInject the object to inject to the template
   * @return generated content
   * @throws IOException when the template file is not existed
   */
  public String render(String templateName, Object dataToInject) throws IOException {
    String templateFileName = "/mustache/" + templateName + ".mustache";
    log.info("fetch config: {}", templateFileName);
    String templateContent =
        IOUtils.toString(getClass().getResource(templateFileName));
    Mustache mustache =
        mustacheFactory.compile(new StringReader(templateContent), templateFileName);
    StringWriter stringWriter = new StringWriter();
    mustache.execute(stringWriter, dataToInject).flush();
    return stringWriter.getBuffer().toString();
  }
}
