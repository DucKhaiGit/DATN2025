package com.example.quanlyquannet;

public class Computer {
    private int id;
    private String code;
    private String cpu;
    private String ram;
    private String gpu;
    private String storage;
    private String status; // Tên trạng thái
    private String dateAdded;

    public Computer(int id, String code, String cpu, String ram, String gpu, String storage, String status, String dateAdded) {
        this.id = id;
        this.code = code;
        this.cpu = cpu;
        this.ram = ram;
        this.gpu = gpu;
        this.storage = storage;
        this.status = status;
        this.dateAdded = dateAdded;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getCpu() {
        return cpu;
    }

    public String getRam() {
        return ram;
    }

    public String getGpu() {
        return gpu;
    }

    public String getStorage() {
        return storage;
    }

    public String getStatus() {
        return status;
    }

    public String getDateAdded() {
        return dateAdded;
    }



    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Computer{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", cpu='" + cpu + '\'' +
                ", ram='" + ram + '\'' +
                ", gpu='" + gpu + '\'' +
                ", storage='" + storage + '\'' +
                ", status='" + status + '\'' +
                ", dateAdded='" + dateAdded + '\'' +
                '}';
    }
}
