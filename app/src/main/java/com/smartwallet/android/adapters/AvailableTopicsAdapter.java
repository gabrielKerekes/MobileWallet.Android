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

/**
 * Created by ROCK LEE on 5.12.2016.
 */

public class AvailableTopicsAdapter extends RecyclerView.Adapter<AvailableTopicsAdapter.ViewHolder> {
    private List<String> topics;
    private List<String> selected;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView topic;
        public CheckBox subscribeCheckbox;

        public ViewHolder(View v) {
            super(v);
            topic = (TextView) itemView.findViewById(R.id.topic_text);
            subscribeCheckbox = (CheckBox) itemView.findViewById(R.id.subscribeCheckbox);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AvailableTopicsAdapter(List<String> topics) {
        this.topics = topics;
        this.selected = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AvailableTopicsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.available_topics_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.topic.setText(topics.get(position));
        holder.subscribeCheckbox.setChecked(false);

        holder.subscribeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    selected.add(topics.get(holder.getAdapterPosition()));
                }
                else {
                    if (selected.contains(topics.get(holder.getAdapterPosition()))) {
                        selected.remove(topics.get(holder.getAdapterPosition()));
                    }
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return topics.size();
    }

    public List<String> getSelected() {
        return selected;
    }

    public void resetSelected() {
        this.selected.clear();
    }

}
