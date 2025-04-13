package com.example.quanlyquannet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ComputerListActivity extends AppCompatActivity {

    private ListView lvComputers;
    private NetCafeDatabase dbHelper;
    private Button btnAddComputer;
    private ComputerAdapter adapter;
    private ArrayList<Computer> computerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_list);

        lvComputers = findViewById(R.id.lvComputers);
        btnAddComputer = findViewById(R.id.btnAddComputer);
        dbHelper = new NetCafeDatabase(this);

        // Lấy danh sách máy tính từ cơ sở dữ liệu
        computerList = dbHelper.getAllComputers();

        // Sử dụng adapter tùy chỉnh
        adapter = new ComputerAdapter(this, computerList);
        lvComputers.setAdapter(adapter);

        // Xử lý sự kiện nút "Quay về trang chủ"
        Button btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(ComputerListActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Sự kiện bấm nút "Thêm Máy Tính"
        btnAddComputer.setOnClickListener(v -> {
            Intent intent = new Intent(ComputerListActivity.this, AddComputerActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Khi thêm máy tính thành công, tải lại danh sách máy tính
            computerList.clear();
            computerList.addAll(dbHelper.getAllComputers());
            adapter.notifyDataSetChanged();
        }
    }
}
