package com.seatus.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.seatus.Adapters.ChatAdapter;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Models.ChatItem;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.seatus.Utils.AppConstants;
import com.seatus.Views.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saqib on 12/7/2017.
 */

public class ChatFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.edit_msg)
    EditText editMsg;

    private ChatAdapter chatAdapter;
    private FirestoreRecyclerOptions<ChatItem> chatOptions;
    private String trip_id;

    public static Fragment newInstance(String trip_id) {
        ChatFragment fragment = new ChatFragment();
        fragment.trip_id = trip_id;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("group chat").enableBack();
    }

    @Override
    public void inits() {

        Query chatQuery = FirebaseFirestore.getInstance()
                .collection(AppConstants.STORE_COLLECTION_GROUPS)
                .document(trip_id)
                .collection(AppConstants.STORE_COLLECTION_CHAT)
                .orderBy("timestamp");

        chatOptions = new FirestoreRecyclerOptions.Builder<ChatItem>()
                .setQuery(chatQuery, ChatItem.class)
                .build();

        chatAdapter = new ChatAdapter(getContext(), chatOptions);

        setRecyclerview();

        recyclerview.setAdapter(chatAdapter);

    }

    private void setRecyclerview() {

        LinearLayoutManager ll = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        ll.setStackFromEnd(true);
        recyclerview.setLayoutManager(ll);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        recyclerview.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recyclerview.postDelayed(() -> recyclerview.scrollToPosition(
                        chatAdapter.getItemCount() - 1), 100);
            }
        });

        editMsg.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                recyclerview.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });

        RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int friendlyMessageCount = chatAdapter.getItemCount();
                int lastVisiblePosition = ll.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    if (recyclerview != null)
                        recyclerview.scrollToPosition(positionStart);
                }
            }

        };

        chatAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    @Override
    public void setEvents() {

    }

    @OnClick(R.id.btn_send)
    public void onViewClicked() {
        if (editMsg.getText().length() == 0)
            return;

        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection(AppConstants.STORE_COLLECTION_GROUPS)
                .document(trip_id)
                .collection(AppConstants.STORE_COLLECTION_CHAT).document();

        ChatItem chatItem = new ChatItem();
        chatItem.first_name = getUserItem().first_name;
        chatItem.last_name = getUserItem().last_name;
        chatItem.user_id = getUserItem().user_id;
        chatItem.message_id = documentReference.getId();
        chatItem.message_text = editMsg.getText().toString();

        documentReference.set(chatItem.toMap(), SetOptions.merge());

        editMsg.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatAdapter.stopListening();
    }
}
