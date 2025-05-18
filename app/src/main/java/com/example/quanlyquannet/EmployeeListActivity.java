package com.example.quanlyquannet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeeAdapter employeeAdapter;
    private List<Employee> employeeList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        recyclerView = findViewById(R.id.recyclerViewEmployees);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        employeeList = new ArrayList<>();
        employeeAdapter = new EmployeeAdapter(employeeList, this::updateEmployee, this::deleteEmployee);
        recyclerView.setAdapter(employeeAdapter);

        Button btnAddEmployee = findViewById(R.id.btnAddEmployee);
        Button btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();
        loadEmployees();

        btnAddEmployee.setOnClickListener(v -> showAddEmployeeDialog());
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadEmployees() {
        db.collection("employees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        employeeList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            String role = document.getString("role");
                            Long salary = document.getLong("salary");
                            if (name != null && role != null && salary != null) {
                                employeeList.add(new Employee(id, name, role, salary));
                            }
                        }
                        employeeAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Lỗi tải danh sách: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddEmployeeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm nhân viên");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_employee, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etName);
        EditText etRole = view.findViewById(R.id.etRole);
        EditText etSalary = view.findViewById(R.id.etSalary);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = etName.getText().toString();
            String role = etRole.getText().toString();
            String salaryStr = etSalary.getText().toString();
            if (!name.isEmpty() && !role.isEmpty() && !salaryStr.isEmpty()) {
                long salary = Long.parseLong(salaryStr);
                addEmployee(name, role, salary);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addEmployee(String name, String role, long salary) {
        Map<String, Object> employee = new HashMap<>();
        employee.put("name", name);
        employee.put("role", role);
        employee.put("salary", salary);

        db.collection("employees")
                .add(employee)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Thêm nhân viên thành công!", Toast.LENGTH_SHORT).show();
                    loadEmployees();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi thêm nhân viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEmployee(String employeeId, String name, String role, long salary) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("role", role);
        updates.put("salary", salary);

        db.collection("employees").document(employeeId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật nhân viên thành công!", Toast.LENGTH_SHORT).show();
                    loadEmployees();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteEmployee(String employeeId) {
        db.collection("employees").document(employeeId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Xóa nhân viên thành công!", Toast.LENGTH_SHORT).show();
                    loadEmployees();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}