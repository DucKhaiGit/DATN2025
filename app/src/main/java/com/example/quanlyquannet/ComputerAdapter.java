package com.example.quanlyquannet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ComputerAdapter extends ArrayAdapter<Computer> {

    public ComputerAdapter(Context context, List<Computer> computers) {
        super(context, 0, computers);
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

        // Không thêm sự kiện cho btnOn và btnOff theo yêu cầu trước đó

        return convertView;
    }

    // ViewHolder pattern để tối ưu hiệu suất
    private static class ViewHolder {
        TextView tvComputerCode;
        TextView tvComputerDetails;
        Button btnOn;
        Button btnOff;
    }
}