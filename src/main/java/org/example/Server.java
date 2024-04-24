package org.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = null;
        int port = 5565;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Error on the port:" + port);
            System.exit(-1);
        }
        System.out.println("Server started");
        Socket socket1,socket2;
        Client client1,client2;
        Worker worker1,worker2;
        client1 = new Client("127.0.0.2",port, 1);
        client1.start();

        socket1 = serverSocket.accept();
        worker1 = new Worker(socket1);
        worker1.start();

        client2 = new Client("127.0.0.3",port, 2);
        client2.start();

        socket2 = serverSocket.accept();
        worker2 = new Worker(socket2);
        worker2.start();

        DataOutputStream out1 = new DataOutputStream(socket1.getOutputStream());
        DataOutputStream out2 = new DataOutputStream(socket2.getOutputStream());
        DataOutputStream []outputStreams = {out1,out2};
        Random random = new Random();
        for(int index = 0 ;index<=100;index++){
            Thread.sleep(50);
            int cur = random.nextInt(2);
            System.out.println("Current number: " + index);
            outputStreams[cur].write(index);
            outputStreams[cur].flush();
        }

        client1.stopRunning();
        client1.interrupt();
        client2.stopRunning();
        client2.interrupt();
        worker1.stopRunning();
        worker1.interrupt();
        worker2.stopRunning();
        worker2.interrupt();
    }
}
class Worker extends Thread {
    private final Socket socket;
    public volatile boolean running = true;
    BufferedReader in = null;
    String line;

    public Worker(Socket socket) {
        super("Worker");
        this.socket = socket;
        System.out.println("\nSocket info from server: " + socket);
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

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Client accepted!");

            while (running && !Thread.currentThread().isInterrupted()) {
                line = in.readLine();
                if (line == null) break;
                System.out.println("Received from client: " + line);
            }
        } catch (IOException ignored) {
        } finally {
            try {
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
