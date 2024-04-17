package org.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Socket socket;
        DataOutputStream out;
        BufferedReader in;
        BufferedReader c;
        c = new BufferedReader(new InputStreamReader(System.in));

        String line;

        try {
            System.out.println("\nClient online!");
            socket = new Socket(args[0], Integer.parseInt(args[1]));
            System.out.println("Socket info from client: " + socket);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            while (true) {
                System.out.print("\nWrite your message here:");
                line = c.readLine();
                out.writeBytes(line + "\n");
                out.flush();

                System.out.println("Sent to the server:" + line);
                line = in.readLine();
                System.out.println("Reception from the server:" + line);
            }
        } catch (IOException ignored) {
        }
    }

}
