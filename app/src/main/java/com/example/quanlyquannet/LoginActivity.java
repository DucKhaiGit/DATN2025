package com.example.quanlyquannet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Ánh xạ các thành phần từ XML
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        // Kiểm tra xem người dùng đã đăng nhập chưa
//        if (auth.getCurrentUser() != null) {
//            // Nếu đã đăng nhập, điều hướng thẳng đến MainActivity
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

        // Thiết lập sự kiện cho TextView Đăng Ký
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Điều hướng đến RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Thiết lập sự kiện cho nút Đăng Nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Kiểm tra dữ liệu đầu vào
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra độ dài username (tối thiểu 4 ký tự)
                if (username.length() < 4) {
                    Toast.makeText(LoginActivity.this, "Tên đăng nhập phải có ít nhất 4 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra username không chứa ký tự đặc biệt
                if (!username.matches("^[a-zA-Z0-9]+$")) {
                    Toast.makeText(LoginActivity.this, "Tên đăng nhập chỉ được chứa chữ cái và số", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra độ dài mật khẩu (tối thiểu 6 ký tự)
                if (password.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra kết nối internet
                if (!isNetworkAvailable()) {
                    Toast.makeText(LoginActivity.this, "Không có kết nối internet. Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo email giả định từ username
                final String email = username + "@yourapp.com";

                // Đăng nhập với Firebase Authentication
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Đăng nhập thành công, lấy UID
                                    String uid = auth.getCurrentUser().getUid();

                                    // Lấy vai trò và username từ Firestore
                                    db.collection("users").document(uid)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            String role = document.getString("role");
                                                            String username = document.getString("username");
                                                            long balance = document.getLong("balance") != null ? document.getLong("balance") : 0;

                                                            // Lưu UID, vai trò, username và balance vào SharedPreferences
                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            editor.putString("uid", uid);
                                                            editor.putString("role", role);
                                                            editor.putString("username", username);
                                                            editor.putLong("balance", balance);
                                                            editor.apply();

                                                            // Hiển thị Toast "Đăng nhập thành công!"
                                                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                                                            // Điều hướng đến MainActivity
                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(LoginActivity.this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "Lỗi lấy thông tin: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}