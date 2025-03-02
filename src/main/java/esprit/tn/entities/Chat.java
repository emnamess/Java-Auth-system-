package esprit.tn.entities;

import java.sql.Timestamp;

public class Chat { private int id;
    private int senderId;
    private int receiverId;
    private String messageText;
    private Timestamp timestamp;
    private String status;
    private String SenderName;

    public Chat(int id, int senderId, int receiverId, String messageText, Timestamp timestamp, String status,String SenderName) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.status = status;
        this.SenderName=SenderName;
    }
    public Chat(int senderId, int receiverId, String messageText) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.status = "sent";
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }
}
