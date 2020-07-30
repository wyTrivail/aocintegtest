package com.amazon.aocagent.mustache;

import com.amazon.aocagent.mustache.models.MustacheTemplate;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

@Log4j2
public class TemplateProvider {
  private MustacheFactory mustacheFactory = new DefaultMustacheFactory();

  /**
   * renderTemplate renders the mustache template base on the the class name of the template.
   *
   * @param template the template object, which will be used to replace the placeholders in the
   *     template
   * @return the rendered content
   * @throws IOException when no template is found
   */
  public String renderTemplate(MustacheTemplate template) throws IOException {
    String templateContent =
        IOUtils.toString(getClass().getResource(template.getTemplateFileName()));
    Mustache mustache =
        mustacheFactory.compile(new StringReader(templateContent), template.getTemplateFileName());
    StringWriter stringWriter = new StringWriter();
    mustache.execute(stringWriter, template).flush();
    return stringWriter.getBuffer().toString();
  }
}
