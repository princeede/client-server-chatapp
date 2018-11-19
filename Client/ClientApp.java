/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Abdul-Hammid [princeede]
 */

/**
 *
 * The client connects to the server at the specified ip and port.
 * the current room for the client by default is general
 * client interact with the server thread using the WriteClientThread and ReadClientThread
 *  to write and read from the server.
 * 
 * The default location of the server is localhost...
 * 
 */


public class ClientApp {
    Socket connection;
    private String currentRoom;
    private final String clientName;
    
    ClientApp(String ip, int port, String name) throws IOException{
        connection = new Socket(ip, port);
        SetCurrentRoom("general");
        clientName = name;
        
        new Thread(new WriteClientThread(this)).start();
        new Thread(new ReadClientThread(this)).start();
        System.out.println("You are now in the genral room...");
    }
    
    void SetCurrentRoom(String value){
        currentRoom = value;
    }
    
    String GetCurrentRoom(){
        return currentRoom;
    }
    
    String GetClientName(){
        return clientName;
    }
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        System.out.print("Plaese enter your name to continue: ");
        String name = sc.nextLine();
       new ClientApp("localhost", 12345, name);
    }
    
}
