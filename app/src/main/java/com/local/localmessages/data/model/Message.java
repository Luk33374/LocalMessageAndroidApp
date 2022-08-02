package com.local.localmessages.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;

import java.time.LocalDate;
import java.time.LocalTime;

public class Message {
    private Long id;
    private String messageContent;
    private Long userId;
    private Long fromUser;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime time;

    public Message() {
    }

    public Message(Long id, String messageContent, Long userId, Long fromUser, LocalDate date, LocalTime time) {
        this.id = id;
        this.messageContent = messageContent;
        this.userId = userId;
        this.fromUser = fromUser;
        this.date = date;
        this.time = time;
    }

    public Long getFromUser() {
        return fromUser;
    }

    public void setFromUser(Long fromUser) {
        this.fromUser = fromUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
