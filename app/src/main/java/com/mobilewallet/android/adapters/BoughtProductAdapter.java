package com.mobilewallet.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.mobilewallet.android.R;
import com.mobilewallet.android.pojos.BoughtProduct;


public class BoughtProductAdapter extends RecyclerView.Adapter<BoughtProductAdapter.ViewHolder> {

    private List<BoughtProduct> products;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView productName;
        TextView topicName;
        TextView productPrice;
        TextView productAmount;
        TextView date;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            productPrice = (TextView) itemView.findViewById(R.id.product_price);
            topicName = (TextView) itemView.findViewById(R.id.product_topic_name);
            productAmount = (TextView) itemView.findViewById(R.id.product_amount);
            date = (TextView) itemView.findViewById(R.id.product_date);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BoughtProductAdapter(List<BoughtProduct> products) {
        this.products = products;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BoughtProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bought_product_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final BoughtProduct product = products.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf(product.getPrice())+" €");
        holder.productAmount.setText(String.valueOf(1)); //Zatial takto
        holder.topicName.setText(product.getTopic_name());
        holder.date.setText(product.getDate().toString());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return products.size();
    }

}
