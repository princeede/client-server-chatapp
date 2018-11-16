/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Abdul-Hammid
 */
public class ClientApp {
    Socket connection;
    private String currentRoom;
    
    ClientApp() throws IOException{
        connection = new Socket("localhost", 12345);
        SetCurrentRoom("general");
        
        Thread thread1 = new Thread(new WriteClientThread(this));
        thread1.start();
        Thread thread2 = new Thread(new ReadClientThread(this));
        thread2.start();
    }
    
    void SetCurrentRoom(String value){
        currentRoom = value;
    }
    
    String GetCurrentRoom(){
        return currentRoom;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        new ClientApp();
    }
    
}
