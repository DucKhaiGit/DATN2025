package com.example.quanlyquannet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EmployeeListActivity extends AppCompatActivity {

    private ListView lvEmployees;
    private NetCafeDatabase dbHelper;
    private Button btnAddEmployee;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> employeeList;
    private ArrayList<Integer> employeeIds; // Lưu trữ ID của nhân viên

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        lvEmployees = findViewById(R.id.lvEmployees);
        btnAddEmployee = findViewById(R.id.btnAddEmployee);
        dbHelper = new NetCafeDatabase(this);

        // Đăng ký Context Menu cho ListView
        registerForContextMenu(lvEmployees);

        // Lấy danh sách nhân viên từ cơ sở dữ liệu
        loadEmployeeList();

        // Nút quay về trang chủ
        Button btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Sự kiện bấm nút "Thêm Nhân Viên"
        btnAddEmployee.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeListActivity.this, AddEmployeeActivity.class);
            startActivityForResult(intent, 100); // Mã yêu cầu là 100
        });
    }

    private void loadEmployeeList() {
        // Lấy danh sách nhân viên và ID từ cơ sở dữ liệu
        employeeList = dbHelper.getAllEmployees(); // Danh sách chứa thông tin hiển thị
        employeeIds = dbHelper.getAllEmployeeIds(); // Danh sách chứa ID của nhân viên

        if (employeeList == null || employeeIds == null) {
            employeeList = new ArrayList<>();
            employeeIds = new ArrayList<>();
        }

        // Thiết lập adapter cho ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, employeeList);
        lvEmployees.setAdapter(adapter);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 100 || requestCode == 200) && resultCode == RESULT_OK) {
            // Khi thêm hoặc sửa nhân viên thành công, tải lại danh sách
            loadEmployeeList();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Tạo tiêu đề cho Context Menu
        menu.setHeaderTitle("Chọn hành động");

        // Thêm các item vào Context Menu
        menu.add(0, 1, 0, "Sửa");
        menu.add(0, 2, 1, "Xóa");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        if (employeeIds == null || position < 0 || position >= employeeIds.size()) {
            Toast.makeText(this, "Lỗi: Không thể lấy ID nhân viên!", Toast.LENGTH_SHORT).show();
            return super.onContextItemSelected(item);
        }

        int employeeId = employeeIds.get(position);

        switch (item.getItemId()) {
            case 1: // Sửa nhân viên
                editEmployee(employeeId);
                return true;
            case 2: // Xóa nhân viên
                deleteEmployee(employeeId);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    private void editEmployee(int employeeId) {
        Intent intent = new Intent(EmployeeListActivity.this, AddEmployeeActivity.class);
        intent.putExtra("EMPLOYEE_ID", employeeId); // Truyền ID nhân viên để sửa
        startActivityForResult(intent, 200); // Mã yêu cầu là 200
    }

    private void deleteEmployee(int employeeId) {
        // Hiển thị hộp thoại xác nhận
        new AlertDialog.Builder(this)
                .setTitle("Xóa nhân viên")
                .setMessage("Bạn có chắc chắn muốn xóa nhân viên này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    // Xóa nhân viên khỏi cơ sở dữ liệu
                    boolean result = dbHelper.deleteEmployeeById(employeeId);
                    if (result) {
                        Toast.makeText(this, "Xóa nhân viên thành công!", Toast.LENGTH_SHORT).show();
                        loadEmployeeList();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Xóa nhân viên thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }
}
