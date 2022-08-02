package com.local.localmessages;

import com.local.localmessages.data.model.Conversation;
import com.local.localmessages.data.model.LoggedInUser;

import java.util.Map;

public class Config {
    public static String API="http://10.0.2.2:8080";
    public static LoggedInUser currentUser;
    public static Map<Long, Conversation> usersConversation;
}
