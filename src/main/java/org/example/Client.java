package org.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client extends Thread{
    private final String ip;
    private final int port;
    private final int id;
    public volatile boolean running = true;

    Socket socket;
    DataOutputStream out;
    BufferedReader in;

    public Client(String ip,int port,int id){
        this.ip = ip;
        this.port = port;
        this.id = id;
    }

    public void stopRunning(){
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    public void run(){
        try {
            System.out.println("\nClient online!");
            socket = new Socket(ip, port);
            System.out.println("Socket info from client: " + socket);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());

            while (!Thread.currentThread().isInterrupted() && running) {
                int number = in.read();
                if(number%(1+id*2)!=0)continue;
                System.out.println("Client: " + id + " received:" + number);
                out.writeBytes("ACK\n");
                out.flush();
            }
        } catch (IOException ignored) {
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

}
