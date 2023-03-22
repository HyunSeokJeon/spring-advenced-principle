package hello.advanced.trace.hellotrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloTraceV2 {

    public static final String START_PREFIX = "-->";
    public static final String COMPLETE_PREFIX = "<--";
    public static final String EX_PREFIX = "<X-";

    public TraceStatus begin(String message) {
        TraceId traceId = new TraceId();
        long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}",
                traceId.getId(),
                addSpace(START_PREFIX, traceId.getLevel()),
                message);
        return new TraceStatus(traceId, startTimeMs, message);
    }

    //V2에서 추가
    public TraceStatus beginSync(TraceId traceId, String message) {
        TraceId nextId = traceId.createNextId();
        long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}",
                nextId.getId(),
                addSpace(START_PREFIX, nextId.getLevel()),
                message);
        return new TraceStatus(nextId, startTimeMs, message);
    }

    public void end(TraceStatus status) {
        complete(status, null);
    }

    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        long stopTimeMs = System.currentTimeMillis();
        TraceId traceId = status.getTraceId();
        long allTime = stopTimeMs - status.getStartTimeMs();
        if (e == null) {
            log.info("[{}] {}{} time={}ms",
                    traceId.getId(),
                    addSpace(COMPLETE_PREFIX, traceId.getLevel()),
                    status.getMessage(),
                    allTime
            );

        } else {
            log.info("[{}] {}{} time={}ms ex={}",
                    traceId.getId(),
                    addSpace(EX_PREFIX, traceId.getLevel()),
                    status.getMessage(),
                    allTime,
                    e.toString()
            );
        }
    }

    private String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            boolean isLast = i == level - 1;
            sb.append(isLast ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }

}
