package com.example.quanlyquannet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PaymentListActivity extends AppCompatActivity {

    private ListView listViewPayments;
    private Button btnBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);

        listViewPayments = findViewById(R.id.listViewPayments);
        btnBackToMain = findViewById(R.id.btnBackToMain);

        // Lấy danh sách tất cả hóa đơn từ cơ sở dữ liệu
        NetCafeDatabase dbHelper = new NetCafeDatabase(this);
        ArrayList<Payment> paymentsList = dbHelper.getAllPayments(); // Trả về danh sách Payment

        // Kiểm tra danh sách rỗng
        if (paymentsList.isEmpty()) {
            Toast.makeText(this, "Không có hóa đơn nào", Toast.LENGTH_SHORT).show();
        } else {
            // Sử dụng adapter cho danh sách đối tượng Payment
            PaymentAdapter paymentAdapter = new PaymentAdapter(this, paymentsList);
            listViewPayments.setAdapter(paymentAdapter);
        }

        // Sự kiện bấm nút "Quay lại"
        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentListActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng Activity hiện tại
        });

        listViewPayments.setOnItemLongClickListener((parent, view, position, id) -> {
            // Lấy hóa đơn được giữ lâu
            Payment selectedPayment = paymentsList.get(position);

            // Lấy thông tin UsageLog từ cơ sở dữ liệu

            UsageLog usageLog = dbHelper.getUsageLogById(selectedPayment.getUsageLogId());

            // Kiểm tra nếu UsageLog tồn tại
            if (usageLog != null) {
                // Hiển thị thông tin UsageLog trong AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentListActivity.this);
                builder.setTitle("Chi tiết UsageLog");
                builder.setMessage(
                        "ID: " + usageLog.getId() +
                                "\nComputer ID: " + usageLog.getComputerId() +
                                "\nThời gian bắt đầu: " + new Date(usageLog.getStartTime()).toString() +
                                "\nThời gian kết thúc: " + new Date(usageLog.getEndTime()).toString()
                );
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                Toast.makeText(PaymentListActivity.this, "Không tìm thấy UsageLog", Toast.LENGTH_SHORT).show();
            }

            return true; // Đã xử lý sự kiện
        });

    }
}

