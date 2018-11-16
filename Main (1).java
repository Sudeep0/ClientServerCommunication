//Author: Sudeep Neupane
//Device: Macbook Pro i5


package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            //Create a ServerSocket
            ServerSocket serverSocket = new ServerSocket(9999);
            BufferedReader infile = new BufferedReader(new FileReader("/Users/sudeepneupane/IdeaProjects/Project3/account.txt"));
            //Read the file
            String file = infile.readLine();
            String add;
            while ((add = infile.readLine()) != null) {
                file = file + ("\n" + add);
            }
            System.out.println(file);
            int i = 0;
            //Condition for more less than 5
            while (i < 5) {
                Socket socket = serverSocket.accept();
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                DataInputStream input = new DataInputStream(socket.getInputStream());
                new Thread(new multithreading(socket, output, input)).start();
                System.out.println("Connection Established");
                //socket.close();
            }
            // Condition when it is more than  5 users
            while (i == 5) {
                Socket socket = serverSocket.accept();
                DataOutputStream response = new DataOutputStream(socket.getOutputStream());
                DataInputStream input = new DataInputStream(socket.getInputStream());
                response.writeUTF("Number of clients limited");
                socket.close();
            }
        } //Catch the unnecessary exception
        catch (IOException e) {
            System.out.println("Exception:" + e.getMessage());

        }


    }

}