package com.example.quanlyquannet;

public class Computer {
    private int id;
    private String code;
    private String cpu;
    private String ram;
    private String gpu;
    private String storage;
    private String status;
    private String macAddress;
    private String ipAddress;
    private String dateAdded;

    public Computer(int id, String code, String cpu, String ram, String gpu, String storage, String status, String macAddress, String ipAddress, String dateAdded) {
        this.id = id;
        this.code = code;
        this.cpu = cpu;
        this.ram = ram;
        this.gpu = gpu;
        this.storage = storage;
        this.status = status;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.dateAdded = dateAdded;
    }

    // Getters và Setters
    public int getId() { return id; }
    public String getCode() { return code; }
    public String getCpu() { return cpu; }
    public String getRam() { return ram; }
    public String getGpu() { return gpu; }
    public String getStorage() { return storage; }
    public String getStatus() { return status; }
    public String getMacAddress() { return macAddress; }
    public String getIpAddress() { return ipAddress; }
    public String getDateAdded() { return dateAdded; }

    public void setId(int id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setCpu(String cpu) { this.cpu = cpu; }
    public void setRam(String ram) { this.ram = ram; }
    public void setGpu(String gpu) { this.gpu = gpu; }
    public void setStorage(String storage) { this.storage = storage; }
    public void setStatus(String status) { this.status = status; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }
}