package com.local.localmessages.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.local.localmessages.Config;
import com.local.localmessages.R;
import com.local.localmessages.data.MessageRepository;
import com.local.localmessages.data.model.Conversation;
import com.local.localmessages.data.model.Message;
import com.local.localmessages.services.MessageService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class ConversationView extends AppCompatActivity {
    public static Conversation currentConversation;
    private static RecyclerView messagesContainer;
    private TextView conversationWithUser;
    private MessageService messageService=new MessageService();
    private Button backButton;
    private Button sentMessageButton;
    private TextInputEditText textInputEditText;
    private static Long messageId=0l;
    private static Long conversationWithUserId=0l;
    private static List<Message> messagesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messagesList=new ArrayList<>();
        setContentView(R.layout.conversation_view);
        backButton=findViewById(R.id.backButton);
        sentMessageButton=findViewById(R.id.sendMessageButton);
        textInputEditText=findViewById(R.id.textInputEditText);


        Intent intent=getIntent();
        currentConversation=(Conversation) intent.getSerializableExtra("Conversation");
        Long userId=null;
        try {
            userId=(Long) intent.getSerializableExtra("UserId");
        }catch (Exception e){}
        RecyclerViewAdapterForConversation adapter=new RecyclerViewAdapterForConversation(this,
                currentConversation);

        conversationWithUser= findViewById(R.id.userName);
        if(userId==null) {
            conversationWithUserId = currentConversation
                    .getMessagesInConversation().stream().findFirst().get().getFromUser();
        }else conversationWithUserId=userId;
        conversationWithUser.setText(messageService.getUserFromId(conversationWithUserId));
        messagesContainer= findViewById(R.id.conversation);
        messagesContainer.setAdapter(adapter);
        messagesContainer.setLayoutManager(new LinearLayoutManager(this));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                if(messagesList.size()>0)MessageService.saveMessagesToFile();
                startActivity(intent);
            }
        });

        sentMessageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                currentConversation.getMessagesInConversation().stream().forEach(message -> {
                    if(ConversationView.messageId<message.getId())
                        ConversationView.messageId=message.getId();
                });

                Message messageToSave= new Message(ConversationView.messageId,
                        textInputEditText.getText().toString(), Long.parseLong(Config.currentUser.getUserId()),
                conversationWithUserId, LocalDate.now(), LocalTime.now());
                messagesList.add(messageToSave);
                MessageRepository.setMessage(messageToSave,view.getContext());
                textInputEditText.setText("");
            }
        });
    }

    public static void updateMessages(Context mContext, Message message){
        RecyclerViewAdapterForConversation adapter=new RecyclerViewAdapterForConversation(mContext,
                currentConversation);
        messagesContainer.setAdapter(adapter);
        if(Config.usersConversation.get(conversationWithUserId)==null){
            List<Message> singleMessageList=new ArrayList<>();
            singleMessageList.add(message);
            Config.usersConversation.put(conversationWithUserId,new Conversation(singleMessageList));
        }else {
            Config.usersConversation.get(conversationWithUserId).addMessage(message);
        }
    }

}
