package com.example.quanlyquannet;

public class Employee {
    private String id;
    private String name;
    private String role;
    private long salary;

    public Employee(String id, String name, String role, long salary) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public long getSalary() {
        return salary;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }
}