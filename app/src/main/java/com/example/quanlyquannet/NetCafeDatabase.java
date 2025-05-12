package com.example.quanlyquannet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetCafeDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NetCafeDB";
    private static final int DATABASE_VERSION = 2;

    public NetCafeDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bảng Máy tính
        db.execSQL("CREATE TABLE Computer (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT UNIQUE NOT NULL, " +
                "cpu TEXT NOT NULL, " +
                "ram TEXT NOT NULL, " +
                "gpu TEXT NOT NULL, " +
                "storage TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "macAddress TEXT, " +
                "ipAddress TEXT, " +
                "inventoryId INTEGER, " +
                "dateAdded TEXT NOT NULL, " +
                "FOREIGN KEY (inventoryId) REFERENCES Inventory(id))");

        // Bảng Nhân viên
        db.execSQL("CREATE TABLE Employee (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "employeeCode TEXT UNIQUE NOT NULL, " +
                "name TEXT NOT NULL, " +
                "phone TEXT NOT NULL, " +
                "roleId INTEGER, " +
                "shiftId INTEGER, " +
                "FOREIGN KEY (roleId) REFERENCES Role(id), " +
                "FOREIGN KEY (shiftId) REFERENCES Shift(id))");

        // Bảng Vai trò
        db.execSQL("CREATE TABLE Role (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "roleName TEXT UNIQUE NOT NULL)");

        // Thêm dữ liệu mẫu vào bảng Role
        db.execSQL("INSERT INTO Role (roleName) VALUES ('Quản lý'), ('Kỹ thuật viên'), ('Lễ tân'), ('Nhân viên vệ sinh'), ('Phục vụ')");

        // Bảng Ca làm việc
        db.execSQL("CREATE TABLE Shift (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "shiftTime TEXT UNIQUE NOT NULL)");

        // Thêm dữ liệu mẫu vào bảng Shift
        db.execSQL("INSERT INTO Shift (shiftTime) VALUES ('Ca sáng'), ('Ca chiều'), ('Ca tối'), ('Cả ngày'), ('Ca linh hoạt')");

        // Thêm máy tính mẫu với thông tin WoL
        ContentValues computerValues = new ContentValues();
        computerValues.put("code", "PC-001");
        computerValues.put("cpu", "Intel Core i5-10400");
        computerValues.put("ram", "16GB DDR4");
        computerValues.put("gpu", "NVIDIA GTX 1660 Super");
        computerValues.put("storage", "512GB SSD + 1TB HDD");
        computerValues.put("status", "available");
        computerValues.put("macAddress", "408D5C1C5754");
        computerValues.put("ipAddress", "192.168.1.202");
        computerValues.put("dateAdded", "2025-05-10");
        db.insert("Computer", null, computerValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE Computer ADD COLUMN macAddress TEXT");
            db.execSQL("ALTER TABLE Computer ADD COLUMN ipAddress TEXT");

            ContentValues computerValues = new ContentValues();
            computerValues.put("code", "PC-001");
            computerValues.put("cpu", "Intel Core i5-10400");
            computerValues.put("ram", "16GB DDR4");
            computerValues.put("gpu", "NVIDIA GTX 1660 Super");
            computerValues.put("storage", "512GB SSD + 1TB HDD");
            computerValues.put("status", "available");
            computerValues.put("macAddress", "408D5C1C5754");
            computerValues.put("ipAddress", "192.168.1.202");
            computerValues.put("dateAdded", "2025-05-10");
            db.insert("Computer", null, computerValues);
        }
    }

    // Phương thức đồng bộ dữ liệu từ SQLite sang Firestore
    public void syncComputersToFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ArrayList<Computer> computers = getAllComputers();

        for (Computer computer : computers) {
            Map<String, Object> computerData = new HashMap<>();
            computerData.put("id", computer.getId());
            computerData.put("code", computer.getCode());
            computerData.put("cpu", computer.getCpu());
            computerData.put("ram", computer.getRam());
            computerData.put("gpu", computer.getGpu());
            computerData.put("storage", computer.getStorage());
            computerData.put("status", computer.getStatus());
            computerData.put("macAddress", computer.getMacAddress());
            computerData.put("ipAddress", computer.getIpAddress());
            computerData.put("dateAdded", computer.getDateAdded());

            firestore.collection("computers")
                    .document(String.valueOf(computer.getId()))
                    .set(computerData)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Computer synced: " + computer.getCode()))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error syncing computer: " + computer.getCode(), e));
        }
    }

    // Phương thức lấy tất cả dữ liệu từ bảng Computer
    public ArrayList<Computer> getAllComputers() {
        ArrayList<Computer> computers = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT c.id, c.code, c.cpu, c.ram, c.gpu, c.storage, c.status, c.macAddress, c.ipAddress, c.dateAdded " +
                "FROM Computer c";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String code = cursor.getString(1);
                String cpu = cursor.getString(2);
                String ram = cursor.getString(3);
                String gpu = cursor.getString(4);
                String storage = cursor.getString(5);
                String status = cursor.getString(6);
                String macAddress = cursor.getString(7);
                String ipAddress = cursor.getString(8);
                String dateAdded = cursor.getString(9);

                computers.add(new Computer(id, code, cpu, ram, gpu, storage, status, macAddress, ipAddress, dateAdded));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return computers;
    }

    // Thêm nhân viên mới vào bảng Employee
    public long insertEmployee(String employeeCode, String name, String phone, int roleId, int shiftId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("employeeCode", employeeCode);
        values.put("name", name);
        values.put("phone", phone);
        values.put("roleId", roleId);
        values.put("shiftId", shiftId);

        return db.insert("Employee", null, values);
    }

    // Phương thức lấy danh sách nhân viên từ bảng Employee
    public ArrayList<String> getAllEmployees() {
        ArrayList<String> employeeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT e.name, e.phone, r.roleName, s.shiftTime " +
                "FROM Employee e " +
                "LEFT JOIN Role r ON e.roleId = r.id " +
                "LEFT JOIN Shift s ON e.shiftId = s.id";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String phone = cursor.getString(1);
                String role = cursor.getString(2);
                String shift = cursor.getString(3);

                String employeeInfo = "Tên: " + name + "\n" +
                        "SĐT: " + phone + "\n" +
                        "Vai trò: " + (role != null ? role : "Chưa phân vai trò") + "\n" +
                        "Ca làm: " + (shift != null ? shift : "Chưa phân ca");
                employeeList.add(employeeInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return employeeList;
    }

    // Phương thức xóa nhân viên theo ID
    public boolean deleteEmployeeById(int employeeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("Employee", "id = ?", new String[]{String.valueOf(employeeId)});
        db.close();
        return rowsAffected > 0;
    }

    // Phương thức cập nhật thông tin nhân viên
    public boolean updateEmployee(int employeeId, String employeeCode, String name, String phone, int roleId, int shiftId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("employeeCode", employeeCode);
        values.put("name", name);
        values.put("phone", phone);
        values.put("roleId", roleId);
        values.put("shiftId", shiftId);

        int rowsAffected = db.update("Employee", values, "id = ?", new String[]{String.valueOf(employeeId)});
        db.close();
        return rowsAffected > 0;
    }

    public ArrayList<Integer> getAllEmployeeIds() {
        ArrayList<Integer> employeeIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM Employee", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                employeeIds.add(id);
            }
            cursor.close();
        }
        db.close();
        return employeeIds;
    }

    // Phương thức thêm máy tính mới
    public long insertComputer(String code, String cpu, String ram, String gpu, String storage, String status, String macAddress, String ipAddress, String dateAdded) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", code);
        values.put("cpu", cpu);
        values.put("ram", ram);
        values.put("gpu", gpu);
        values.put("storage", storage);
        values.put("status", status);
        values.put("macAddress", macAddress);
        values.put("ipAddress", ipAddress);
        values.put("dateAdded", dateAdded);

        long result = db.insert("Computer", null, values);
        if (result != -1) {
            syncComputersToFirestore(); // Đồng bộ sau khi thêm máy mới
        }
        return result;
    }
}