package org.academiadecodigo.hexallents;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by GuessWho on 13/03/2018.
 */
public class Client {

    public final static int DEFAULT_PORT = 6666;
    public final static String DEFAULT_HOST = "localhost";

    // The client socket
    private Socket socket;

    /**
     * Bootstraps the chat client
     *
     * @param args command line arguments
     */
    public static void main(String args[]) {

        String host = "localhost";

        try {

            System.out.println("Trying to establish connection");

            new Menu();

            new Client(DEFAULT_HOST, DEFAULT_PORT);

        } catch (NumberFormatException ex) {

            System.out.println("Invalid port number " + host);
            System.out.print(1);

        }
    }

    /**
     * Connects to the specified hostname/port
     *
     * @param serverName the hostname of the server to connect to
     * @param serverPort the tcp port to connect to
     */
    public Client(String serverName, int serverPort) {

        try {

            // Connect to server
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();

        } catch (UnknownHostException ex) {

            System.out.println("Unknown host: " + ex.getMessage());
            System.exit(1);

        } catch (IOException ex) {

            System.out.println(ex.getMessage());
            System.exit(1);

        }

    }

    // Starts handling messages
    private void start() {

        // Creates a new thread to handle incoming server messages
        Thread thread = new Thread(new ChatRunnable());
        thread.start();

        try {

            BufferedWriter sockOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));

            while (!socket.isClosed()) {

                String consoleMessage = null;

                try {

                    // Blocks waiting for user input
                    consoleMessage = consoleIn.readLine();

                } catch (IOException ex) {
                    System.out.println("Error reading from console: " + ex.getMessage());
                    break;
                }

                if (consoleMessage == null || consoleMessage.equals("/quit")) {
                    break;
                }


                sockOut.write(consoleMessage);
                sockOut.newLine();
                sockOut.flush();

            }

            try {

                consoleIn.close();
                sockOut.close();
                socket.close();

            } catch (IOException ex) {
                System.out.println("Error closing connection: " + ex.getMessage());
            }

        } catch (IOException ex) {

            System.out.println("Error sending message to server: " + ex.getMessage());

        }
    }

    // Runnable to handle incoming messages from the server
    private class ChatRunnable implements Runnable {

        /**
         * @see Thread#run()
         */
        @Override
        public void run() {

            try {

                BufferedReader sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (!socket.isClosed()) {

                    // Block waiting for incoming messages from server
                    String incomingMessage = sockIn.readLine();

                    if (incomingMessage != null) {

                        System.out.println(incomingMessage);

                    } else {

                        try {

                            System.out.println("Connection closed, exiting...");
                            sockIn.close();
                            socket.close();

                        } catch (IOException ex) {
                            System.out.println("Error closing connection: " + ex.getMessage());
                        }

                    }

                }
            } catch (SocketException ex) {
                // Socket closed by other thread, no need for special handling
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
            }

            // Server closed, but main thread blocked in console readline
            System.exit(0);

        }
    }

}
