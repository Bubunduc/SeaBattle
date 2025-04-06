package com.seabattle.Course_work.models;

public class Message {
    public String getAction() {
        return action;
    }

    String action = "chat";
    String sender;
    String message;
    String game_owner;
    public Message(String sender, String message, String game_owner) {
        this.sender = sender;
        this.message = message;
        this.game_owner = game_owner;
    }
    public String getGame_owner() {
        return game_owner;
    }

    public void setGame_owner(String game_owner) {
        this.game_owner = game_owner;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
