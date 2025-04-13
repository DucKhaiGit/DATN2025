package com.example.quanlyquannet;

public class UsageLog {
    private int id;
    private int computerId;
    private long startTime;
    private long endTime;

    public UsageLog(int id, int computerId, long startTime, long endTime) {
        this.id = id;
        this.computerId = computerId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public int getComputerId() {
        return computerId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
