package edu.seg2105.edu.server.ui;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;

import java.io.IOException;
import java.util.Scanner;

public class ServerConsole implements ChatIF {

    Scanner fromConsole;
    EchoServer server;

    public ServerConsole(int port) {
        server = new EchoServer(port);
        try {
            server.listen();
        } catch (IOException e) {
            System.out.println("Error: Could not listen for clients.");
        }

        fromConsole = new Scanner(System.in);
        Thread consoleThread = new Thread(this::accept);
        consoleThread.start();
    }


    /**
     * This method waits for input from the console. Once it is
     * received, it sends it to the server's message handler.
     * @param message
     */
    @Override
    public void display(String message) {
        System.out.println("SERVER MSG> " + message);
    }

    public void accept() {
        try {
            String message;

            while (true) {
                message = fromConsole.nextLine();

                server.handleMessageFromServerConsole(message);

            }
        } catch (Exception ex) {
            System.out.println("Unexpected error while reading from console!");
        }
    }
}
