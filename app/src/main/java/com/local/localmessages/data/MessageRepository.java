package com.local.localmessages.data;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.localmessages.Config;
import com.local.localmessages.data.model.Message;
import com.local.localmessages.ui.main.ConversationView;
import com.local.localmessages.ui.main.MainActivity;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageRepository {
    private static Message[] messages;
    private static String user;
    private static final OkHttpClient client = new OkHttpClient();

    public String getUserWithId(Long userId){
        Request request = new Request.Builder()
                .url(Config.API+"/getUserNameWithUserId/"+userId)
                .build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e);
                countDownLatch.countDown();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String message =response.body().string();
                if(message.length()>0) {
                    user=message;
                    countDownLatch.countDown();
                }else {
                    user="Error";
                    countDownLatch.countDown();
                }
            }
        });

        try {
            countDownLatch.await();
            return user;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return user;
    }

    public Message[] getMessagesFromServer(Long userId) {
        Request request = new Request.Builder()
                .url(Config.API+"/getMessageWithUserId/"+userId)
                .build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e);
                countDownLatch.countDown();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body =response.body().string();
                //body="{\"message\":"+body+"}";
                ObjectMapper mapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
                Message[] message = mapper.readValue(body,Message[].class);
                if(message.length>0) {
                    deleteDownloadedMessages(message);
                    setMessages(message);
                    countDownLatch.countDown();
                }else {
                    setMessages(new Message[]{new Message(0l, "No messages", 0l, 0l, LocalDate.now(), LocalTime.now())});
                    countDownLatch.countDown();
                }
            }
        });

        try {
            countDownLatch.await();
            return messages;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private static void deleteDownloadedMessages(Message[] messages){
        Set<Long> messagesIds=new HashSet<>();
        Arrays.stream(messages).sequential().forEach(message -> {
            if(message.getUserId().equals(Long.parseLong(Config.currentUser.getUserId())))
                messagesIds.add(message.getId());
        });
        if(messagesIds.size()>0) {
            String json = "[";
            for (Long id : messagesIds) json = json + id + ", ";
            json = json.substring(0, json.length() - 2) + "]";
            RequestBody formBody = RequestBody.create(
                    MediaType.parse("application/json"), json);
            Request request = new Request.Builder()
                    .url(Config.API + "/deleteMessages/")
                    .delete(formBody)
                    .build();
            CountDownLatch countDownLatch = new CountDownLatch(1);


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    System.out.println(e);
                    countDownLatch.countDown();
                }

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String body = response.body().string();
                    System.out.println(body);
                    countDownLatch.countDown();
                }
            });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        }

    }

    private static void setMessages(Message[] messages) {
        MessageRepository.messages = messages;
    }

    public static void setMessage(Message message, Context mContext){
        Message[] updatedMessages= new Message[MessageRepository.messages.length+1];
        for(int i=0;i<MessageRepository.messages.length;i++)updatedMessages[i]=MessageRepository.messages[i];
        updatedMessages[updatedMessages.length-1]=message;
        ConversationView.currentConversation.setMessagesInConversation(message);
        ConversationView.updateMessages(mContext, message);
        sendMessage(message.getMessageContent(),message.getUserId(),message.getFromUser());
    }

    private static void sendMessage(String messageContent, Long fromUser, Long toUser){
        String json ="{\"messageContent\":\""+messageContent+"\",\"userId\":\""+toUser+"\",\"fromUser\":\""+fromUser+"\"}";
        RequestBody formBody = RequestBody.create(
                MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(Config.API+"/setMessage/")
                .post(formBody)
                .build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e);
                countDownLatch.countDown();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body =response.body().string();
                System.out.println(body);
            }
        });

//        try {
//            countDownLatch.await();
//            System.out.println("good");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
