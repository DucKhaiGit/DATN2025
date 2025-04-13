package com.example.quanlyquannet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class PaymentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Payment> payments;

    public PaymentAdapter(Context context, ArrayList<Payment> payments) {
        this.context = context;
        this.payments = payments;
    }

    @Override
    public int getCount() {
        return payments.size();
    }

    @Override
    public Object getItem(int position) {
        return payments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate layout item
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.payment_item, parent, false);
        }

        // Lấy thông tin Payment
        Payment payment = payments.get(position);

        // Gán dữ liệu vào các TextView trong item_payment.xml
        TextView tvPaymentId = convertView.findViewById(R.id.tvPaymentId);
        TextView tvUsageLogId = convertView.findViewById(R.id.tvUsageLogId);
        TextView tvTotalCost = convertView.findViewById(R.id.tvTotalCost);

        tvPaymentId.setText(String.valueOf(payment.getId()));
        tvUsageLogId.setText(String.valueOf(payment.getUsageLogId()));
        tvTotalCost.setText(String.format("%,d VND", payment.getTotalCost())); // Định dạng số tiền

        return convertView;
    }
}
