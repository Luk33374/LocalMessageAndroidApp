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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecyclerVIewAdapterNewConversation extends RecyclerView.Adapter<RecyclerVIewAdapterNewConversation.ViewHolder>{
    private Context context;
    private Map<String, Long> users;
    private List<String> userNames;
    private final MessageService messageService = new MessageService();

    public RecyclerVIewAdapterNewConversation(Context context, Map users) {
        this.context = context;
        this.users = users;
        this.userNames=new ArrayList<>(users.keySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item,parent,false);
        RecyclerVIewAdapterNewConversation.ViewHolder holder= new RecyclerVIewAdapterNewConversation.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String userName=userNames.get(position);
        holder.userName.setText(userName);
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Conversation conversationWithNewUser = messageService.getConversationWithUser(users
                        .get(holder.userName.getText().toString()));
                Intent selectedConversation = new Intent(context , ConversationView.class);
                selectedConversation.putExtra("Conversation",conversationWithNewUser);
                selectedConversation.putExtra("UserId",users.get(holder
                        .userName.getText().toString()));
                context.startActivity(selectedConversation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName= itemView.findViewById(R.id.messageFromUser);
            relativeLayout= itemView.findViewById(R.id.conversation);
        }
    }
}
