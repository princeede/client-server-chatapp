/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 *
 * @author Abdul-Hammid
 */

/**
 *
 * The server stores a collection of all the active rooms
 * The server also creates room "general" by default and add all clients.
 * The server continues to listen for clients and set them up on a new thread when there's a new connection
 * 
 * The default port is 12345, you can always change that in the main method.
 * 
 */
public class ServerApp {
    ServerSocket server;
    static HashMap<String, RoomManager> rooms = new HashMap<>();
    
    ServerApp(int port) throws IOException{
        server = new ServerSocket(port, 10);
        RoomManager general = new RoomManager("general");
        new Thread(general).start();
        rooms.put(general.GetRoomName(), general);
        
        while(true){
            Socket client = server.accept();
            DataOutputStream output = new DataOutputStream(client.getOutputStream());
            output.flush();
            ObjectInputStream input = new ObjectInputStream(client.getInputStream());
            general.GetMembers().add(output);
            System.out.println("New client alert!!!");
            new Thread(new ServerThread(client, output, input)).start();
        }
        
    }
    
    
    
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        new ServerApp(12345);
    }
    
}
