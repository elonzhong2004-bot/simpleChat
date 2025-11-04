package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com


import edu.seg2105.edu.server.ui.ServerConsole;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer {
    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port) {
        super(port);
    }


    //Instance methods ************************************************

    /**
     * This method handles any messages received from the client.
     *
     * @param msg    The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient
    (Object msg, ConnectionToClient client) {

        System.out.println("Message received: " + msg + " from " + client.getInfo("loginID"));

        if (msg.toString().startsWith("#login")) {
            if (client.getInfo("loginID") != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                client.setInfo("loginID", msg.toString().substring(7));
            }
            System.out.println(client.getInfo("loginID") + " has logged in");
        }
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
     */
    protected void serverStarted() {
        System.out.println
                ("Server listening for connections on port " + getPort());
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped() {
        System.out.println
                ("Server has stopped listening for connections.");
    }

    protected void clientConnected(ConnectionToClient client) {
        System.out.println("A new client has connected to the server.");
    }

    protected void clientDisconnected(
            ConnectionToClient client) {
        System.out.println(client.getInfo("loginID") + " has disconnected");
    }

    //Class methods ***************************************************

    /**
     * This method is responsible for the creation of
     * the server instance (there is no UI in this phase).
     *
     * @param args The port number to listen on.  Defaults to 5555
     *             if no argument is entered.
     */
    public static void main(String[] args) {
        int port = 0;

        try {
            port = Integer.parseInt(args[0]);
        } catch (Throwable t) {
            port = DEFAULT_PORT;
        }

        new ServerConsole(port);


    }

    public void handleMessageFromServerConsole(String message) {
        if (message.startsWith("#")) {
            handleCommand(message);
        } else {
            sendToAllClients("SERVER MSG>" + message);
            System.out.println("SERVER MSG> " + message);
        }
    }

    private void handleCommand(String message) {
        String[] parts = message.split(" ", 2);
        String command = parts[0];
        String argument = (parts.length > 1) ? parts[1] : null;

        switch (command) {
            case "#quit":
                try {
                    close();
                } catch (IOException ignored) {
                }
                System.exit(0);
                break;

            case "#stop":
                stopListening();
                break;

            case "#close":
                try {
                    close();
                } catch (IOException e) {
                    System.out.println("Could not close server.");
                }
                break;

            case "#setport":
                if (argument != null) {
                    setPort(Integer.parseInt(argument));
                    stopListening();
                } else {
                    System.out.println("No port provided.");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isListening()) {
                    try {
                        listen();
                    } catch (IOException e) {
                        System.out.println("Could not listen for clients.");
                    }
                } else {
                    System.out.println("Server is already listening for clients.");
                }
                break;

            case "#start":
                if (!isListening()) {
                    try {
                        listen();
                    } catch (IOException e) {
                        System.out.println("Could not listen for clients.");
                    }
                } else {
                    System.out.println("Server is already listening for clients.");
                }
                break;

            case "#getport":
                System.out.println("Current port: " + getPort());
                break;

            default:
                System.out.println("Invalid command: " + command);
                break;
        }
    }
}

