package com.example.quanlyquannet;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Xử lý sự kiện khi chọn item trong BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_computers) {
                Intent computerIntent = new Intent(MainActivity.this, ComputerListActivity.class);
                startActivity(computerIntent);
                return true;
            } else if (item.getItemId() == R.id.nav_employees) {
                Intent employeeIntent = new Intent(MainActivity.this, EmployeeListActivity.class);
                startActivity(employeeIntent);
                return true;
            } else if (item.getItemId() == R.id.nav_payments) {
                Intent paymentIntent = new Intent(MainActivity.this, PaymentListActivity.class);
                startActivity(paymentIntent);
                return true;
            } else if (item.getItemId() == R.id.nav_user) {
                // Thêm logic cho nav_user nếu cần
                // Ví dụ: Intent userIntent = new Intent(MainActivity.this, UserActivity.class);
                // startActivity(userIntent);
                return true;
            } else {
                return false;
            }
        });
    }
}