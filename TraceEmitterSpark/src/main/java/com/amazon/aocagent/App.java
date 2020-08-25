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
import java.util.Arrays;
import java.util.List;

import static spark.Spark.exception;
import static spark.Spark.get;

public class App {

  public static void main(String[] args) {
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

    get("/span1", (req, res) -> {
      String nextSpanId = makeHttpCall("http://localhost:4567/span2");
      return TracingContextUtils.getCurrentSpan().getContext().getSpanId().toLowerBase16() + "," + nextSpanId;
    });

    get("/span2", (req, res) -> {
      return TracingContextUtils.getCurrentSpan().getContext().getSpanId().toLowerBase16();
    });

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
