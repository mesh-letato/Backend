package com.pinmoa.link.link.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinmoa.link.global.exception.LinkProcessingException;
import com.pinmoa.link.link.dto.VideoMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class YtDlpVideoMetadataExtractor implements VideoMetadataExtractor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String binary;
    private final long timeoutSeconds;

    public YtDlpVideoMetadataExtractor(
        @Value("${ytdlp.binary}") String binary,
        @Value("${ytdlp.timeout-seconds}") long timeoutSeconds
    ) {
        this.binary = binary;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public VideoMetadata extract(String url) {
        Process process = startProcess(url);

        StringBuffer[] streams = readStreamsAsync(process);
        waitFor(process, url);

        String stdout = streams[0].toString();
        String stderr = streams[1].toString();

        if (process.exitValue() != 0) {
            log.error("yt-dlp stderr: {}", stderr);
            throw new LinkProcessingException(
                "yt-dlp 실행이 실패했습니다. (exit=" + process.exitValue() + ") " + stderr.lines().findFirst().orElse(""));
        }
        return parse(stdout);
    }

    private Process startProcess(String url) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                binary,
                "--dump-json",
                "--no-playlist",
                "--no-warnings",
                url
            );
            pb.redirectErrorStream(false);
            return pb.start();
        } catch (Exception e) {
            throw new LinkProcessingException("yt-dlp 실행을 시작할 수 없습니다. 바이너리 설치 여부를 확인하세요.", e);
        }
    }

    private StringBuffer[] readStreamsAsync(Process process) {
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();

        Thread outThread = new Thread(() -> {
            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                r.lines().forEach(line -> stdout.append(line).append("\n"));
            } catch (Exception ignored) {}
        });
        Thread errThread = new Thread(() -> {
            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                r.lines().forEach(line -> stderr.append(line).append("\n"));
            } catch (Exception ignored) {}
        });

        outThread.start();
        errThread.start();

        try {
            outThread.join();
            errThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return new StringBuffer[]{stdout, stderr};
    }

    private void waitFor(Process process, String url) {
        try {
            if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new LinkProcessingException("yt-dlp 실행이 시간 초과되었습니다: " + url);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            throw new LinkProcessingException("yt-dlp 실행이 중단되었습니다.", e);
        }
    }

    private VideoMetadata parse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            return new VideoMetadata(
                root.path("title").asText(null),
                root.path("description").asText(null),
                root.path("uploader").asText(null),
                root.path("webpage_url").asText(null)
            );
        } catch (Exception e) {
            throw new LinkProcessingException("yt-dlp JSON 파싱에 실패했습니다.", e);
        }
    }
}
