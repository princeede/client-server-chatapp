
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
    //            System.out.println("Waiting for new message");
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
                    ExitRoom(master.GetCurrentRoom());
                }

                else {
                    try {
                        Message msg = new Message(master.GetCurrentRoom(), "CLIENT: "+message);
                        SendMessage(msg);
                    } catch (IOException ex) {
                        //
                        System.out.println(ex.getMessage());
                    }
                }

            }

        } catch (Exception e) {
        }
        finally{
            try {
                connection.close();
            } catch (IOException e) {
            }
        }
    }
    
    private void SendMessage(Message message) throws IOException{
        output.writeObject(message);
        output.flush();
    }
    
    private void ListRoom() throws IOException{
        SendMessage(new Message(null, "--list"));
    }
    
    private void ExitRoom(String roomName) throws IOException{
        SendMessage(new Message(null, roomName));
    }
    
    private void JoinRoom(String roomName) throws IOException{
        SendMessage(new Message(master.GetCurrentRoom(), roomName));
    }
    
    private void CreateRoom(String roomname) throws IOException {
        SendMessage(new Message(master.GetCurrentRoom(), roomname));
    }
}
