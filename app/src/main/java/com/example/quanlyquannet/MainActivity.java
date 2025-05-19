package com.example.quanlyquannet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvBalance;
    private TextView tvHourlyRate;
    private ImageButton btnLogout;
    private Button btnEditHourlyRate;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo FirebaseAuth và Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Ánh xạ các thành phần từ XML
        tvWelcome = findViewById(R.id.tv_welcome);
        tvBalance = findViewById(R.id.tv_balance);
        tvHourlyRate = findViewById(R.id.tv_hourly_rate);
        btnLogout = findViewById(R.id.btn_logout);
        btnEditHourlyRate = findViewById(R.id.btn_edit_hourly_rate);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Lấy thông tin từ SharedPreferences
        String username = sharedPreferences.getString("username", "Người dùng");
        String role = sharedPreferences.getString("role", "guest");
        long balance = sharedPreferences.getLong("balance", 0);

        // Hiển thị lời chào
        tvWelcome.setText("Xin chào, " + username);

        // Hiển thị số dư nếu là guest
        if (role.equals("guest")) {
            tvBalance.setText("Số dư: " + balance + " VNĐ");
            tvBalance.setVisibility(View.VISIBLE);
        } else {
            tvBalance.setVisibility(View.GONE);
        }

        // Phân quyền: Hạn chế guest truy cập nav_employees
        if (role.equals("guest")) {
            MenuItem employeeMenuItem = bottomNavigationView.getMenu().findItem(R.id.nav_employees);
            employeeMenuItem.setEnabled(false);
        }

        if (role.equals("guest")) {
            MenuItem guestMenuItem = bottomNavigationView.getMenu().findItem(R.id.nav_guests);
            guestMenuItem.setEnabled(false);
        }

        // Chỉ hiển thị nút sửa giá cho admin
        if (!role.equals("admin")) {
            btnEditHourlyRate.setVisibility(View.GONE);
        } else {
            btnEditHourlyRate.setOnClickListener(v -> showEditHourlyRateDialog());
        }

        // Tải giá thuê từ Firebase
        loadHourlyRate();

        // Xử lý sự kiện khi chọn item trong BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_computers) {
                    Intent computerIntent = new Intent(MainActivity.this, ComputerListActivity.class);
                    startActivity(computerIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_employees) {
                    // Đã vô hiệu hóa cho guest ở trên, chỉ admin mới vào được
                    Intent employeeIntent = new Intent(MainActivity.this, EmployeeListActivity.class);
                    startActivity(employeeIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_guests) {
                    Intent intent = new Intent(MainActivity.this, AccountListActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Thiết lập sự kiện cho icon Đăng xuất
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đăng xuất khỏi Firebase
                auth.signOut();

                // Xóa dữ liệu trong SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Hiển thị thông báo đăng xuất thành công
                Toast.makeText(MainActivity.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                // Điều hướng về LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    private void loadHourlyRate() {
        db.collection("settings")
                .document("hourlyRateConfig")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long rate = documentSnapshot.getLong("hourlyRate");
                        if (rate != null) {
                            tvHourlyRate.setText("Giá thuê/giờ: " + rate + " VNĐ");
                            tvHourlyRate.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvHourlyRate.setText("Giá thuê/giờ: Chưa thiết lập");
                        tvHourlyRate.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải giá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showEditHourlyRateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa giá thuê/giờ");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_hourly_rate, null);
        builder.setView(view);

        EditText etHourlyRate = view.findViewById(R.id.et_hourly_rate);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String rateStr = etHourlyRate.getText().toString().trim();
            if (!rateStr.isEmpty()) {
                long rate = Long.parseLong(rateStr);
                updateHourlyRate(rate);
            } else {
                Toast.makeText(this, "Vui lòng nhập giá", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateHourlyRate(long rate) {
        db.collection("settings")
                .document("hourlyRateConfig")
                .set(new HashMap<String, Object>() {{
                    put("hourlyRate", rate);
                }})
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật giá thành công!", Toast.LENGTH_SHORT).show();
                    loadHourlyRate(); // Tải lại giá để cập nhật giao diện
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi cập nhật giá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}