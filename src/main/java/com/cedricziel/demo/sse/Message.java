package com.cedricziel.demo.sse;

import org.hibernate.validator.constraints.NotEmpty;

public class Message {

    @NotEmpty
    private String from;

    @NotEmpty
    private String message;

    public Message(String from, String message) {

        this.from = from;
        this.message = message;
    }

    public Message() {
    }

    @Override
    public String toString() {

        return "Message{" +
                "from='" + from + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
