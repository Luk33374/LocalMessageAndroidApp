package com.local.localmessages.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.local.localmessages.R;
import com.local.localmessages.data.model.Conversation;
import com.local.localmessages.services.MessageService;



public class ConversationView extends AppCompatActivity {
    private Conversation currentConversation;
    private RecyclerView messagesContainer;
    private TextView conversationWithUser;
    private MessageService messageService=new MessageService();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_view);


        Intent intent=getIntent();
        currentConversation=(Conversation) intent.getSerializableExtra("Conversation");
        RecyclerViewAdapterForConversation adapter=new RecyclerViewAdapterForConversation(this,
                currentConversation);

        conversationWithUser= findViewById(R.id.userName);
        conversationWithUser.setText(messageService.getUserFromId(currentConversation
                .getMessagesInConversation().stream().findFirst().get().getFromUser()));
        messagesContainer= findViewById(R.id.conversation);
        messagesContainer.setAdapter(adapter);
        messagesContainer.setLayoutManager(new LinearLayoutManager(this));
    }

}
