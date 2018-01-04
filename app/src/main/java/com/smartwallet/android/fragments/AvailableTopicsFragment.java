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
import com.mobilewallet.android.adapters.AvailableTopicsAdapter;
import com.mobilewallet.android.services.ClientManager;
import com.mobilewallet.android.services.ToastMaker;

/**
 * Created by ROCK LEE on 30.12.2016.
 */

public class AvailableTopicsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AvailableTopicsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private CardView subscribeButton;

    private ClientManager clientManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientManager = ClientManager.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_available_topics, container, false);
        setRetainInstance(true);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.topic_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new AvailableTopicsAdapter(clientManager.getAvailableTopics());

        mRecyclerView.setAdapter(mAdapter);

        subscribeButton = (CardView) rootView.findViewById(R.id.subscribeTopicButton);

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientManager.connected) {
                    System.out.println(Arrays.toString(mAdapter.getSelected().toArray()));
                    if (mAdapter.getSelected().size() > 0) {
                        for (String topic : mAdapter.getSelected()) {
                            clientManager.subscribe(topic);
                            clientManager.getSubscribedTopics().add(topic);
                            clientManager.getAvailableTopics().remove(topic);
                        }
                        mAdapter.notifyDataSetChanged(); //Update available topics adapter
                        ((TopicActivity) getActivity()).updateSubsbsribeFragment(); //Update subscribed topics adapter
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

    public AvailableTopicsAdapter getAdapter() {
        return mAdapter;
    }
}
