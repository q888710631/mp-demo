package com.honyee.app.config.http;

import com.honyee.app.config.Constants;
import com.honyee.app.utils.LogUtil;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate(Tracer tracer) {
        // BufferingClientHttpRequestFactory解决response body 不能重复读取的问题
        BufferingClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add(new TraceIdRequestInterceptor(tracer));
        return restTemplate;
    }

    /**
     * request中携带traceid
     */
    static class TraceIdRequestInterceptor implements ClientHttpRequestInterceptor {
        private final Tracer tracer;

        public TraceIdRequestInterceptor(Tracer tracer) {
            this.tracer = tracer;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            // sleuth已默认填充trace头部： X-B3-TraceId、X-B3-SpanId、X-B3-ParentSpanId、X-B3-Sampled
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                HttpHeaders headers = request.getHeaders();
                headers.add(Constants.TRACE_ID, currentSpan.context().traceId());
            }
            // 打印日志
            traceRequest(request, body);
            ClientHttpResponse response = execution.execute(request, body);
            traceResponse(response);
            return response;
        }

        private void traceRequest(HttpRequest request, byte[] body) throws IOException {
            String info = "\nrequest" +
                String.format("\n\tURI：%s", request.getURI()) +
                String.format("\n\tMethod：%s", request.getMethod()) +
                String.format("\n\tHeaders：%s", request.getHeaders()) +
                String.format("\n\tRequest body：%s", new String(body, StandardCharsets.UTF_8));
            LogUtil.info(info);
        }

        private void traceResponse(ClientHttpResponse response) throws IOException {
            StringBuilder inputStringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
            String line = bufferedReader.readLine();
            while (line != null) {
                inputStringBuilder.append(line);
                inputStringBuilder.append('\n');
                line = bufferedReader.readLine();
            }
            String info = "\nresponse" +
                String.format("\n\tStatus Code：%s", response.getStatusCode()) +
                String.format("\n\tStatus Text：%s", response.getStatusText()) +
                String.format("\n\tHeaders：%s", response.getHeaders()) +
                String.format("\n\tResponse body：%s", inputStringBuilder);
            LogUtil.info(info);
        }
    }
}
