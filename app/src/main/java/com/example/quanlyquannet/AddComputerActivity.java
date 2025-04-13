package com.example.quanlyquannet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddComputerActivity extends AppCompatActivity {

    private EditText etComputerCode, etStorage;
    private Spinner spinnerCPU, spinnerRAM, spinnerGPU, spinnerStatus;
    private Button btnAddComputer, btnBackToHome;
    private NetCafeDatabase dbHelper;
    private ArrayList<Integer> statusIds;
    private ArrayList<String> statusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_computer);

        etComputerCode = findViewById(R.id.etComputerCode);
        etStorage = findViewById(R.id.etStorage);
        spinnerCPU = findViewById(R.id.spinnerCPU);
        spinnerRAM = findViewById(R.id.spinnerRAM);
        spinnerGPU = findViewById(R.id.spinnerGPU);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnAddComputer = findViewById(R.id.btnAddComputer);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        dbHelper = new NetCafeDatabase(this);

        setUpSpinner(spinnerCPU, new String[]{"Intel i5", "Intel i7", "AMD Ryzen 5", "AMD Ryzen 7"});
        setUpSpinner(spinnerRAM, new String[]{"8GB", "16GB", "32GB"});
        setUpSpinner(spinnerGPU, new String[]{"NVIDIA GTX 1050", "NVIDIA GTX 1660", "AMD RX 5700"});
        setUpStatusSpinner();

        btnAddComputer.setOnClickListener(v -> addComputerToDatabase());

        btnBackToHome.setOnClickListener(v -> finish());
    }

    private void setUpSpinner(Spinner spinner, String[] options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setUpStatusSpinner() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, status FROM ComputerStatus", null);

        statusIds = new ArrayList<>();
        statusList = new ArrayList<>();

        while (cursor.moveToNext()) {
            statusIds.add(cursor.getInt(0));
            statusList.add(cursor.getString(1));
        }
        cursor.close();
        db.close();

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void addComputerToDatabase() {
        String computerCode = etComputerCode.getText().toString().trim();
        String storage = etStorage.getText().toString().trim();
        String cpu = spinnerCPU.getSelectedItem().toString();
        String ram = spinnerRAM.getSelectedItem().toString();
        String gpu = spinnerGPU.getSelectedItem().toString();
        int statusPosition = spinnerStatus.getSelectedItemPosition();

        if (computerCode.isEmpty()) {
            etComputerCode.setError("Mã máy tính không được để trống");
            return;
        }
        if (storage.isEmpty()) {
            etStorage.setError("Thông tin ổ cứng không được để trống");
            return;
        }

        int statusId = statusIds.get(statusPosition);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", computerCode);
        values.put("cpu", cpu);
        values.put("ram", ram);
        values.put("gpu", gpu);
        values.put("storage", storage);
        values.put("status", statusId);
        values.put("dateAdded", getCurrentDate());

        long result = db.insert("Computer", null, values);
        db.close();

        if (result == -1) {
            Toast.makeText(this, "Lỗi: Không thể thêm máy tính. Hãy thử lại!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Thêm máy tính thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Set the result to OK to notify the list activity to update
            finish();
        }
    }



    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
