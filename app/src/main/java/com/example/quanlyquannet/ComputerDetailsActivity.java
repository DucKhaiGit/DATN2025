package com.example.quanlyquannet;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ComputerDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_details);

        TextView tvComputerCode = findViewById(R.id.tvComputerCode);
        TextView tvUsageTime = findViewById(R.id.tvUsageTime);
        TextView tvTotalCost = findViewById(R.id.tvTotalCost);

        // Nhận dữ liệu từ Intent
        String computerCode = getIntent().getStringExtra("computerCode");
        long hours = getIntent().getLongExtra("hours", 0);
        long minutes = getIntent().getLongExtra("minutes", 0);
        long seconds = getIntent().getLongExtra("seconds", 0);
        long totalCost = getIntent().getLongExtra("totalCost", 0);

        // Hiển thị thông tin
        tvComputerCode.setText("Mã máy: " + computerCode);
        tvUsageTime.setText(String.format("Thời gian sử dụng: %02d:%02d:%02d", hours, minutes, seconds));
        tvTotalCost.setText("Tiền cần thanh toán: " + totalCost + " VND");
    }
}
