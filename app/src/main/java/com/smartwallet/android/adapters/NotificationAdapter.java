package com.smartwallet.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.smartwallet.android.R;
import com.smartwallet.android.avengerstoken.ConfirmIdentityActivity;
import com.smartwallet.android.avengerstoken.ConfirmTransactionActivity;
import com.smartwallet.android.enums.NotificationType;
import com.smartwallet.android.pojos.Notification;
import com.smartwallet.android.services.MQTTClientInterface;

/**
 * Created by JakubJ on 7.3.2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Notification> notifications;
    private Context context;

    public static class ViewHolderIdentity extends RecyclerView.ViewHolder {
        TextView status;
        TextView datePartOne;
        TextView datePartTwo;


        public ViewHolderIdentity(View v) {
            super(v);
            status = (TextView) itemView.findViewById(R.id.notificationStatus);
            datePartOne = (TextView) itemView.findViewById(R.id.notificationDatePartOne);
            datePartTwo = (TextView) itemView.findViewById(R.id.notificationDatePartTwo);
        }
    }

    public static class ViewHolderTransaction extends RecyclerView.ViewHolder {
        TextView status;
        TextView datePartOne;
        TextView datePartTwo;
        TextView amount;

        public ViewHolderTransaction(View v) {
            super(v);
            status = (TextView) itemView.findViewById(R.id.notificationStatus);
            datePartOne = (TextView) itemView.findViewById(R.id.notificationDatePartOne);
            datePartTwo = (TextView) itemView.findViewById(R.id.notificationDatePartTwo);
            amount = (TextView) itemView.findViewById(R.id.notificationAmount);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotificationAdapter(List<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {

        if (notifications.get(position).getType() == NotificationType.ConfirmIdentity) {
            return 0;
        }
        else return 1;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {

        View viewIndentity = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.confirm_identity_layout, parent, false);

        View viewTransaction = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.confirm_transaction_layout, parent, false);


        switch(viewType) {
            case 0:

                return new ViewHolderIdentity(viewIndentity);
            case 1:
                return new ViewHolderTransaction(viewTransaction);

        }

        // These lines should not be reached
        System.out.println("Error: Recycler view in notification adapter didn't recognize viewType");
        return new ViewHolderIdentity(viewIndentity);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        final Notification notification = notifications.get(position);

        final int notificationId = notification.getId();
        final String formatedDateFull = MQTTClientInterface.dateFormat.format(notification.getDate());
        final String formatedDatePartOne = MQTTClientInterface.dateFormatDay.format(notification.getDate());
        final String formatedDatePartTwo = MQTTClientInterface.dateFormatHours.format(notification.getDate());
        final String status = notification.getStatus().toString();
        final String content = notifications.get(position).getContent().toString();
        final String accountNumber = notification.getAccountNumber();

        switch (holder.getItemViewType()) {
            case 0: //ConfirmIdentity
                ViewHolderIdentity viewHolderIdentity = (ViewHolderIdentity) holder;
                viewHolderIdentity.datePartOne.setText(formatedDatePartOne);
                viewHolderIdentity.datePartTwo.setText(formatedDatePartTwo);
                viewHolderIdentity.status.setTextColor(Notification.notificationColor.get(status));
                viewHolderIdentity.status.setText(status);
                viewHolderIdentity.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ConfirmIdentityActivity.class);
                        intent.putExtra("notificationId", notificationId);
                        intent.putExtra("message", content);
                        intent.putExtra("timestamp", notification.getTimestampString());
                        intent.putExtra("guid", notification.getGuid());
                        intent.putExtra("action", notification.getAction());
                        intent.putExtra("accountNumber", accountNumber);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                break;
            case 1: //ConfirmTransaction
                final double amount = notifications.get(position).getAmount();
                final String paymentId = notifications.get(position).getPaymentId();

                ViewHolderTransaction viewHolderTransaction = (ViewHolderTransaction) holder;
                viewHolderTransaction.datePartOne.setText(formatedDatePartOne);
                viewHolderTransaction.datePartTwo.setText(formatedDatePartTwo);
                viewHolderTransaction.status.setTextColor(Notification.notificationColor.get(status));
                viewHolderTransaction.status.setText(status);
                viewHolderTransaction.amount.setText(String.valueOf(notifications.get(position).getAmount()) + "€");
                viewHolderTransaction.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ConfirmTransactionActivity.class);
                        intent.putExtra("notificationId", notificationId);
                        intent.putExtra("message", content);
                        intent.putExtra("timestamp", notification.getTimestampString());
                        intent.putExtra("amount", amount);
                        intent.putExtra("accountNumber", accountNumber);
                        intent.putExtra("paymentId", paymentId);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent);
                    }
                });
                break;
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return notifications.size();
    }

}
