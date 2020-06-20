package com.lessons.onespring.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MultipartFormData {

    public static class Builder {

        private String boundary;
        private Charset charset = StandardCharsets.UTF_8;
        private List<MimeFile> files = new ArrayList<>();
        private Map<String, String> texts = new LinkedHashMap<>();

        private Builder() {
            this.boundary = new BigInteger(128, new Random()).toString();
        }

        public Builder withCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder withBoundary(String boundary) {
            this.boundary = boundary;
            return this;
        }

        public Builder addFile(String name, ByteArrayOutputStream byteArrayOutputStream, String mimeType) {
            this.files.add(new MimeFile(name, byteArrayOutputStream, mimeType));
            return this;
        }

        public Builder addText(String name, String text) {
            texts.put(name, text);
            return this;
        }

        public MultipartFormData build() throws IOException {
            MultipartFormData multipartFormData = new MultipartFormData();
            multipartFormData.boundary = boundary;

            var newline = "\r\n".getBytes(charset);
            var byteArrayOutputStream = new ByteArrayOutputStream();
            for (MimeFile f : files) {
                byteArrayOutputStream.write(("--" + boundary).getBytes(charset));
                byteArrayOutputStream.write(newline);
                byteArrayOutputStream.write(("Content-Disposition: form-data; name=\"" + f.name + "\"; filename=\"" + "tmp.jpeg" + "\"").getBytes(charset));
                byteArrayOutputStream.write(newline);
                byteArrayOutputStream.write(("Content-Type: " + f.mimeType).getBytes(charset));
                byteArrayOutputStream.write(newline);
                byteArrayOutputStream.write(newline);
                byteArrayOutputStream.write(f.byteArrayOutputStream.toByteArray());
                byteArrayOutputStream.write(newline);
            }
            for (var entry: texts.entrySet()) {
                byteArrayOutputStream.write(("--" + boundary).getBytes(charset));
                byteArrayOutputStream.write(newline);
                byteArrayOutputStream.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"").getBytes(charset));
                byteArrayOutputStream.write(newline);
                byteArrayOutputStream.write(newline);
                byteArrayOutputStream.write(entry.getValue().getBytes(charset));
                byteArrayOutputStream.write(newline);
            }
            byteArrayOutputStream.write(("--" + boundary + "--").getBytes(charset));

            multipartFormData.bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(byteArrayOutputStream.toByteArray());
            return multipartFormData;
        }

        public class MimeFile {

            public final String name;
            public final ByteArrayOutputStream byteArrayOutputStream;
            public final String mimeType;

            public MimeFile(String name, ByteArrayOutputStream byteArrayOutputStream, String mimeType) {
                this.name = name;
                this.byteArrayOutputStream = byteArrayOutputStream;
                this.mimeType = mimeType;
            }
        }
    }

    private String boundary;
    private HttpRequest.BodyPublisher bodyPublisher;

    private MultipartFormData() {
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public HttpRequest.BodyPublisher getBodyPublisher() throws IOException {
        return bodyPublisher;
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

}