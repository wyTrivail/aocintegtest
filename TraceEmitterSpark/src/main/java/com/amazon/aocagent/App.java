package com.amazon.aocagent;

import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.TracingContextUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
<<<<<<< HEAD
import java.util.List;
import java.util.Random;

import static spark.Spark.*;

public class App {
  static final String DEFAULT_OTLP_ENDPOINT = "localhost:55680";
  static final String REQUEST_START_TIME = "requestStartTime";

  private static MetricEmitter buildMetricEmitter(){
    String otlpEndpoint = DEFAULT_OTLP_ENDPOINT;
    // get otlp endpoint
    String otlpEndpointFromEnvVar = System.getenv("OTEL_OTLP_ENDPOINT");
    if(otlpEndpointFromEnvVar != null && !otlpEndpointFromEnvVar.trim().equals("")){
      otlpEndpoint = otlpEndpointFromEnvVar;
    }

    return new MetricEmitter(otlpEndpoint);

  }
  public static void main(String[] args) {
    MetricEmitter metricEmitter = buildMetricEmitter();

=======
import java.util.Arrays;
import java.util.List;

import static spark.Spark.exception;
import static spark.Spark.get;

public class App {

  public static void main(String[] args) {
>>>>>>> 41f16578cb61672e2447f0eded973d8f1937a57e
    get("/span0", (req, res) -> {
      Span currentSpan = TracingContextUtils.getCurrentSpan();
      List<String> spanList = new ArrayList<>();
      spanList.add(currentSpan.getContext().getSpanId().toLowerBase16());

      String spans = makeHttpCall("http://localhost:4567/span1");
      for(String spanId: spans.split(",")){
        spanList.add(spanId);
      }

      String traceId = currentSpan.getContext().getTraceId().toLowerBase16();
      String xrayTraceId = "1-" + traceId.substring(0, 8)
          + "-" + traceId.substring(8);

      Response response = new Response(xrayTraceId, spanList);
      return response;
    }, new JsonTransformer());

<<<<<<< HEAD
    get("/span400", (req, res) -> {
      res.status(400);
      return "params error";
    });

    get("/span500", (req, res) -> {
      res.status(500);
      return "internal error";
    });

=======
>>>>>>> 41f16578cb61672e2447f0eded973d8f1937a57e
    get("/span1", (req, res) -> {
      String nextSpanId = makeHttpCall("http://localhost:4567/span2");
      return TracingContextUtils.getCurrentSpan().getContext().getSpanId().toLowerBase16() + "," + nextSpanId;
    });

    get("/span2", (req, res) -> {
      return TracingContextUtils.getCurrentSpan().getContext().getSpanId().toLowerBase16();
    });

<<<<<<< HEAD
    /**
     * record a start time for each request
     */
    before((req, res) -> {
      req.attribute(REQUEST_START_TIME, System.currentTimeMillis());
    });

    after((req, res) -> {
      String statusCode = String.valueOf(res.status());
      // calculate return time
      Long requestStartTime = req.attribute(REQUEST_START_TIME);
      metricEmitter.emitReturnTimeMetric(
          System.currentTimeMillis()- requestStartTime,
          req.pathInfo(),
          statusCode
      );
    });

=======
>>>>>>> 41f16578cb61672e2447f0eded973d8f1937a57e
    exception(Exception.class, (exception, request, response) -> {
      // Handle the exception here
      exception.printStackTrace();
    });
  }

  private static String makeHttpCall(String url) throws IOException {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
      HttpGet httpget = new HttpGet(url);

      System.out.println("Executing request " + httpget.getRequestLine());

      // Create a custom response handler
      ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

        @Override
        public String handleResponse(
            final HttpResponse response) throws ClientProtocolException, IOException {
          int status = response.getStatusLine().getStatusCode();
          if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
          } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
          }
        }

      };
      String responseBody = httpclient.execute(httpget, responseHandler);
      System.out.println("----------------------------------------");
      System.out.println(responseBody);
      return responseBody;
    } finally {
      httpclient.close();
    }
  }


}
