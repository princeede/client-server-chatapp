
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
    
    /**
    * Server API -- create, join, list, exit
    * to create and/or join a room, the current room of the user is required
    * user will exit the current room and join/create a new room which will serve as the current room
    * all new messages will be sent to the current room
    * the incoming from the client will have 2 arguments, the current room, and the room to be created or joined
    * to list all rooms, server receives the message --list
    * to exit a room, user just need to send --exit..
    */
    @Override
    public void run() {
        try {
            while(true){
                Message incoming = (Message) input.readObject();
                System.out.println(incoming.message);
                if (incoming.roomName != null) {
                    
                    if(incoming.message.startsWith("--create")){
                        CreateRoom(incoming.message.substring(9), incoming.roomName, incoming.sender);
                    }
                    
                    else if (incoming.message.startsWith("--join")) {
                        JoinRoom(incoming.roomName, incoming.message.substring(7), incoming.sender);
                    }
                    
                    else if (incoming.message.startsWith("--exit")) {
                        ExitRoom(incoming.roomName, incoming.sender);
                    }
                        
                    else {
                        Broadcast(incoming.roomName, incoming.message);
                    }
                    
                } 
                else {
                    if (incoming.message.equals("--list")) {
                         ListAllRooms();
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
    
    /**
    * send incoming to all the members in roomName
    * Add the incoming to the list of messages..
    */
    
    void Broadcast(String roomName, String message) throws IOException{
        RoomManager room = ServerApp.rooms.get(roomName);
        if (room != null) {
            HashSet<DataOutputStream> members = room.GetMembers();
            Iterator<DataOutputStream> it = members.iterator();
        
            while (it.hasNext()) {
                DataOutputStream next = it.next();
                SendResponse(next, "["+roomName+"] "+message);
            }

            room.AddNewMessage(message);
        }
        
        else {
            SendResponse(output, "Room "+roomName+" no longer exist\nEnter \"--list\" to see the list of available rooms.");
        }
    }

    /**
    * List all the active rooms...
    */
    private void ListAllRooms() throws IOException {
        Iterator<String> allRooms = ServerApp.rooms.keySet().iterator();
        while (allRooms.hasNext()) {
            String next = allRooms.next();
            SendResponse(output, next);
        }
    }

    /**
    * Check if newRoom doesn't already exist
    * if not, create newRoom and add the user as a member of the room
    * exit user from its currentRoom.
    */
    private void CreateRoom(String newRoom, String currentRoom, String sender) throws IOException {
        if (ServerApp.rooms.containsKey(newRoom)) {
            SendResponse(output, "Sorry, room "+newRoom+" already exist");
        } else{
            RoomManager room = new RoomManager(newRoom);
            new Thread(room).start();
            ServerApp.rooms.put(newRoom, room);
            SendResponse(output, newRoom+" created succefully.");
            JoinRoom(currentRoom, newRoom, sender);
        }
    }
    
    /**
    * check if roomName exist
    * if yes, check if user is a member of the room
    * if yes, room user from the room.
    */
    private void ExitRoom(String roomName, String sender) throws IOException {
        if (ServerApp.rooms.containsKey(roomName)) {
            
            HashSet<DataOutputStream> members = ServerApp.rooms.get(roomName).GetMembers();
            
            if (members.contains(this.output)) {
                ServerApp.rooms.get(roomName).GetMembers().remove(output);
                SendResponse(output, "You have been removed from room "+roomName);
                Broadcast(roomName, sender+" left...");
                
            } else {
                SendResponse(output, "You are not a member of room "+roomName);
            }
        } else {
            SendResponse(output, roomName+" does not exist..");
        }
    }
    
    /**
    * To join a room, check if the room exist
    * if the room exist, check if user is not already a member
    * if not a member, add user to the room and exit user from its current room..
    */
    private void JoinRoom(String currentRoom, String roomName, String sender) throws IOException {
        if (ServerApp.rooms.containsKey(roomName)) {
            HashSet<DataOutputStream> members = ServerApp.rooms.get(roomName).GetMembers();
            if (!members.contains(this.output)) {
                Broadcast(roomName, sender+" just joind room.");
                ServerApp.rooms.get(roomName).GetMembers().add(output);
                SendResponse(output, "---joined "+roomName);
                ExitRoom(currentRoom, sender);
                
                ArrayList<String> messages = ServerApp.rooms.get(roomName).GetMessages();
                Iterator<String> it = messages.iterator();
                while (it.hasNext()) {
                    String msg = it.next();
                    SendResponse(output, msg);
                }
            }
            else {
                SendResponse(output, "You are already a member of "+roomName);
            }
        } 
        else {
            SendResponse(output, roomName+" does not exist..");
        }
    }
    
}
