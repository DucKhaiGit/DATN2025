package com.example.quanlyquannet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvBalance;
    private ImageButton btnLogout;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Ánh xạ các thành phần từ XML
        tvWelcome = findViewById(R.id.tv_welcome);
        tvBalance = findViewById(R.id.tv_balance);
        btnLogout = findViewById(R.id.btn_logout);
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
                } else if (item.getItemId() == R.id.nav_payments) {
                    Intent paymentIntent = new Intent(MainActivity.this, PaymentListActivity.class);
                    startActivity(paymentIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_user) {
                    // Thêm logic cho nav_user nếu cần
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
}