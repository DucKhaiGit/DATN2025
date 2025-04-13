package com.example.quanlyquannet;

public class Employee {
    private int id;  // ID của nhân viên
    private String name;  // Tên nhân viên
    private String phone;  // Số điện thoại
    private int roleId;  // ID vai trò
    private int shiftId;  // ID ca làm việc

    // Constructor
    public Employee(int id, String name, String phone, int roleId, int shiftId) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.roleId = roleId;
        this.shiftId = shiftId;
    }

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public int getShiftId() { return shiftId; }
    public void setShiftId(int shiftId) { this.shiftId = shiftId; }
}

