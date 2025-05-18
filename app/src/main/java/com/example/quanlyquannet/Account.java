package com.example.quanlyquannet;

public class Account {
    private String id;
    private String username;
    private long balance;

    public Account(String id, String username, long balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}