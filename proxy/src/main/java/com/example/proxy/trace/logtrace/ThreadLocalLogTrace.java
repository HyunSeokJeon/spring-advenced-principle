package com.example.proxy.trace.logtrace;

import com.example.proxy.trace.TraceId;
import com.example.proxy.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadLocalLogTrace implements LogTrace {


    public static final String START_PREFIX = "-->";
    public static final String COMPLETE_PREFIX = "<--";
    public static final String EX_PREFIX = "<X-";

    /**
     * ThreadLocal 사용하는 경우 주의해야할 점
     * Thread 생성비용은 비싸기 때문에 WAS에서는 Thread Pool 재사용하는 방식을 사용
     * Thread Pool User-A Thread-A 사용 후, 값을 지워주지 않으면 User-B Thread-A 사용 시, User-A의 정보가 노출될 가능성 있음
     */
    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
        long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}",
                traceId.getId(),
                addSpace(START_PREFIX, traceId.getLevel()),
                message);
        return new TraceStatus(traceId, startTimeMs, message);
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
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

        releaseTraceId();
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove();
        } else {
            traceIdHolder.set(traceId.createPreviousId());
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
