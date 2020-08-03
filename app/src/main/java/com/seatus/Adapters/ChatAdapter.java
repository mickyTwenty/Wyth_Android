package com.seatus.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.seatus.Activities.MainActivity;
import com.seatus.Holders.ChatHolder;
import com.seatus.Models.ChatItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.DateTimeHelper;
import com.seatus.Utils.StaticMethods;

/**
 * Created by saqib on 12/7/2017.
 */

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatItem, ChatHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private Context context;

    public ChatAdapter(Context context, FirestoreRecyclerOptions options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(ChatHolder holder, int position, ChatItem model) {
        UserItem userItem = ((MainActivity) context).getUserItem();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (!model.user_id.equals(userItem.user_id)) {
            params.gravity = Gravity.START;

            holder.messageBox.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_chatbox_grey));
            holder.messageBox.setLayoutParams(params);
            holder.name.setTextColor(ContextCompat.getColor(context,R.color.black));
            holder.message.setTextColor(ContextCompat.getColor(context,R.color.black));
            holder.date.setTextColor(ContextCompat.getColor(context,R.color.black));
        }else {
            params.gravity = Gravity.END;

            holder.messageBox.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_chatbox_blue));
            holder.messageBox.setLayoutParams(params);
            holder.name.setTextColor(ContextCompat.getColor(context,R.color.white));
            holder.message.setTextColor(ContextCompat.getColor(context,R.color.white));
            holder.date.setTextColor(ContextCompat.getColor(context,R.color.white));
        }

        holder.name.setText(model.first_name + " " + model.last_name);
        holder.message.setText(model.message_text);
        holder.date.setText(DateTimeHelper.getChatDateTime(model.timestamp));
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.item_chat_message, group, false);
        return new ChatHolder(view);
    }
}
