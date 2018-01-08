package com.mobilewallet.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.mobilewallet.android.R;
import com.mobilewallet.android.pojos.Product;

/**
 * Created by ROCK LEE on 5.12.2016.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private List<Product> selected;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView productName;
        TextView topicName;
        TextView productPrice;
        TextView productAvailability;
        CheckBox buyCheckbox;

        ViewHolder(View v) {
            super(v);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            productPrice = (TextView) itemView.findViewById(R.id.product_price);
            topicName = (TextView) itemView.findViewById(R.id.product_topic_name);
            productAvailability = (TextView) itemView.findViewById(R.id.product_available);
            buyCheckbox = (CheckBox) itemView.findViewById(R.id.buyCheckbox);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProductAdapter(List<Product> products) {
        this.products = products;
        this.selected = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Product product = products.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf(product.getPrice())+" €");
        holder.productAvailability.setText(String.valueOf(product.getAvailable()));
        holder.topicName.setText(product.getTopicName());

        holder.buyCheckbox.setChecked(false);

        holder.buyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    selected.add(product);
                }
                else {
                    if (selected.contains(product)) {
                        selected.remove(product);
                    }
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return products.size();
    }

    public List<Product> getSelected() {
        return selected;
    }

    public void resetSelected() {
        this.selected.clear();
    }
}
