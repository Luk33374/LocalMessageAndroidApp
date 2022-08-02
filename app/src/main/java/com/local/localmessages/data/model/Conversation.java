package com.local.localmessages.data.model;

import java.util.HashSet;
import java.util.Set;

public class Conversation {
    private Set<Message> messagesInConversation;

    public Conversation(HashSet<Message> messagesInConversation) {
        this.messagesInConversation= messagesInConversation;
    }

    public Set<Message> getMessagesInConversation() {
        return messagesInConversation;
    }

    public void setMessagesInConversation(Message messagesInConversation) {
        this.messagesInConversation.add(messagesInConversation);
    }
}
