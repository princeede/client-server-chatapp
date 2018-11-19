
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

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
 * ReadClientThread reads from the server and display it to the client
 * It also provides the API to change clients currentRoom anytime a client joins new a room.
 * 
 */
public class ReadClientThread extends Thread {
    DataInputStream input;
    Socket client;
    ClientApp master;
    
    
    ReadClientThread(ClientApp master) throws IOException{
        this.master = master;
        this.client = master.connection;
        this.input = new DataInputStream(client.getInputStream());
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                String message = input.readUTF();
                
                if (message.startsWith("---joined")) {
                    master.SetCurrentRoom(message.substring(10));
                }

                else {
                    System.out.println(message);
                }
            }
            
        } catch (IOException e) {
            System.out.println("Error:- "+e.getMessage());
        } 
        finally{
            try {
                client.close();
            } catch (IOException e) {
            }
        }
    }
    
}
