
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

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
 * The WriteClientThread is how the client writes to the server
 * it provides the API for creating, exiting, joining and listing available rooms
 * 
 * To join a room, the client starts the line with "--join " followed by the name of the room
 * To create a room, the client starts the line with "--create " followed by the name of the room
 * To exit a room, the client starts the line with "--exit " and it will exit the current room
 * To create a room, the client starts the line with "--create " followed by the name of the room
 * To list a room, the client starts the line with "--list".
 */

public class WriteClientThread implements Runnable{
    ClientApp master;
    Socket connection;
    ObjectOutputStream output;
    
    WriteClientThread(ClientApp client) throws IOException{
        this.master = client;
        connection = master.connection;
        output = new ObjectOutputStream(connection.getOutputStream());
    }
    
    @Override
    public void run() {
        try {
            while(true){
                Scanner sc = new Scanner(System.in);
                String message = sc.nextLine();

                if (message.startsWith("--create")) {
                    CreateRoom(message);
                }

                else if (message.startsWith("--join")){
                    JoinRoom(message);
                }

                else if(message.equals("--list")){
                    ListRoom();
                }

                else if (message.startsWith("--exit")){
                    ExitRoom();
                }

                else {
                    try {
                        SendMessage(new Message(master.GetCurrentRoom(), master.GetClientName()+": "+message));
                    } catch (IOException ex) {
                        //
                        System.out.println(ex.getMessage());
                    }
                }

            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        finally{
            try {
                connection.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    /**
     * 
     * @param message
     * @throws IOException
     * The only way client can write to the server is through the Message object.
     */
    
    private void SendMessage(Message message) throws IOException{
        output.writeObject(message);
        output.flush();
    }

    /**
     * 
     * To list a room, you only need to sent the message "--list" to the server.
     * @throws IOException 
     */
    private void ListRoom() throws IOException{
        SendMessage(new Message(null, "--list"));
    }
    
    /**
     * 
     * To exit a room, just send a message "--exit" and you will exit the your current room
     * @throws IOException 
     */
    private void ExitRoom() throws IOException{
        SendMessage(new Message(master.GetCurrentRoom(), "--exit", master.GetClientName()));
    }
    
    /**
     * 
     * @param roomName
     * To join a room, you need to specify the name of the room you want to join
     * The Message object sends you current room, the new room you want to join and you name... 
     * @throws IOException 
     */
    private void JoinRoom(String roomName) throws IOException{
        SendMessage(new Message(master.GetCurrentRoom(), roomName, master.GetClientName()));
    }
    
    /**
     * 
     * @param roomName
     * To create a new room, specify the name of the room you want to create
     * The message sends your current room and the name of the new room to create... 
     * @throws IOException 
     */
    private void CreateRoom(String roomName) throws IOException {
        SendMessage(new Message(master.GetCurrentRoom(), roomName, master.GetClientName()));
    }
}
