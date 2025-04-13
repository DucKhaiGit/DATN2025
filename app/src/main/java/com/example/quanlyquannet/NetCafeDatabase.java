package com.example.quanlyquannet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetCafeDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NetCafeDB";
    private static final int DATABASE_VERSION = 1;

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
                "inventoryId INTEGER, " +
                "dateAdded TEXT NOT NULL, " +
                "FOREIGN KEY (inventoryId) REFERENCES Inventory(id))");

        // Bảng Trạng thái máy tính
        db.execSQL("CREATE TABLE ComputerStatus (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "status TEXT UNIQUE NOT NULL)");

        // Thêm dữ liệu mặc định cho bảng ComputerStatus
        db.execSQL("INSERT INTO ComputerStatus (status) VALUES ('Đang sử dụng'), ('Bảo trì'), ('Không hoạt động')");
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

        // Bảng Lịch sử bảo trì
        db.execSQL("CREATE TABLE MaintenanceHistory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "computerId INTEGER, " +
                "maintenanceDate TEXT NOT NULL, " +
                "issue TEXT NOT NULL, " +
                "solution TEXT NOT NULL, " +
                "FOREIGN KEY (computerId) REFERENCES Computer(id))");

        // Bảng Chấm công
        db.execSQL("CREATE TABLE Attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "employeeId INTEGER, " +
                "date TEXT NOT NULL, " +
                "shiftId INTEGER, " +
                "hoursWorked INTEGER NOT NULL, " +
                "FOREIGN KEY (employeeId) REFERENCES Employee(id), " +
                "FOREIGN KEY (shiftId) REFERENCES Shift(id))");

        // Bảng Kho linh kiện
        db.execSQL("CREATE TABLE Inventory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "itemName TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL)");

        // Bảng Khách hàng
        db.execSQL("CREATE TABLE Customer (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customerCode TEXT UNIQUE NOT NULL, " +
                "name TEXT NOT NULL, " +
                "phone TEXT NOT NULL, " +
                "registrationDate TEXT NOT NULL)");

        // Bảng Phiên sử dụng máy tính (Session)
        db.execSQL("CREATE TABLE Session (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "computerId INTEGER NOT NULL, " +
                "customerId INTEGER NOT NULL, " +
                "employeeId INTEGER NOT NULL, " +
                "startTime TEXT NOT NULL, " +
                "endTime TEXT NOT NULL, " +
                "FOREIGN KEY (computerId) REFERENCES Computer(id), " +
                "FOREIGN KEY (customerId) REFERENCES Customer(id), " +
                "FOREIGN KEY (employeeId) REFERENCES Employee(id))");

        // Tạo bảng UsageLog
        db.execSQL("CREATE TABLE IF NOT EXISTS UsageLog (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "computerId INTEGER NOT NULL, " +
                "startTime TEXT NOT NULL, " +
                "endTime TEXT, " +
                "duration INTEGER, " +
                "FOREIGN KEY (computerId) REFERENCES Computer (id) ON DELETE CASCADE" +
                ");");

        // Tạo bảng Payment
        db.execSQL("CREATE TABLE IF NOT EXISTS Payment (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usageLogId INTEGER NOT NULL, " +
                "totalCost INTEGER NOT NULL, " +
                "FOREIGN KEY (usageLogId) REFERENCES UsageLog (id) ON DELETE CASCADE" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Computer");
        db.execSQL("DROP TABLE IF EXISTS ComputerStatus");
        db.execSQL("DROP TABLE IF EXISTS Employee");
        db.execSQL("DROP TABLE IF EXISTS Role");
        db.execSQL("DROP TABLE IF EXISTS Shift");
        db.execSQL("DROP TABLE IF EXISTS MaintenanceHistory");
        db.execSQL("DROP TABLE IF EXISTS Attendance");
        db.execSQL("DROP TABLE IF EXISTS Inventory");
        db.execSQL("DROP TABLE IF EXISTS Customer");
        db.execSQL("DROP TABLE IF EXISTS Session");
        db.execSQL("DROP TABLE IF EXISTS Payment");
        db.execSQL("DROP TABLE IF EXISTS UsageLog");
        onCreate(db);
    }

    // Phương thức lấy tất cả dữ liệu từ bảng Computer
    public ArrayList<Computer> getAllComputers() {
        ArrayList<Computer> computers = new ArrayList<>();

        // Mở kết nối cơ sở dữ liệu
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn dữ liệu với JOIN để lấy thông tin trạng thái
        String query = "SELECT c.id, c.code, c.cpu, c.ram, c.gpu, c.storage, cs.status, c.dateAdded " +
                "FROM Computer c " +
                "JOIN ComputerStatus cs ON c.status = cs.id";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String code = cursor.getString(1);
                String cpu = cursor.getString(2);
                String ram = cursor.getString(3);
                String gpu = cursor.getString(4);
                String storage = cursor.getString(5);
                String status = cursor.getString(6);  // Tên trạng thái từ bảng ComputerStatus
                String dateAdded = cursor.getString(7);

                // Thêm máy tính vào danh sách
                computers.add(new Computer(id, code, cpu, ram, gpu, storage, status, dateAdded));
            } while (cursor.moveToNext());
        }

        // Đóng con trỏ và cơ sở dữ liệu
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

        // Truy vấn dữ liệu nhân viên cùng với vai trò và ca làm việc
        String query = "SELECT e.name, e.phone, r.roleName, s.shiftTime " +
                "FROM Employee e " +
                "LEFT JOIN Role r ON e.roleId = r.id " +
                "LEFT JOIN Shift s ON e.shiftId = s.id";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Lấy dữ liệu từng cột
                String name = cursor.getString(0);
                String phone = cursor.getString(1);
                String role = cursor.getString(2);
                String shift = cursor.getString(3);

                // Format thông tin nhân viên
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
        return rowsAffected > 0; // Trả về true nếu xóa thành công
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
        return rowsAffected > 0; // Trả về true nếu cập nhật thành công
    }

    public ArrayList<Integer> getAllEmployeeIds() {
        ArrayList<Integer> employeeIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM Employee", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Lấy ID của nhân viên từ cột đầu tiên (cột "id")
                int id = cursor.getInt(0);
                employeeIds.add(id);
            }
            cursor.close();
        }
        db.close();
        return employeeIds;
    }
    public void updateComputerStatus(int computerId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Lấy id của trạng thái trong bảng ComputerStatus
        Cursor cursor = db.rawQuery("SELECT id FROM ComputerStatus WHERE status = ?", new String[]{status});
        if (cursor.moveToFirst()) {
            int statusId = cursor.getInt(0);
            values.put("status", statusId);
            db.update("Computer", values, "id = ?", new String[]{String.valueOf(computerId)});
        }
        cursor.close();
    }

    public ArrayList<Payment> getAllPayments() {
        ArrayList<Payment> payments = new ArrayList<>();

        // Mở kết nối cơ sở dữ liệu
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn tất cả các bản ghi từ bảng Payment
        String query = "SELECT id, usageLogId, totalCost FROM Payment";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Lấy dữ liệu từ từng cột theo chỉ số cột
                int id = cursor.getInt(0);             // Cột 0: id
                int usageLogId = cursor.getInt(1);     // Cột 1: usageLogId
                int totalCost = cursor.getInt(2);      // Cột 2: totalCost

                // Tạo đối tượng Payment và thêm vào danh sách
                payments.add(new Payment(id, usageLogId, totalCost));
            } while (cursor.moveToNext());
        }

        // Đóng con trỏ và cơ sở dữ liệu
        cursor.close();
        db.close();

        return payments;
    }

    public UsageLog getUsageLogById(int usageLogId) {
        SQLiteDatabase db = this.getReadableDatabase();
        UsageLog usageLog = null;

        // Truy vấn thông tin UsageLog dựa trên usageLogId
        String query = "SELECT id, computerId, startTime, endTime FROM UsageLog WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usageLogId)});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            int computerId = cursor.getInt(1);
            long startTime = cursor.getLong(2);
            long endTime = cursor.getLong(3);

            usageLog = new UsageLog(id, computerId, startTime, endTime);
        }

        cursor.close();
        db.close();

        return usageLog;
    }









}
