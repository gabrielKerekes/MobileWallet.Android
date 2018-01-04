package com.mobilewallet.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import com.mobilewallet.android.R;
import com.mobilewallet.android.TopicActivity;
import com.mobilewallet.android.adapters.SubscribedTopicsAdapter;
import com.mobilewallet.android.services.ClientManager;
import com.mobilewallet.android.services.ToastMaker;

/**
 * Created by ROCK LEE on 30.12.2016.
 */

public class SubscribedTopicsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SubscribedTopicsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private CardView unsubscribeButton;

    private ClientManager clientManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientManager = ClientManager.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_subscribed_topics, container, false);
        setRetainInstance(true);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.topic_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new SubscribedTopicsAdapter(clientManager.getSubscribedTopics());

        mRecyclerView.setAdapter(mAdapter);

        unsubscribeButton = (CardView) rootView.findViewById(R.id.unsubscribeTopicButton);
        unsubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientManager.connected) {
                    System.out.println(Arrays.toString(mAdapter.getSelected().toArray()));
                    if (mAdapter.getSelected().size() > 0) {
                        for (String topic : mAdapter.getSelected()) {
                            clientManager.unSubscribe(topic);
                            clientManager.deleteProductsFromUnsubscribedTopic(topic);
                            clientManager.getAvailableTopics().add(topic);
                            clientManager.getSubscribedTopics().remove(topic);
                        }
                        mAdapter.notifyDataSetChanged(); //Update subscribed topics adapter
                        ((TopicActivity) getActivity()).updateAvailableTopicsFragment(); //Update available topics adapter
                        mAdapter.resetSelected();
                    }
                } else {
                    ToastMaker.connectFirstToast(getActivity());
                }
            }
        });


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public SubscribedTopicsAdapter getAdapter() {
        return mAdapter;
    }

}
