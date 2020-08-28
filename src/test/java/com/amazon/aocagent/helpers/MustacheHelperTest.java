package com.amazon.aocagent.helpers;

import com.amazon.aocagent.fileconfigs.FileConfig;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.models.Stack;
import org.junit.Test;

import java.io.IOException;

public class MustacheHelperTest {

  @Test
  public void testMustache() throws IOException {
    MustacheHelper mustacheHelper = new MustacheHelper();
    FileConfig fileConfig = new FileConfig() {
      @Override
      public String getPath() {
        return "/test.mustache";
      }
    };

    Context context = new Context();
    Stack stack = new Stack();
    stack.setTraceDataS3BucketName("test");
    context.setStack(stack);

    System.out.println(mustacheHelper.render(fileConfig, context));
  }
}
