package com.example.quanlyquannet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private RecyclerView rvSettings;
    private SettingsAdapter settingsAdapter;
    private List<SettingItem> settingList;
    private SharedPreferences sharedPreferences;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        role = sharedPreferences.getString("role", "guest");

        // Ánh xạ RecyclerView
        rvSettings = findViewById(R.id.rv_settings);
        rvSettings.setLayoutManager(new LinearLayoutManager(this));
        settingList = new ArrayList<>();

        // Thêm các danh mục cài đặt
        settingList.add(new SettingItem("Tài khoản"));
        settingList.add(new SettingItem("Giao diện"));

        settingsAdapter = new SettingsAdapter(settingList);
        rvSettings.setAdapter(settingsAdapter);
    }

    // Class lưu thông tin danh mục cài đặt
    private static class SettingItem {
        private String name;

        public SettingItem(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // Adapter cho RecyclerView
    private class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingViewHolder> {
        private List<SettingItem> settings;

        public SettingsAdapter(List<SettingItem> settings) {
            this.settings = settings;
        }

        @NonNull
        @Override
        public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
            return new SettingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
            SettingItem setting = settings.get(position);
            holder.tvSettingName.setText(setting.getName());

            holder.itemView.setOnClickListener(v -> {
                if (setting.getName().equals("Tài khoản")) {
                    showAccountOptions();
                } else if (setting.getName().equals("Giao diện")) {
                    showThemeOptions();
                }
            });
        }

        @Override
        public int getItemCount() {
            return settings.size();
        }

        private class SettingViewHolder extends RecyclerView.ViewHolder {
            TextView tvSettingName;

            public SettingViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSettingName = itemView.findViewById(R.id.tv_setting_name);
            }
        }
    }

    private void showAccountOptions() {
        // Hiển thị dialog với các tùy chọn: Nạp tiền, Đổi mật khẩu
        String[] options = role.equals("guest") ? new String[]{"Nạp tiền", "Đổi mật khẩu"} : new String[]{"Đổi mật khẩu"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tài khoản");
        builder.setItems(options, (dialog, which) -> {
            if (options[which].equals("Nạp tiền")) {
                // Chuyển đến logic nạp tiền (đã có trong MainActivity, có thể gọi lại)
                Toast.makeText(this, "Liên hệ nhân viên để nạp tiền", Toast.LENGTH_SHORT).show();
                // Có thể gọi showTopUpDialog() từ MainActivity bằng cách chuyển dữ liệu hoặc tạo Intent
            } else if (options[which].equals("Đổi mật khẩu")) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void showThemeOptions() {
        // Hiển thị dialog để chọn theme
        String[] themes = {"Sáng", "Tối", "Xanh dương"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn giao diện");
        builder.setItems(themes, (dialog, which) -> {
            String selectedTheme = themes[which];
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("theme", selectedTheme);
            editor.apply();
            applyTheme(selectedTheme);
            Toast.makeText(this, "Đã áp dụng giao diện: " + selectedTheme, Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void applyTheme(String theme) {
        // Áp dụng theme cho toàn bộ ứng dụng
        // switch (theme) {
        //     case "Sáng":
        //         setTheme(R.style.AppTheme_Light);
        //         break;
        //     case "Tối":
        //         setTheme(R.style.AppTheme_Dark);
        //         break;
        //     case "Xanh dương":
        //         setTheme(R.style.AppTheme_Blue);
        //         break;
        // }
        // Cần recreate activity để áp dụng theme
        // recreate();
    }
}