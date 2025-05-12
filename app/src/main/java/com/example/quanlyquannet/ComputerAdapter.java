package com.example.quanlyquannet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class ComputerAdapter extends ArrayAdapter<Computer> {

    private FirebaseFirestore firestore;

    public ComputerAdapter(Context context, List<Computer> computers) {
        super(context, 0, computers);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Computer computer = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.computer_item, parent, false);
            holder = new ViewHolder();
            holder.tvComputerCode = convertView.findViewById(R.id.tvComputerCode);
            holder.tvComputerDetails = convertView.findViewById(R.id.tvComputerDetails);
            holder.btnOn = convertView.findViewById(R.id.btnOn);
            holder.btnOff = convertView.findViewById(R.id.btnOff);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Hiển thị thông tin
        holder.tvComputerCode.setText("Mã máy: " + computer.getCode());
        holder.tvComputerDetails.setText(
                "CPU: " + computer.getCpu() + "\n" +
                        "RAM: " + computer.getRam() + "\n" +
                        "GPU: " + computer.getGpu() + "\n" +
                        "Storage: " + computer.getStorage() + "\n" +
                        "Status: " + computer.getStatus() + "\n" +
                        "Added: " + computer.getDateAdded()
        );

        // Sự kiện cho nút Bật (WoL)
        holder.btnOn.setOnClickListener(v -> {
            // Kiểm tra kết nối mạng
            if (!isNetworkAvailable()) {
                Toast.makeText(getContext(), "Không có kết nối mạng!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi gói WoL
            try {
                sendWakeOnLanPacket(computer.getMacAddress(), computer.getIpAddress());
                Toast.makeText(getContext(), "Đã gửi gói WoL cho máy " + computer.getCode(), Toast.LENGTH_SHORT).show();

                // Cập nhật trạng thái trên Firestore
                computer.setStatus("in_use");
                firestore.collection("computers")
                        .document(String.valueOf(computer.getId()))
                        .update("status", "in_use")
                        .addOnSuccessListener(aVoid -> notifyDataSetChanged())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi cập nhật trạng thái!", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Toast.makeText(getContext(), "Lỗi gửi gói WoL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Tạm thời không xử lý nút Tắt
        holder.btnOff.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng tắt máy chưa được triển khai!", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }

    // ViewHolder pattern để tối ưu hiệu suất
    private static class ViewHolder {
        TextView tvComputerCode;
        TextView tvComputerDetails;
        Button btnOn;
        Button btnOff;
    }

    // Kiểm tra kết nối mạng
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Gửi gói Wake-on-LAN
    private void sendWakeOnLanPacket(String macAddress, String ipAddress) throws Exception {
        // Chuẩn hóa địa chỉ MAC (loại bỏ dấu phân cách và chuyển thành bytes)
        byte[] macBytes = getMacBytes(macAddress);
        byte[] bytes = new byte[6 + 16 * macBytes.length];

        // Điền 6 byte đầu tiên là 0xFF
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xFF;
        }

        // Lặp lại địa chỉ MAC 16 lần
        for (int i = 6; i < bytes.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
        }

        // Gửi gói Magic Packet qua UDP
        InetAddress address = InetAddress.getByName(ipAddress);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 9);
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        socket.close();
    }

    // Chuyển địa chỉ MAC thành mảng bytes
    private byte[] getMacBytes(String macAddress) throws IllegalArgumentException {
        if (macAddress == null || macAddress.isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ MAC không hợp lệ!");
        }

        // Loại bỏ các ký tự phân cách (nếu có)
        macAddress = macAddress.replaceAll("[:-]", "");
        if (macAddress.length() != 12) {
            throw new IllegalArgumentException("Địa chỉ MAC phải có 12 ký tự (6 byte)!");
        }

        byte[] bytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            String hex = macAddress.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(hex, 16);
        }
        return bytes;
    }
}