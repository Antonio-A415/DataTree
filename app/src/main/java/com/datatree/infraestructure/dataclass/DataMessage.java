package com.datatree.infraestructure.dataclass;

public class DataMessage {
    private String text;
    private boolean isUser; // true si el mensaje es del usuario, false si es del bot

    public DataMessage(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }
}
