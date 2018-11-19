
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Abdul-Hammid
 */
public class Message implements Serializable{
    String roomName;
    String message;
    String sender;

    public Message(String room, String message) {
        this.roomName = room;
        this.message = message;
    }
    
    public Message(String room, String message, String sender) {
        this.roomName = room;
        this.message = message;
        this.sender = sender;
    }
}
