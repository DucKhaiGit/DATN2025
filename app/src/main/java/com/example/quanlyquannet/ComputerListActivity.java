package com.example.quanlyquannet;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ComputerListActivity extends AppCompatActivity {

    private ListView lvComputers;
    private ComputerAdapter adapter;
    private NetCafeDatabase db;
    private ArrayList<Computer> computers;
    private FirebaseFirestore firestore;
    private ListenerRegistration firestoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_list);

        lvComputers = findViewById(R.id.lvComputers);
        db = new NetCafeDatabase(this);
        computers = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();

        // Đồng bộ dữ liệu từ SQLite sang Firestore khi khởi động
        db.syncComputersToFirestore();

        // Cập nhật ListView
        adapter = new ComputerAdapter(this, computers);
        lvComputers.setAdapter(adapter);

        // Lắng nghe thay đổi thời gian thực từ Firestore
        listenForRealtimeUpdates();
    }

    private void listenForRealtimeUpdates() {
        firestoreListener = firestore.collection("computers")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Error listening for updates: ", error);
                        // Fallback: Nếu Firestore lỗi, đọc từ SQLite
                        computers.clear();
                        computers.addAll(db.getAllComputers());
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    if (value != null) {
                        computers.clear();
                        for (QueryDocumentSnapshot document : value) {
                            int id = document.getLong("id").intValue();
                            String code = document.getString("code");
                            String cpu = document.getString("cpu");
                            String ram = document.getString("ram");
                            String gpu = document.getString("gpu");
                            String storage = document.getString("storage");
                            String status = document.getString("status");
                            String macAddress = document.getString("macAddress");
                            String ipAddress = document.getString("ipAddress");
                            String dateAdded = document.getString("dateAdded");

                            Computer computer = new Computer(id, code, cpu, ram, gpu, storage, status, macAddress, ipAddress, dateAdded);
                            computers.add(computer);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firestoreListener != null) {
            firestoreListener.remove(); // Hủy lắng nghe khi activity bị hủy
        }
    }
}