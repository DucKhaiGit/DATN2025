package com.example.quanlyquannet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private RadioButton radioAdmin;
    private RadioButton radioGuest;
    private Button btnRegister;

    private Button btnbacktoLogin;


    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Đảm bảo tên file XML của bạn là activity_register.xml

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các thành phần từ XML
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        radioAdmin = findViewById(R.id.radio_admin);
        radioGuest = findViewById(R.id.radio_guest);
        btnRegister = findViewById(R.id.btn_register);
        btnbacktoLogin = findViewById(R.id.btn_backtoLogin);

        // Vô hiệu hóa radioAdmin nếu đã có admin
        checkAdminStatus();

        // Xử lý sự kiện nhấn nút Đăng Ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                final String selectedRole = radioAdmin.isChecked() ? "admin" : "guest";

                // Kiểm tra dữ liệu đầu vào
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra độ dài username (tối thiểu 4 ký tự)
                if (username.length() < 4) {
                    Toast.makeText(RegisterActivity.this, "Tên đăng nhập phải có ít nhất 4 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra username không chứa ký tự đặc biệt không hợp lệ cho email
                if (!username.matches("^[a-zA-Z0-9]+$")) {
                    Toast.makeText(RegisterActivity.this, "Tên đăng nhập chỉ được chứa chữ cái và số", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra độ dài mật khẩu (tối thiểu 6 ký tự, yêu cầu của Firebase)
                if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu và xác nhận mật khẩu khớp nhau
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo email giả định từ username
                final String email = username + "@yourapp.com"; // Ví dụ: user123@yourapp.com

                // Kiểm tra vai trò và đăng ký
                if (selectedRole.equals("admin")) {
                    // Kiểm tra xem đã có admin chưa
                    db.collection("config").document("settings")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists() && document.getBoolean("admin_created") != null
                                                && document.getBoolean("admin_created")) {
                                            // Đã có admin, không cho phép đăng ký thêm
                                            Toast.makeText(RegisterActivity.this, "Tài khoản Admin đã tồn tại!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Chưa có admin, cho phép đăng ký
                                            registerUser(email, password, username, selectedRole);
                                        }
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Lỗi kiểm tra admin: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Vai trò Guest, đăng ký bình thường
                    registerUser(email, password, username, selectedRole);
                }
            }
        });

        //nút quay lai login

        btnbacktoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Điều hướng đến RegisterActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void checkAdminStatus() {
        // Kiểm tra kết nối internet
        if (!isNetworkAvailable()) {
            Toast.makeText(RegisterActivity.this, "Không có kết nối internet. Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra xem đã có admin chưa để vô hiệu hóa radioAdmin
        db.collection("config").document("settings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.getBoolean("admin_created") != null
                                    && document.getBoolean("admin_created")) {
                                // Đã có admin, vô hiệu hóa radioAdmin
                                radioAdmin.setEnabled(false);
                                radioAdmin.setTextColor(getResources().getColor(android.R.color.darker_gray));
                            }
                        }
                    }
                });
    }

    private void registerUser(final String email, String password, final String username, final String role) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng ký thành công, lưu thông tin vào Firestore
                            String userId = auth.getCurrentUser().getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username);
                            user.put("role", role);
                            user.put("balance", 0);

                            // Lưu thông tin người dùng
                            db.collection("users").document(userId)
                                    .set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Nếu là admin, cập nhật flag admin_created
                                                if (role.equals("admin")) {
                                                    Map<String, Object> config = new HashMap<>();
                                                    config.put("admin_created", true);
                                                    db.collection("config").document("settings")
                                                            .set(config)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(RegisterActivity.this, "Đăng ký Admin thành công!", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Toast.makeText(RegisterActivity.this, "Lỗi lưu cấu hình admin!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Đăng ký Guest thành công!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Lỗi lưu thông tin: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}