// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package edu.seg2105.client.backend;

import edu.seg2105.client.common.ChatIF;
import ocsf.client.AbstractClient;

import java.io.IOException;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient {


    //Instance variables **********************************************
    String loginID;
    /**
     * The interface type variable.  It allows the implementation of
     * the display method in the client.
     */
    ChatIF clientUI;


    //Constructors ****************************************************

    /**
     * Constructs an instance of the chat client.
     *
     * @param host     The server to connect to.
     * @param port     The port number to connect on.
     * @param clientUI The interface type variable.
     */

    public ChatClient(String host, int port, ChatIF clientUI, String loginID)
            throws IOException {

        super(host, port);
        this.loginID = loginID;
        this.clientUI = clientUI;
        openConnection();
        sendToServer("#login " + loginID);
        System.out.println(loginID + " has logged on.");
    }


    //Instance methods ************************************************

    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg) {
        clientUI.display(msg.toString());


    }

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message) {
        try {
            if (message.startsWith("#")) {
                handleCommand(message);
            } else {
                sendToServer(message);
                clientUI.display(loginID + "> " + message);
            }
        } catch (IOException e) {
            clientUI.display("Could not send message to server.  Terminating client.");
            quit();
        }
    }

    private void handleCommand(String message) {
        String[] parts = message.split(" ", 2);
        String command = parts[0];
        String argument = (parts.length > 1) ? parts[1] : null;

        switch (command) {
            case "#quit":
                quit();
                break;

            case "#logoff":
                try {
                    closeConnection();
                } catch (IOException ignored) {
                }
                break;

            case "#sethost":
                if (argument != null) {
                    setHost(argument);
                } else {
                    System.out.println("No host provided.");
                }
                break;

            case "#setport":
                if (argument != null) {
                    try {
                        int port = Integer.parseInt(argument);
                        setPort(port);
                        try {
                            closeConnection();
                            openConnection();
                        } catch (IOException e) {
                            System.out.println("Cannot open connection. Awaiting command.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Port must be an integer.");
                    }
                } else {
                    System.out.println("No port provided.");
                }
                break;

            case "#login":
                try {
                    openConnection();
                    sendToServer("#login " + loginID);
                } catch (IOException e) {
                    System.out.println("Cannot open connection. Awaiting command.");
                }
                break;

            case "#gethost":
                System.out.println("Current host: " + getHost());
                break;

            case "#getport":
                System.out.println("Current port: " + getPort());
                break;

            default:
                System.out.println("Invalid command" + command);
                break;
        }
    }

    /**
     * This method terminates the client.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException e) {
        }
        System.exit(0);
    }

    @Override
    protected void connectionClosed() {
        System.out.println("The connection has been closed.");
        System.exit(0);
    }

    @Override
    protected void connectionException(Exception e) {
        System.out.println("Server has stopped responding. Exiting client.");
        System.exit(0);
    }

    public String getLoginID() {
        return loginID;
    }

}

