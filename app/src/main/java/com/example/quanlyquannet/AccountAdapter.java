package com.example.quanlyquannet;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<Account> accountList;
    private Context context;
    private OnUpdateBalanceListener updateBalanceListener;

    public interface OnUpdateBalanceListener {
        void onUpdateBalance(String userId, long amount);
    }

    public AccountAdapter(List<Account> accountList, OnUpdateBalanceListener listener) {
        this.accountList = accountList;
        this.updateBalanceListener = listener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.tvUsername.setText(account.getUsername());
        holder.tvBalance.setText("Số dư: " + account.getBalance());

        holder.btnAddBalance.setOnClickListener(v -> {
            // Hiển thị dialog để nhập số tiền
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Tăng số dư cho " + account.getUsername());

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("Xác nhận", (dialog, which) -> {
                String amountStr = input.getText().toString();
                if (!amountStr.isEmpty()) {
                    long amountToAdd = Long.parseLong(amountStr);
                    long newBalance = account.getBalance() + amountToAdd;
                    updateBalanceListener.onUpdateBalance(account.getId(), newBalance);
                }
            });
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvBalance;
        Button btnAddBalance;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvBalance = itemView.findViewById(R.id.tvBalance);
            btnAddBalance = itemView.findViewById(R.id.btnAddBalance);
        }
    }
}