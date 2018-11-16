
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Abdul-Hammid
 */
public class RoomManager {
    private final String roomName;
    private HashSet<DataOutputStream> members = new HashSet<>();
    private ArrayList<String> messages = new ArrayList<>();
    
    
    public RoomManager(String roomName, DataOutputStream member) {
            this.roomName = roomName;
            members.add(member);
    }
    
    public RoomManager(String roomName){
        this.roomName = roomName;
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
    
}
