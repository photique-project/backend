package com.benchpress200.photique.common.security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);

        // multipart 요청에서는 바디를 소모하지 않지만 getParts를 통해서 간접적으로 getInputStream을 직접 호출하므로 캐싱요청객체가 무시될 수 있음
        if (request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
            this.cachedBody = new byte[0];
            return;
        }

        try (BufferedReader reader = request.getReader()) {
            StringBuilder bodyStringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                bodyStringBuilder.append(line).append("\n");
            }
            this.cachedBody = bodyStringBuilder.toString().trim().getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }
}
