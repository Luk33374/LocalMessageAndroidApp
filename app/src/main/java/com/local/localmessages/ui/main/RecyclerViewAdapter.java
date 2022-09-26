package com.local.localmessages.ui.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.local.localmessages.R;
import com.local.localmessages.data.model.Conversation;
import com.local.localmessages.services.MessageService;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> userNames=new ArrayList<>();
    private final MessageService messageService = new MessageService();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> userNames, Context mContext) {
        this.userNames = userNames;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item,parent,false);
        ViewHolder holder= new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String lastMessageOfConversation=userNames.get(position);
        holder.userName.setText(lastMessageOfConversation);
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Conversation conversationBasedOfLastMessage = messageService
                        .getConversationBasedOfLastMessage(lastMessageOfConversation);
                Intent selectedConversation = new Intent(mContext , ConversationView.class);
                selectedConversation.putExtra("Conversation",conversationBasedOfLastMessage);
                mContext.startActivity(selectedConversation);
                //Toast.makeText(mContext,conversationBasedOfLastMessage.getMessagesInConversation().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName= itemView.findViewById(R.id.messageFromUser);
            relativeLayout= itemView.findViewById(R.id.recyclerView);
        }
    }
}
