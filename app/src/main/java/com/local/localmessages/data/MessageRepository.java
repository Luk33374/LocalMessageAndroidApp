package com.local.localmessages.data;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.localmessages.Config;
import com.local.localmessages.data.model.Message;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessageRepository {
    private static Message[] messages;
    private final OkHttpClient client = new OkHttpClient();

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

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body =response.body().string();
                //body="{\"message\":"+body+"}";
                ObjectMapper mapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
                Message[] message = mapper.readValue(body,Message[].class);
                if(message.length>0) {
                    setMessages(message);
                    countDownLatch.countDown();
                }else

                countDownLatch.countDown();
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

    private static void setMessages(Message[] messages) {
        MessageRepository.messages = messages;
    }
}
