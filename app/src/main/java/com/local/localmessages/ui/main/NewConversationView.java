package com.local.localmessages.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.local.localmessages.R;
import com.local.localmessages.data.UserRepository;
import com.local.localmessages.services.MessageService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NewConversationView extends AppCompatActivity {
    private static RecyclerView userContainer;
    private Button backButton;
    private TextInputEditText searchUser;
    private static Map<String, Long> allUsers;
    private UserRepository userRepository=new UserRepository();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_conversation);
        userContainer=findViewById(R.id.usersList);
        backButton=findViewById(R.id.backButtonNewConversation);
        searchUser=findViewById(R.id.searchUser);
        allUsers=userRepository.getAllUsers();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Map filteredUsers=getFilteredUsers(charSequence,allUsers);
                refreshView(filteredUsers);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        RecyclerVIewAdapterNewConversation adapter=new RecyclerVIewAdapterNewConversation(this,
                allUsers);
        userContainer.setAdapter(adapter);
        userContainer.setLayoutManager(new LinearLayoutManager(this));
    }

    private void refreshView(Map filtered){
        RecyclerVIewAdapterNewConversation adapter=new RecyclerVIewAdapterNewConversation(this,
                filtered);
        userContainer.setAdapter(adapter);
        userContainer.setLayoutManager(new LinearLayoutManager(this));
    }

    private Map getFilteredUsers(CharSequence userSubString, Map<String, Long> users){
        Map<String, Long> filtered=new HashMap<>();
        Set<String> keySet = users.keySet();
        for(String key:keySet){
            if(key.contains(userSubString)){
                Long l=users.get(key);
                filtered.put(key,l);
            }
        }
        return filtered;
    }
}
