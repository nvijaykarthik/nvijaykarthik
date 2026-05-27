package com.example.logmcp.service;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Service
public class LogErrorAggregationService {
    /**
     * Main API
     */
    public List<AggregatedError> scanAndAggregateErrors(
            String logFilePath
    ) throws Exception {
        List<LogErrorEvent> events =
                extractErrorEvents(logFilePath);
        return aggregateErrors(events);
    }
    /**
     * STEP 1
     * Extract multiline error blocks using awk
     */
    private List<LogErrorEvent> extractErrorEvents(
            String logFilePath
    ) throws Exception {
        List<LogErrorEvent> events = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder(
                "awk",
                "-f",
                "/opt/scripts/extract-errors.awk",
                logFilePath
        );
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     process.getInputStream()))) {
            String line;
            StringBuilder currentEvent = null;
            long startLine = -1;
            while ((line = reader.readLine()) != null) {
                // EVENT START
                if (line.startsWith("===EVENT_START===")) {
                    currentEvent = new StringBuilder();
                    String lineNumber =
                            line.replace(
                                    "===EVENT_START===",
                                    ""
                            ).trim();
                    startLine = Long.parseLong(lineNumber);
                    continue;
                }
                // EVENT END
                if (line.equals("===EVENT_END===")) {
                    if (currentEvent != null) {
                        String rawEvent =
                                currentEvent.toString();
                        String signature =
                                generateSignature(rawEvent);
                        events.add(
                                new LogErrorEvent(
                                        startLine,
                                        rawEvent,
                                        signature
                                )
                        );
                    }
                    currentEvent = null;
                    continue;
                }
                // EVENT BODY
                if (currentEvent != null) {
                    currentEvent
                            .append(line)
                            .append("\n");
                }
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException(
                    "awk execution failed. Exit code: "
                            + exitCode
            );
        }
        return events;
    }
    /**
     * STEP 2
     * Group common errors
     */
    private List<AggregatedError> aggregateErrors(
            List<LogErrorEvent> events
    ) {
        Map<String, List<LogErrorEvent>> grouped =
                events.stream()
                        .collect(Collectors.groupingBy(
                                LogErrorEvent::getSignature
                        ));
        List<AggregatedError> result =
                new ArrayList<>();
        for (Map.Entry<String, List<LogErrorEvent>> entry
                : grouped.entrySet()) {
            String signature = entry.getKey();
            List<LogErrorEvent> groupedEvents =
                    entry.getValue();
            int count = groupedEvents.size();
            LogErrorEvent sample =
                    groupedEvents.get(0);
            result.add(
                    new AggregatedError(
                            signature,
                            count,
                            sample.getStartLine(),
                            sample.getRawEvent()
                    )
            );
        }
        // Highest occurrence first
        result.sort((a, b) ->
                Integer.compare(
                        b.getCount(),
                        a.getCount()
                ));
        return result;
    }
    /**
     * Signature generation
     */
    private String generateSignature(
            String rawEvent
    ) {
        String exceptionClass =
                extractExceptionClass(rawEvent);
        List<String> stackFrames =
                extractTopStackFrames(rawEvent, 5);
        String normalizedMessage =
                normalizeMessage(rawEvent);
        String signatureBase =
                exceptionClass
                        + "|"
                        + normalizedMessage
                        + "|"
                        + String.join("|", stackFrames);
        return Integer.toHexString(
                signatureBase.hashCode()
        );
    }
    /**
     * Extract exception class
     */
    private String extractExceptionClass(
            String rawEvent
    ) {
        Pattern pattern = Pattern.compile(
                "([a-zA-Z0-9_.]+Exception|Error)"
        );
        Matcher matcher =
                pattern.matcher(rawEvent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "UNKNOWN";
    }
    /**
     * Extract top stack frames
     */
    private List<String> extractTopStackFrames(
            String rawEvent,
            int limit
    ) {
        List<String> frames =
                new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "\\s+at\\s+([a-zA-Z0-9_.$]+)"
        );
        Matcher matcher =
                pattern.matcher(rawEvent);
        while (matcher.find()
                && frames.size() < limit) {
            frames.add(matcher.group(1));
        }
        return frames;
    }
    /**
     * Normalize changing values
     */
    private String normalizeMessage(
            String rawEvent
    ) {
        return rawEvent
                // numbers
                .replaceAll("\\d+", "<NUM>")
                // UUID-ish values
                .replaceAll(
                        "[a-fA-F0-9\\-]{16,}",
                        "<ID>"
                )
                // collapse spaces
                .replaceAll("\\s+", " ")
                .trim();
    }
    /**
     * RAW ERROR EVENT
     */
    public static class LogErrorEvent {
        private final long startLine;
        private final String rawEvent;
        private final String signature;
        public LogErrorEvent(
                long startLine,
                String rawEvent,
                String signature
        ) {
            this.startLine = startLine;
            this.rawEvent = rawEvent;
            this.signature = signature;
        }
        public long getStartLine() {
            return startLine;
        }
        public String getRawEvent() {
            return rawEvent;
        }
        public String getSignature() {
            return signature;
        }
    }
    /**
     * AGGREGATED ERROR
     */
    public static class AggregatedError {
        private final String signature;
        private final int count;
        private final long sampleStartLine;
        private final String sampleEvent;
        public AggregatedError(
                String signature,
                int count,
                long sampleStartLine,
                String sampleEvent
        ) {
            this.signature = signature;
            this.count = count;
            this.sampleStartLine = sampleStartLine;
            this.sampleEvent = sampleEvent;
        }
        public String getSignature() {
            return signature;
        }
        public int getCount() {
            return count;
        }
        public long getSampleStartLine() {
            return sampleStartLine;
        }
        public String getSampleEvent() {
            return sampleEvent;
        }
    }
}

Required awk script:

/opt/scripts/extract-errors.awk

/^[0-9]{4}-[0-9]{2}-[0-9]{2}/ {
    if (block ~ /ERROR|Exception|Caused by/) {
        print "===EVENT_START===" startLine;
        print block;
        print "===EVENT_END===";
    }
    block = $0 "\n";
    startLine = NR;
    next;
}
{
    block = block $0 "\n";
}
END {
    if (block ~ /ERROR|Exception|Caused by/) {
        print "===EVENT_START===" startLine;
        print block;
        print "===EVENT_END===";
    }
}

Example usage:

List<AggregatedError> errors =
    service.scanAndAggregateErrors(
        "/logs/app.log"
    );

Example output:

[
  {
    "signature": "ab12cd",
    "count": 1532,
    "sampleStartLine": 10455,
    "sampleEvent": "2026-05-22 ERROR ..."
  },
  {
    "signature": "ff9911",
    "count": 1,
    "sampleStartLine": 887722,
    "sampleEvent": "Critical DB corruption ..."
  }
]