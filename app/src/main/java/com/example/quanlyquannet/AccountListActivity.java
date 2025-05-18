package com.example.quanlyquannet;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AccountListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AccountAdapter accountAdapter;
    private List<Account> accountList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);

        recyclerView = findViewById(R.id.recyclerViewAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        accountList = new ArrayList<>();
        accountAdapter = new AccountAdapter(accountList, this::updateBalance);
        recyclerView.setAdapter(accountAdapter);

        db = FirebaseFirestore.getInstance();
        loadAccounts();
    }

    private void loadAccounts() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        accountList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            Long balance = document.getLong("balance");
                            if (username != null && balance != null) {
                                accountList.add(new Account(document.getId(), username, balance));
                            }
                        }
                        accountAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Lỗi tải danh sách: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateBalance(String userId, long amount) {
        db.collection("users").document(userId)
                .update("balance", amount)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật số dư thành công!", Toast.LENGTH_SHORT).show();
                    loadAccounts(); // Tải lại danh sách để cập nhật giao diện
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}