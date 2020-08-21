package com.amazon.aocagent.validators;

import com.amazon.aocagent.exception.BaseException;
import com.amazon.aocagent.exception.ExceptionCode;
import com.amazon.aocagent.helpers.MustacheHelper;
import com.amazon.aocagent.helpers.RetryHelper;
import com.amazon.aocagent.models.Context;
import com.amazon.aocagent.services.XRayService;
import com.amazonaws.services.xray.model.Segment;
import com.amazonaws.services.xray.model.Trace;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Log4j2
public class TraceValidator implements IValidator {
  private MustacheHelper mustacheHelper = new MustacheHelper();
  private static int MAX_RETRY_COUNT = 60;

  @Override
  public void validate(Context context) throws Exception {
    XRayService xrayService = new XRayService(context.getStack().getTestingRegion());
    List<Trace> expectedTraceList = this.getExpectedTrace(context);
    expectedTraceList.sort(Comparator.comparing(Trace::getId));

    RetryHelper.retry(
        MAX_RETRY_COUNT,
        () -> {
          List<Trace> traceList =
              xrayService.listTraceByIds(Arrays.asList(context.getExpectedTraceId()));
          traceList.sort(Comparator.comparing(Trace::getId));

          log.info("expectedTraceList: {}", expectedTraceList);
          log.info("traceList got from backend: {}", traceList);
          if (expectedTraceList.size() != traceList.size()) {
            throw new BaseException(ExceptionCode.TRACE_LIST_NOT_MATCHED);
          }

          for (int i = 0; i != expectedTraceList.size(); ++i) {
            compareTwoTraces(expectedTraceList.get(i), traceList.get(i));
          }
        });
  }

  private List<Trace> getExpectedTrace(Context context) throws IOException {
    String yamlExpectedTrace = mustacheHelper.render(context.getExpectedTrace(), context);

    // load metrics from yaml
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    List<Trace> expectedTraceList =
        mapper.readValue(
            yamlExpectedTrace.getBytes(StandardCharsets.UTF_8), new TypeReference<>() {});

    return expectedTraceList;
  }

  private void compareTwoTraces(Trace trace1, Trace trace2) throws BaseException {
    // check trace id
    if (!trace1.getId().equals(trace2.getId())) {
      throw new BaseException(ExceptionCode.TRACE_ID_NOT_MATCHED);
    }

    if (trace1.getSegments().size() != trace2.getSegments().size()) {
      throw new BaseException(ExceptionCode.TRACE_SPAN_LIST_NOT_MATCHED);
    }
    trace1.getSegments().sort(Comparator.comparing(Segment::getId));
    trace2.getSegments().sort(Comparator.comparing(Segment::getId));

    for (int i = 0; i != trace1.getSegments().size(); ++i) {
      // check span id
      if (!trace1.getSegments().get(i).getId().equals(trace2.getSegments().get(i).getId())) {
        throw new BaseException(ExceptionCode.TRACE_SPAN_NOT_MATCHED);
      }
    }
  }
}
