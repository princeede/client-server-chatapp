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
import java.util.HashSet;

/**
 *
 * @author Abdul-Hammid
 */
public class ServerApp {
    ServerSocket server;
    
    
    //Store the outputstream of all clients in HashSet...
    static HashSet<DataOutputStream> clients = new HashSet<>(); 
    static HashMap<String, RoomManager> rooms = new HashMap<>();
    
    ServerApp() throws IOException{
        //setup the server at port 12345
        //supporting up to 10 concurrent clients...
        server = new ServerSocket(12345, 10);
        RoomManager general = new RoomManager("general");
        
        //add general to the list of rooms...
        rooms.put("general", general);
        
        while(true){
            Socket client = server.accept();
            //add the new client to the list of clients..
            DataOutputStream output = new DataOutputStream(client.getOutputStream());
            output.flush();
            
            ObjectInputStream input = new ObjectInputStream(client.getInputStream());
            clients.add(output);
            
            //add the cleint to the geenral room by default...
            general.GetMembers().add(output);
            
            
            
            System.out.println("New client alert, total now: "+clients.size());
            
            //create a new thread to handle the connection
            new Thread(new ServerThread(client, output, input)).start();
        }
        
    }
    
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        new ServerApp();
    }
    
}
