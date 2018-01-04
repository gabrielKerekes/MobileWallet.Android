package com.mobilewallet.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.mobilewallet.android.R;
import com.mobilewallet.android.pojos.Payment;

/**
 * Created by ROCK LEE on 5.12.2016.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    private List<Payment> payments;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView amount;
        public TextView dateCreated;
        public TextView number;

        public ViewHolder(View v) {
            super(v);
            amount = (TextView) itemView.findViewById(R.id.payment_amount);
            dateCreated = (TextView) itemView.findViewById(R.id.payment_date_created);
            number = (TextView) itemView.findViewById(R.id.payment_number);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PaymentAdapter(List<Payment> payments) {
        this.payments = payments;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PaymentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Payment payment = payments.get(position);
        holder.amount.setText(String.valueOf(payment.getAmount())+"€");
        holder.dateCreated.setText(payment.getDateCreated().toString());
        holder.number.setText(String.valueOf(position + 1));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return payments.size();

    }
}
