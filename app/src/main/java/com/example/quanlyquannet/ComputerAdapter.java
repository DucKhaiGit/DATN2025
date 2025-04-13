package com.example.quanlyquannet;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ComputerAdapter extends ArrayAdapter<Computer> {

    private long[] startTimeArray; // Lưu thời gian bắt đầu của từng máy
    private boolean[] isRunningArray; // Trạng thái đang chạy của từng máy
    private Handler handler = new Handler(); // Handler để cập nhật bộ đếm
    // Khai báo mảng lưu trữ ID của UsageLog
    private long[] usageLogIds;


    public ComputerAdapter(Context context, List<Computer> computers) {
        super(context, 0, computers);
        startTimeArray = new long[computers.size()];
        isRunningArray = new boolean[computers.size()];
        usageLogIds = new long[computers.size()];

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Computer computer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.computer_item, parent, false);
        }
        TextView tvComputerCode = convertView.findViewById(R.id.tvComputerCode);
        TextView tvComputerDetails = convertView.findViewById(R.id.tvComputerDetails);
        TextView tvTimer = convertView.findViewById(R.id.tvTimer);
        Button btnOn = convertView.findViewById(R.id.btnOn);
        Button btnOff = convertView.findViewById(R.id.btnOff);

        // Hiển thị thông tin
        tvComputerCode.setText("Mã máy: " + computer.getCode());
        tvComputerDetails.setText(
                "CPU: " + computer.getCpu() +
                        "\nRAM: " + computer.getRam() +
                        "\nGPU: " + computer.getGpu() +
                        "\nStorage: " + computer.getStorage() +
                        "\nStatus: " + computer.getStatus() +
                        "\nAdded: " + computer.getDateAdded()
        );

        btnOn.setOnClickListener(v -> {
            if (!isRunningArray[position]) {
                computer.setStatus("Đang sử dụng");
                isRunningArray[position] = true;

                // Lưu trạng thái mới vào cơ sở dữ liệu
                NetCafeDatabase dbHelper = new NetCafeDatabase(getContext());
                dbHelper.updateComputerStatus(computer.getId(), "Đang sử dụng");

                // Lưu thông tin thời gian bắt đầu vào bảng UsageLog
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    ContentValues usageLogValues = new ContentValues();
                    usageLogValues.put("computerId", computer.getId());  // Lưu computerId chính xác
                    usageLogValues.put("startTime", SystemClock.elapsedRealtime());  // Lưu thời gian bắt đầu
                    long logId = db.insert("UsageLog", null, usageLogValues);
                    if (logId == -1) {
                        Toast.makeText(getContext(), "Lỗi khi lưu thời gian bắt đầu!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lưu logId vào mảng để sử dụng sau khi tắt máy
                        usageLogIds[position] = logId;  // Lưu id của UsageLog
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Lỗi khi kết nối cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                } finally {
                    db.close();
                }

                // Hiển thị bộ đếm thời gian
                tvTimer.setVisibility(View.VISIBLE);
                startTimeArray[position] = SystemClock.elapsedRealtime();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isRunningArray[position]) {
                            long elapsedTime = SystemClock.elapsedRealtime() - startTimeArray[position];
                            long hours = (elapsedTime / 1000) / 3600;
                            long minutes = ((elapsedTime / 1000) % 3600) / 60;
                            long seconds = (elapsedTime / 1000) % 60;
                            tvTimer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                            handler.postDelayed(this, 1000);
                        }
                    }
                });

                notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Máy tính đã bật rồi!", Toast.LENGTH_SHORT).show();
            }
        });




        btnOff.setOnClickListener(v -> {
            if (isRunningArray[position]) {
                computer.setStatus("Không hoạt động");
                isRunningArray[position] = false;

                // Cập nhật trạng thái máy tính trong cơ sở dữ liệu
                NetCafeDatabase dbHelper = new NetCafeDatabase(getContext());
                dbHelper.updateComputerStatus(computer.getId(), "Không hoạt động");

                // Tính thời gian đã sử dụng
                long endTime = SystemClock.elapsedRealtime();
                long elapsedTime = endTime - startTimeArray[position]; // Thời gian chạy
                long hours = (elapsedTime / 1000) / 3600;
                long minutes = ((elapsedTime / 1000) % 3600) / 60;
                long seconds = (elapsedTime / 1000) % 60;

                // Tính tiền (1 giây = 1000 đồng)
                long totalCost = (elapsedTime / 1000) * 1000;

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    // Cập nhật UsageLog: lưu thời gian kết thúc và thời gian sử dụng
                    ContentValues usageLogValues = new ContentValues();
                    usageLogValues.put("endTime", endTime);
                    usageLogValues.put("duration", elapsedTime);

                    // Cập nhật dữ liệu UsageLog với usageLogId
                    int updatedRows = db.update(
                            "UsageLog",
                            usageLogValues,
                            "id = ?",
                            new String[]{String.valueOf(usageLogIds[position])}  // Truyền usageLogId từ mảng
                    );


                    if (updatedRows > 0) {
                        // Thêm bản ghi mới vào bảng Payment
                        ContentValues paymentValues = new ContentValues();
                        paymentValues.put("usageLogId", usageLogIds[position]);  // Sử dụng usageLogId đã lưu
                        paymentValues.put("totalCost", totalCost);
                        db.insert("Payment", null, paymentValues);
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy bản ghi để cập nhật!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Lỗi khi kết nối cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                } finally {
                    db.close();
                }

                // Chuyển sang màn hình chi tiết
                Intent intent = new Intent(getContext(), ComputerDetailsActivity.class);
                intent.putExtra("computerCode", computer.getCode());
                intent.putExtra("hours", hours);
                intent.putExtra("minutes", minutes);
                intent.putExtra("seconds", seconds);
                intent.putExtra("totalCost", totalCost);
                getContext().startActivity(intent);

                tvTimer.setVisibility(View.GONE); // Ẩn bộ đếm
                notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Máy tính chưa được bật!", Toast.LENGTH_SHORT).show();
            }
        });






        return convertView;
    }

    private void startTimer(TextView tvTimer, int position) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isRunningArray[position]) {
                    long elapsedTime = SystemClock.elapsedRealtime() - startTimeArray[position];
                    long hours = (elapsedTime / 1000) / 3600;
                    long minutes = ((elapsedTime / 1000) % 3600) / 60;
                    long seconds = (elapsedTime / 1000) % 60;

                    tvTimer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }
}
