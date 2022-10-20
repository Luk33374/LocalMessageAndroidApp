package com.local.localmessages.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Conversation implements Serializable {
    private List<Message> messagesInConversation;

    public Conversation(List<Message> messagesInConversation) {
        this.messagesInConversation= messagesInConversation;
    }

    public List<Message> getMessagesInConversation() {
        return messagesInConversation;
    }

    public void setMessagesInConversation(Message messagesInConversation) {
        this.messagesInConversation.add(messagesInConversation);
    }

    public void addMessage(Message message){
        messagesInConversation.add(message);
    }

    @Override
    public String toString() {
        return "{" +
                "\"messagesInConversation\":\"" + messagesInConversation +
                "\"}";
    }
}
