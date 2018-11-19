
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Abdul-Hammid
 */

/**
 * 
 * The RoomManager keeps track of all room members, messages and the last active and expiry date
 * the last active is the time the last message was sent to the room
 * expiry date is last active date + 7 days
 * if the room is not active for 7 days, the room will be removed from the server
 */
public final class RoomManager implements Runnable{
    private final String roomName;
    private HashSet<DataOutputStream> members = new HashSet<>();
    private ArrayList<String> messages = new ArrayList<>();
    Date lastActive;
    Date expiryDate;
    
    public RoomManager(String roomName, DataOutputStream member) {
            this.roomName = roomName;
            members.add(member);
            SetDate();
    }
    
    public RoomManager(String roomName){
        this.roomName = roomName;
        SetDate();
    }
    
    public String GetRoomName(){
        return this.roomName;
    }
    
    public HashSet<DataOutputStream> GetMembers(){
        return this.members;
    }
    
    public ArrayList<String> GetMessages(){
        return this.messages;
    }
    
    /**
     * API to add a new message to the group..
     * @param message 
     */
    public void AddNewMessage(String message){
        this.messages.add(message);
        SetDate();
    }
    
    private void SetDate(){
        lastActive = new Date();
        expiryDate =new Date(lastActive.getTime() + (7 * 24 * 60 * 60 * 1000));
    }
    
    /**
     * 
     */
    void Watch(){
        while (true) {
            if (expiryDate.before(new Date())) {
                System.out.println(this.roomName+" expired");
                /**
                 * remove the room from the server
                 */
                ServerApp.rooms.remove(this.roomName);
                /**
                 * send a message to all room members that the room has been destroyed.
                 */
                Iterator<DataOutputStream> it = members.iterator();
                while (it.hasNext()) {
                    DataOutputStream next = it.next();
                    try {
                        next.writeUTF(roomName+ " no longer exist, please join another room.");
                        next.flush();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                break;
            } 
            
//            else {
//                long diff = this.expiryDate.getTime() - new Date().getTime();
//                int sec = (int)(diff/1000);
//                if (sec % 5 == 0) {
//                     System.out.println(sec+"seconds to go");
//                }
//            }
        }
    }

    @Override
    public void run() {
        Watch();
    }
    
}
