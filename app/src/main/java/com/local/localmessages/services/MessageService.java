package com.local.localmessages.services;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.local.localmessages.Config;
import com.local.localmessages.data.model.Conversation;
import com.local.localmessages.data.model.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageService {
    public ArrayList<String> getMessageContent(Message[] messages){
        Map<Long, Conversation> conversations=new HashMap<>();
        ArrayList<String> messageContent;
        for (int i=0;i<messages.length;i++){

            if(conversations.get(messages[i].getFromUser())==null) {
                conversations.put(messages[i].getFromUser(), new Conversation(new HashSet<Message>(Arrays.asList(messages[i]))));
            }else{
                Conversation existingConversation= conversations.get(messages[i].getFromUser());
                existingConversation.setMessagesInConversation(messages[i]);
                conversations.put(messages[i].getFromUser(),existingConversation);
            }
        }
        messageContent=returnFirstMessages(conversations);
        Config.usersConversation=conversations;
        return messageContent;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList returnFirstMessages(Map conversation){
        ArrayList firstMessage=new ArrayList();
        Set<Long> keySet = conversation.keySet();
        for(Long key: keySet){
            Conversation conversation1 = (Conversation) conversation.get(key);
            firstMessage.add(getLastMessage(conversation1));
        }
        return firstMessage;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getLastMessage(Conversation conversation){
        Set<Message> messagesInConversation = conversation.getMessagesInConversation();
        Message messageFirst=messagesInConversation.stream().findFirst().get();
        for (Message message: messagesInConversation){
            if(messageFirst.getDate().isBefore(message.getDate())||messageFirst.getDate().isEqual(message.getDate())){
                if(messageFirst.getTime().isBefore(message.getTime())){
                    messageFirst=message;
                }
            }
        }
        return messageFirst.getMessageContent();
    }
}
