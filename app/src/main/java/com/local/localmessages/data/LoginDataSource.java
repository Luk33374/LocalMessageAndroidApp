package com.local.localmessages.data;

import androidx.annotation.NonNull;

import com.local.localmessages.Config;
import com.local.localmessages.data.model.LoggedInUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.localmessages.dto.UserDTO;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private final OkHttpClient client = new OkHttpClient();
    private static Result<LoggedInUser> loggedInUserResult;
    public Result<LoggedInUser> login(String username, String password) {
        String json ="{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
        RequestBody formBody = RequestBody.create(
                MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .url(Config.API+"/login")
                .post(formBody)
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
                ObjectMapper mapper = new ObjectMapper();
                UserDTO user = mapper.readValue(body,UserDTO.class);
                if(user.getUsername()!=null) {
                    Config.currentUser = new LoggedInUser(String.valueOf(user.getId()), user.getUsername());
                    LoginDataSource.setLoggedInUserResult(Config.currentUser);
                }else
                    LoginDataSource.setLoggedInUserResult(null);
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
            return loggedInUserResult;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return loggedInUserResult;
    }

    private static void setLoggedInUserResult(LoggedInUser user){
        if(user!=null) loggedInUserResult=new Result.Success<>(user);
        else loggedInUserResult= new Result.Error(null);
    }

    public void logout() {
        // TODO: revoke authentication
    }
}