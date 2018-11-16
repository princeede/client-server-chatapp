
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
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
public class ServerThread extends Thread{
    Socket client;
    DataOutputStream output;
    ObjectInputStream input;
    
    ServerThread(Socket client, DataOutputStream output, ObjectInputStream input) throws IOException{
        this.client = client;  
        this.output = output;
        output.flush();
        this.input = input;
    }
    
    //
    //Actions required for the server -- create, join, list, exit
    //to create and/or join a room, the current room of the user is required
    //user will exit the current room and join/create a new room which will serve as the current room
    //all new messages are will bre sent to the current room...
    //the message from the client will have 2 args, the current room, and the room to be created or joined
    //
    //to list all rooms, user just need to send the message '--list'
    //to exit a room, user just need to send --exit..
    //
    @Override
    public void run() {
        try {
            while(true){
                Message message = (Message) input.readObject();
                System.out.println(message.message);
                //store the message in room [general]
                if (message.roomName != null) {
                    
                    if(message.message.startsWith("--create")){
                        CreateRoom(message.message.substring(9), message.roomName);
                    }
                    
                    else if (message.message.startsWith("--join")) {
                        JoinRoom(message.roomName, message.message.substring(7));
                    }
                    
                    else {
                        Broadcast(message.roomName, message.message);
                    }
                    
                } 
                
                else {
                    
                    if (message.message.equals("--list")) {
                         ListAllRooms();
                    } 
                    
                    else {
                        ExitRoom(message.message);
                    }
                }
                
            }
        } catch (IOException e) {
            System.out.println("Error:- "+e.getMessage());
            try {
                client.close();
            } catch (IOException ex) {
                System.out.println("Closing client connection:- "+ex.getMessage());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void SendResponse(DataOutputStream to, String message) throws IOException{
        to.writeUTF(message);
        to.flush();
    }
    
    //
    //send message to all the members in roomName
    //Add the message to the lsit of messages..
    //
    void Broadcast(String roomName, String message) throws IOException{
        RoomManager room = ServerApp.rooms.get(roomName);
        room.GetMessages().add(message);
        HashSet<DataOutputStream> members = room.GetMembers();
        Iterator<DataOutputStream> it = members.iterator();
        
        while (it.hasNext()) {
            DataOutputStream next = it.next();
            SendResponse(next, roomName+": "+message);
        }
    }

    //
    //List all the active rooms...
    //
    private void ListAllRooms() throws IOException {
        Iterator<String> allRooms = ServerApp.rooms.keySet().iterator();
        while (allRooms.hasNext()) {
            String next = allRooms.next();
            SendResponse(output, next);
        }
    }

    //
    //Check if newRoom doesn't already exist
    //if not, create newRoom and add the user as a member of the room
    //exit user from its prevRoom
    private void CreateRoom(String newRoom, String prevRoom) throws IOException {
        if (ServerApp.rooms.containsKey(newRoom)) {
            SendResponse(output, "Sorry, room "+newRoom+" already exist");
        } else{
            RoomManager room = new RoomManager(newRoom, output);
            ServerApp.rooms.put(newRoom, room);
            SendResponse(output, "---created "+newRoom);
            ExitRoom(prevRoom);
            SendResponse(output, newRoom+" created succefully.");
        }
    }
    
    //
    //check if roomName exist
    //if yes, check if user is a member of the room
    //if yes, room user from the room
    //
    private void ExitRoom(String roomName) throws IOException {
        if (ServerApp.rooms.containsKey(roomName)) {
            
            HashSet<DataOutputStream> members = ServerApp.rooms.get(roomName).GetMembers();
            
            if (members.contains(this.output)) {
                ServerApp.rooms.get(roomName).GetMembers().remove(output);
                SendResponse(output, "You have been removed from room "+roomName);
                
            } else {
                SendResponse(output, "You are not a member of room "+roomName);
            }
        } else {
            SendResponse(output, roomName+" does not exist..");
        }
    }
    
    //
    //To join a room, check if the room exist..
    //if the room exist, check if user is not already a member
    //if not a member, add user to the room and exit user from its current room
    //
    private void JoinRoom(String currentRoom, String roomName) throws IOException {
        //If the room exist...
        if (ServerApp.rooms.containsKey(roomName)) {
            HashSet<DataOutputStream> members = ServerApp.rooms.get(roomName).GetMembers();
            //Check to see if user is not a alredy a member
            if (!members.contains(this.output)) {
                ServerApp.rooms.get(roomName).GetMembers().add(output);
                SendResponse(output, "---joined "+roomName);
                ExitRoom(currentRoom);
                SendResponse(output, "You have been added to "+roomName);
                
                ArrayList<String> messages = ServerApp.rooms.get(roomName).GetMessages();
                Iterator<String> it = messages.iterator();
                while (it.hasNext()) {
                    String msg = it.next();
                    SendResponse(output, msg);
                }
            }
            //user already a member...
            else {
                SendResponse(output, "You are already a member of "+roomName);
            }
        } 
        //room does not exist
        else {
            SendResponse(output, roomName+" does not exist..");
        }
    }
    
}
