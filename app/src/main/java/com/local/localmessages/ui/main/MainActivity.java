package com.local.localmessages.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.local.localmessages.Config;
import com.local.localmessages.R;
import com.local.localmessages.data.MessageRepository;
import com.local.localmessages.data.model.Message;
import com.local.localmessages.services.MessageService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MessageRepository messageRepository=new MessageRepository();
    private MessageService messageService=new MessageService();
    private RecyclerView messagesContainer;
    private  TextView userName;
    private Button refresh;
    private Button newConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName= findViewById(R.id.userName);
        newConversation=findViewById(R.id.newConversation);
        refresh= findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message[] messagesFromServer = messageRepository.getMessagesFromServer(Long.parseLong(Config.currentUser.getUserId()));
                RecyclerViewAdapter recyclerViewAdapter=new RecyclerViewAdapter(messageService.getMessageContent(messagesFromServer),view.getContext());
                messagesContainer.setAdapter(recyclerViewAdapter);
            }
        });

        newConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewConversationView.class);
                startActivity(intent);
            }
        });
        userName.setText("You are logged in as: "+Config.currentUser.getDisplayName()+" user");
        syncMessages();
    }

    @Override
    protected void onDestroy() {
        MessageService.saveMessagesToFile();
        super.onDestroy();
    }

    private void syncMessages(){
        Message[] messagesFromServer = messageRepository.getMessagesFromServer(Long.parseLong(Config.currentUser.getUserId()));
        initRecyclerView(messagesFromServer);
    }

    private void initRecyclerView(Message[] messagesFromServer){
        messagesContainer= findViewById(R.id.recyclerView);
        RecyclerViewAdapter recyclerViewAdapter=new RecyclerViewAdapter(messageService.getMessageContent(messagesFromServer),this);
        messagesContainer.setAdapter(recyclerViewAdapter);
        messagesContainer.setLayoutManager(new LinearLayoutManager(this));
    }
}