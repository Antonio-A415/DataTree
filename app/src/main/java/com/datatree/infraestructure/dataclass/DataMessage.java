package com.datatree.infraestructure.dataclass;

public class DataMessage {
    private String text;
    private boolean isUser;
    private String avatarUrl;       // Imagen del usuario/bot (fija)
    private String messageImageUrl; // Imagen adjunta en el mensaje
    private long timestamp;

    public DataMessage(String text, boolean isUser, String avatarUrl, String messageImageUrl, long timestamp) {
        this.text = text;
        this.isUser = isUser;
        this.avatarUrl = avatarUrl;
        this.messageImageUrl = messageImageUrl;
        this.timestamp = timestamp;
    }

    // Constructor rápido para texto sin imagen adjunta
    public DataMessage(String text, boolean isUser, String avatarUrl, long timestamp) {
        this(text, isUser, avatarUrl, null, timestamp);
    }

    public String getText() { return text; }
    public boolean isUser() { return isUser; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getMessageImageUrl() { return messageImageUrl; }
    public long getTimestamp() { return timestamp; }
}
