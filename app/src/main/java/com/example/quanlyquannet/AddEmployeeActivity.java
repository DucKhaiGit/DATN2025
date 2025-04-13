package com.example.quanlyquannet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddEmployeeActivity extends AppCompatActivity {
    private EditText etEmployeeCode, etEmployeeName, etEmployeePhone;
    private Spinner spRole, spShift;
    private Button btnAddEmployee;
    private NetCafeDatabase database;
    private ArrayList<Integer> roleIds, shiftIds; // Để lưu ID từ database
    private int employeeId = -1; // ID nhân viên (nếu đang sửa)
    private Button btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        // Ánh xạ view
        etEmployeeCode = findViewById(R.id.etEmployeeCode);
        etEmployeeName = findViewById(R.id.etEmployeeName);
        etEmployeePhone = findViewById(R.id.etEmployeePhone);
        spRole = findViewById(R.id.spRole);
        spShift = findViewById(R.id.spShift);
        btnAddEmployee = findViewById(R.id.btnAddEmployee);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        // Khởi tạo database
        database = new NetCafeDatabase(this);

        // Thiết lập dữ liệu cho Spinner
        setUpRoleSpinner();
        setUpShiftSpinner();

        btnBackToHome.setOnClickListener(v -> finish());

        // Kiểm tra nếu đang sửa nhân viên
        Intent intent = getIntent();
        if (intent.hasExtra("EMPLOYEE_ID")) {
            employeeId = intent.getIntExtra("EMPLOYEE_ID", -1);
            if (employeeId != -1) {
                loadEmployeeData(employeeId);
                btnAddEmployee.setText("Cập nhật nhân viên"); // Thay đổi nút thành "Cập nhật"
            }
        }

        // Xử lý thêm hoặc cập nhật nhân viên
        btnAddEmployee.setOnClickListener(view -> {
            String employeeCode = etEmployeeCode.getText().toString().trim();
            String name = etEmployeeName.getText().toString().trim();
            String phone = etEmployeePhone.getText().toString().trim();

            if (employeeCode.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(AddEmployeeActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            int roleId = roleIds.get(spRole.getSelectedItemPosition());
            int shiftId = shiftIds.get(spShift.getSelectedItemPosition());

            if (employeeId == -1) {
                // Thêm nhân viên mới
                long result = database.insertEmployee(employeeCode, name, phone, roleId, shiftId);

                if (result != -1) {
                    Toast.makeText(AddEmployeeActivity.this, "Thêm nhân viên thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddEmployeeActivity.this, "Thêm nhân viên thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Cập nhật nhân viên
                boolean result = database.updateEmployee(employeeId, employeeCode, name, phone, roleId, shiftId);

                if (result) {
                    Toast.makeText(AddEmployeeActivity.this, "Cập nhật nhân viên thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddEmployeeActivity.this, "Cập nhật nhân viên thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpRoleSpinner() {
        // Lấy danh sách vai trò từ cơ sở dữ liệu
        roleIds = new ArrayList<>();
        ArrayList<String> roleList = new ArrayList<>();

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, roleName FROM Role", null);

        while (cursor.moveToNext()) {
            roleIds.add(cursor.getInt(0)); // Lưu ID
            roleList.add(cursor.getString(1)); // Lưu tên vai trò
        }
        cursor.close();
        db.close();

        // Đổ dữ liệu vào Spinner
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleList);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);
    }

    private void setUpShiftSpinner() {
        // Lấy danh sách ca làm việc từ cơ sở dữ liệu
        shiftIds = new ArrayList<>();
        ArrayList<String> shiftList = new ArrayList<>();

        Cursor cursor = database.getReadableDatabase().rawQuery("SELECT id, shiftTime FROM Shift", null);

        while (cursor.moveToNext()) {
            shiftIds.add(cursor.getInt(0)); // Lưu ID
            shiftList.add(cursor.getString(1)); // Lưu thời gian ca làm việc
        }
        cursor.close();

        // Đổ dữ liệu vào Spinner
        ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shiftList);
        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spShift.setAdapter(shiftAdapter);
    }

    private void loadEmployeeData(int employeeId) {
        // Lấy dữ liệu nhân viên từ cơ sở dữ liệu
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT employeeCode, name, phone, roleId, shiftId FROM Employee WHERE id = ?", new String[]{String.valueOf(employeeId)});

        if (cursor.moveToFirst()) {
            etEmployeeCode.setText(cursor.getString(0));
            etEmployeeName.setText(cursor.getString(1));
            etEmployeePhone.setText(cursor.getString(2));

            // Thiết lập vai trò và ca làm việc trong Spinner
            int roleId = cursor.getInt(3);
            int shiftId = cursor.getInt(4);
            spRole.setSelection(roleIds.indexOf(roleId));
            spShift.setSelection(shiftIds.indexOf(shiftId));
        }
        cursor.close();
    }
}
