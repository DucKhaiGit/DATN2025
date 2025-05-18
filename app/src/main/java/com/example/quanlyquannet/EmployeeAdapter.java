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

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employeeList;
    private Context context;
    private OnUpdateEmployeeListener updateListener;
    private OnDeleteEmployeeListener deleteListener;

    public interface OnUpdateEmployeeListener {
        void onUpdateEmployee(String employeeId, String name, String role, long salary);
    }

    public interface OnDeleteEmployeeListener {
        void onDeleteEmployee(String employeeId);
    }

    public EmployeeAdapter(List<Employee> employeeList, OnUpdateEmployeeListener updateListener, OnDeleteEmployeeListener deleteListener) {
        this.employeeList = employeeList;
        this.updateListener = updateListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.tvName.setText("Tên: " + employee.getName());
        holder.tvRole.setText("Vai trò: " + employee.getRole());
        holder.tvSalary.setText("Lương: " + employee.getSalary());

        holder.btnEdit.setOnClickListener(v -> showEditDialog(employee));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteEmployee(employee.getId()));
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    private void showEditDialog(Employee employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa nhân viên");

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_employee, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etName);
        EditText etRole = view.findViewById(R.id.etRole);
        EditText etSalary = view.findViewById(R.id.etSalary);

        etName.setText(employee.getName());
        etRole.setText(employee.getRole());
        etSalary.setText(String.valueOf(employee.getSalary()));

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = etName.getText().toString();
            String role = etRole.getText().toString();
            String salaryStr = etSalary.getText().toString();
            if (!name.isEmpty() && !role.isEmpty() && !salaryStr.isEmpty()) {
                long salary = Long.parseLong(salaryStr);
                updateListener.onUpdateEmployee(employee.getId(), name, role, salary);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole, tvSalary;
        Button btnEdit, btnDelete;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}