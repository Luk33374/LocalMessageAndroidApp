package com.local.localmessages.data;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.localmessages.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserRepository {
    private final OkHttpClient client = new OkHttpClient();
    private static Map users=new HashMap();

    public Map<String, Long> getAllUsers(){
        Request request = new Request.Builder()
                .url(Config.API+"/allUsers")
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
                ObjectMapper mapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                users = mapper.readValue(body,Map.class);
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
            users.remove(Config.currentUser.getDisplayName());
            users=convert(users);
            return users;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return users;
    }
    private Map<String, Long> convert(Map<String, Integer> map){
        Map<String, Long> convert=new HashMap<>();
        Set<String> set = map.keySet();

        for (String key:set)convert.put(key,new Long(map.get(key)));
        return convert;
    }
}
