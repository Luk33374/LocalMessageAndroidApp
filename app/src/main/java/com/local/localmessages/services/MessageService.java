package com.local.localmessages.services;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.local.localmessages.Config;
import com.local.localmessages.data.MessageRepository;
import com.local.localmessages.data.model.Conversation;
import com.local.localmessages.data.model.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MessageService {
    MessageRepository messageRepository=new MessageRepository();
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<String> getMessageContent(Message[] messages){
        Map<Long, Conversation> conversations=new HashMap<>();
        ArrayList<String> messageContent;
        for (int i=0;i<messages.length;i++){

            if(conversations.get(messages[i].getFromUser())==null) {
                if(messages[i].getFromUser().equals(Long.parseLong(Config.currentUser.getUserId()))){
                    if(conversations.get(messages[i].getUserId())==null)
                        conversations.put(messages[i].getUserId(), new Conversation(new ArrayList<>(Arrays.asList(messages[i]))));
                    else {
                        Conversation existingConversation= conversations.get(messages[i].getUserId());
                        existingConversation.setMessagesInConversation(messages[i]);
                        conversations.put(messages[i].getUserId(),existingConversation);
                    }
                } else conversations.put(messages[i].getFromUser(), new Conversation(new ArrayList<>(Arrays.asList(messages[i]))));
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

    public Conversation getConversationBasedOfLastMessage(final String lastMessage){
        for (Map.Entry<Long, Conversation> pair : Config.usersConversation.entrySet()) {
            Message first = pair.getValue().getMessagesInConversation().stream()
                    .filter(message -> message.getMessageContent().equals(lastMessage))
                    .findFirst().orElse(null);
            if(first!=null) return pair.getValue();
        }
        return null;
    }

    public String getUserFromId(Long id){
        return messageRepository.getUserWithId(id);
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
        List<Message> messagesInConversation = conversation.getMessagesInConversation();
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
