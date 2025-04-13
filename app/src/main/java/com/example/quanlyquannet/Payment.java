package com.example.quanlyquannet;

public class Payment {
    private int id;
    private int usageLogId;
    private int totalCost;

    public Payment(int id, int usageLogId, int totalCost) {
        this.id = id;
        this.usageLogId = usageLogId;
        this.totalCost = totalCost;
    }

    // Getter và Setter (nếu cần)
    public int getId() {
        return id;
    }

    public int getUsageLogId() {
        return usageLogId;
    }

    public int getTotalCost() {
        return totalCost;
    }
}

