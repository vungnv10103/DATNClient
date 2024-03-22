package com.datn.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.models.PaymentMethod;
import com.google.android.material.button.MaterialButton;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder> {
    private final HashMap<Integer, String> paymentMethod;
    private final List<PaymentMethod> paymentMethodList;
    private final Context context;
    private static IAction iActionPaymentMethod;

    public PaymentMethodAdapter(Context context, HashMap<Integer, String> paymentMethod, List<PaymentMethod> list, IAction iActionPaymentMethod) {
        this.paymentMethod = paymentMethod;
        paymentMethodList = list;
        this.context = context;
        PaymentMethodAdapter.iActionPaymentMethod = iActionPaymentMethod;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_method, parent, false);
        return new PaymentMethodAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentMethod payment = paymentMethodList.get(position);
        if (payment != null) {
            holder.setData(context, paymentMethod, payment);
        }
    }

    @Override
    public int getItemCount() {
        return paymentMethod == null ? 0 : paymentMethod.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialButton btnPaymentMethod;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnPaymentMethod = itemView.findViewById(R.id.btn_payment_method);
        }

        void setData(Context context, @NonNull HashMap<Integer, String> paymentMethod, @NonNull PaymentMethod payment) {
            Set<Integer> keys = paymentMethod.keySet();
            Collection<String> values = paymentMethod.values();
            System.out.println(keys);
            System.out.println(values);
            btnPaymentMethod.setText(payment.getValue());
            btnPaymentMethod.setTag(payment.getKey());
            btnPaymentMethod.setOnClickListener(v -> Toast.makeText(context, btnPaymentMethod.getTag().toString(), Toast.LENGTH_SHORT).show());

            itemView.setOnClickListener(v -> iActionPaymentMethod.onClick(payment));
        }
    }
}
