package com.example.praya.inclass13;

/*
InClass13
Prayas Rode and Jacob Stern
*/

public class Email {
    String sender, message, timestamp;
    boolean read;

    public Email(String sender, String message, String timestamp, boolean read) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public Email() {
    }
}
