package com.example.capstone25_2.memo;

import jdk.jfr.EventType;
import lombok.Getter;

@Getter
public class MemoEvent {
    private final Memo memo;
    private final EventType eventType;

    public MemoEvent(Memo memo, EventType eventType) {
        this.memo = memo;
        this.eventType = eventType;
    }

    public enum EventType {
        CREATED,
        UPDATED
    }
}
