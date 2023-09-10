package com.example.proxy.pureproxy.proxy.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class CacheProxy implements Subject{

    private Subject target;
    private String cacheValue;

    public CacheProxy(Subject target) {
        this.target = target;
    }

    @Override
    public String operation() {
        log.info("프록시 호출");
        if (!StringUtils.hasLength(cacheValue)) {
            cacheValue = target.operation();
        }
        return cacheValue;
    }
}
