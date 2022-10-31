package com.local.localmessages.services;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.localmessages.Config;
import com.local.localmessages.data.MessageRepository;
import com.local.localmessages.data.model.Conversation;
import com.local.localmessages.data.model.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class MessageService {
    MessageRepository messageRepository=new MessageRepository();
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<String> getMessageContent(Message[] messages){
        Map<Long, Conversation> conversations=getMessagesFromFile();
        if(conversations.size()>2&&conversations.get(0l)!=null)conversations.remove(0l);
        ArrayList<String> messageContent;
        for (int i=0;i<messages.length;i++){

            if(conversations.get(messages[i].getFromUser())==null) {
                if(messages[i].getFromUser().equals(Long.parseLong(Config.currentUser.getUserId()))){
                    if(conversations.get(messages[i].getUserId())==null)
                        if(checkIfMessageNotExists(messages[i],conversations))conversations.put(messages[i].getUserId(), new Conversation(new ArrayList<>(Arrays.asList(messages[i]))));
                    else {
                        if(checkIfMessageNotExists(messages[i],conversations)) {
                            Conversation existingConversation = conversations.get(messages[i].getUserId());
                            existingConversation.setMessagesInConversation(messages[i]);
                            conversations.put(messages[i].getUserId(), existingConversation);
                        }
                    }
                } else if(checkIfMessageNotExists(messages[i],conversations))conversations.put(messages[i].getFromUser(), new Conversation(new ArrayList<>(Arrays.asList(messages[i]))));
            }else{
                if(checkIfMessageNotExists(messages[i],conversations)) {
                    Conversation existingConversation = conversations.get(messages[i].getFromUser());
                    existingConversation.setMessagesInConversation(messages[i]);
                    conversations.put(messages[i].getFromUser(), existingConversation);
                }
            }
        }
        messageContent=returnFirstMessages(conversations);
        Config.usersConversation=conversations;
        saveMessagesToFile(conversations);
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

    public Conversation getConversationWithUser(Long userId){
        Set<Long> keys = Config.usersConversation.keySet();
        if (keys.contains(userId))return Config.usersConversation.get(userId);
        else return new Conversation(new ArrayList<Message>());
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
            if(messageFirst.getDate().isBefore(message.getDate())){
                messageFirst = message;
            }
            if(messageFirst.getDate().isEqual(message.getDate())&&messageFirst.getTime().isBefore(message.getTime())){
                messageFirst = message;
            }
        }
        return messageFirst.getMessageContent();
    }

    private static Map<Long, Conversation> getMessagesFromFile(){
        Map<Long, Conversation> conversations= new HashMap<>();
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File userMessagesFile = new File(path+"/userMessages.json");
            Scanner userMessagesReader = new Scanner(userMessagesFile);
            String data="";
            while (userMessagesReader.hasNextLine()) {
                 data = userMessagesReader.nextLine();
                System.out.println(data);
            }
            userMessagesReader.close();

            Map mapped=  mapper.readValue(data,Map.class);
            Set<String> keySet=mapped.keySet();
            for (String key:keySet){
                LinkedHashMap map = (LinkedHashMap)mapped.get(key);
                conversations.put(Long.parseLong(key),convertLinkedHashMap(map));
            }
            return conversations;
        } catch (Exception  e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Conversation convertLinkedHashMap(LinkedHashMap map){
        ArrayList linkedHashMap= (ArrayList) map.get("messagesInConversation");
        List<Message> messageList=new ArrayList<>();
        for(int i=0;i<linkedHashMap.size();i++) {
            LinkedHashMap conv=(LinkedHashMap) linkedHashMap.get(i);
            Long fromUser = new Long( (Integer)conv.get("fromUser"));
            Long id = new Long( (Integer) conv.get("id"));
            String messageContent = (String) conv.get("messageContent");
            Long userId = new Long( (Integer) conv.get("userId"));
            LinkedHashMap timeMap=(LinkedHashMap) conv.get("time");
            LinkedHashMap dateMap=(LinkedHashMap) conv.get("date");
            LocalTime time= LocalTime.of((int)timeMap.get("hour"),(int)timeMap.get("minute"),
                    (int)timeMap.get("second"));
            LocalDate date= LocalDate.of((int)dateMap.get("year"),(int)dateMap.get("monthValue"),
                    (int)dateMap.get("dayOfMonth"));
            messageList.add(new Message(id,messageContent,userId,fromUser,date,time));
        }
        return new Conversation(messageList);
    }

    private void saveMessagesToFile(@NonNull Map conversations){
        ObjectMapper mapper=new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            FileOutputStream fos = new FileOutputStream(path+"/userMessages.json");
            fos.write(mapper.writeValueAsBytes(conversations));
            fos.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void saveMessagesToFile(){
        Map<Long, Conversation> conversations = Config.usersConversation;
        ObjectMapper mapper=new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            FileOutputStream fos = new FileOutputStream(path+"/userMessages.json");
            fos.write(mapper.writeValueAsBytes(conversations));
            fos.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private boolean checkIfMessageNotExists(Message message, Map conversations){
        if(message.getMessageContent().equals("No messages"))return true;
        if(conversations.get(message.getFromUser())!=null) {
            List<Message> messages = ((Conversation) conversations.get(message.getFromUser()))
                    .getMessagesInConversation();
            for (Message storedMessage : messages)
                if (storedMessage.equals(message)) return false;
        }else{
            if(conversations.get(message.getUserId())!=null) {
                List<Message> messages = ((Conversation) conversations.get(message.getUserId()))
                        .getMessagesInConversation();
                for (Message storedMessage : messages)
                    if (storedMessage.equals(message)) return false;
            }
        }
        return true;
    }

}
