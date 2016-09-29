package com.company;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ftp_client {

    static boolean loop = true;
    static boolean connected = false;
    static Socket controlConnection = new Socket();
    static DataInputStream inputS = null;
    //static DataOutputStream outputS = null;
    static BufferedWriter outputS = null;

    //String command = "";// = userInput.nextLine();
    static String[] commandsAndParams = null;

    public static void main(String[] args) {

        Scanner userInput = new Scanner(System.in);
        System.out.println("Welcome to the program, please connect to a server.");
        String command  = userInput.nextLine();
        commandsAndParams = command.split(" ");


            while (!connected) {
                    if(commandsAndParams.length == 2) {
                        if (commandsAndParams[0].equals("CONNECT")) {
                            //connect to the server via the provided IP and Port number
                            connected = connect("CONNECT", commandsAndParams[1], 3715);
                        }
                    }
                    else if(commandsAndParams.length == 3) {
                        if (commandsAndParams[0].equals("CONNECT")) {
                            //connect to the server via the provided IP
                            // and Port number
                            connected = connect("CONNECT", commandsAndParams[1],
                                    Integer.parseInt(commandsAndParams[2]));
                        }
                    }
                    else {
                        System.out.println("Please connect to a server before continuing this program." +
                                " We need the command CONNECT followed by a space, a valid IP address, " +
                                "and optionally a port number.");
                    }
                //end of while loop.
            }


        while(loop) {
             System.out.println("Welcome to the program, specify a function with" +
                    " spaces separating the command and any potential parameters");
             command = userInput.nextLine();
             commandsAndParams = command.split(" ");

            try {
                if(commandsAndParams.length == 1){
                    if(commandsAndParams[0].equals("LIST")){
                        System.out.println("LIST RECEIVED");
                        //byte[] outputBuffer = "LIST".getBytes();
                        outputS.write("LIST", 0, 4);
                        //not writing commands to the server correctly, was formerly "LIST".getBytes()



                        byte[] listBuffer = "Response: 225 Data Connection Open.\r\n".getBytes();
                        byte[] wantedResult = ("Response: 225 Data Connection Open.\r\n").getBytes();

                        int totalBytes = 0;
                        int bytesCaught;
                        while(totalBytes < listBuffer.length){
                            bytesCaught = inputS.read(listBuffer, totalBytes, listBuffer.length - totalBytes);
                            totalBytes += bytesCaught;
                        }

                        System.out.println(new String(listBuffer));

                        if(new String(listBuffer).equals(new String(wantedResult))){

                            Socket dataSocket = new Socket(controlConnection.getInetAddress(), 3716);
                            //and with the data socket on the other end not being open
                            // that is making this data socket connection being refused.

                            InputStream listInput = dataSocket.getInputStream();

                            //and do other logic listening for the list and processing it before printing it
                            // ArrayList<String> ourFiles = new ArrayList<>();
                            byte[] word = new byte[1024];

                            while (listInput.read() != -1) {
                                listInput.read(word);
                                //ourFiles.add(new String(word));
                            }

                            System.out.println(new String(word));
                            //not sure if this should be inside the while loop or at the end of it.

                        }
                        else{
                            System.out.println("Data Connection Refused");
                        }

                    }

                    if (commandsAndParams[0].equals("QUIT")) {
                        //close the data connection to the server after telling it to quit itself,
                        byte[] outputBuffer = "QUIT".getBytes();
                        outputS.write("QUIT");//outputBuffer);
                        controlConnection.close(); //not sure how right that is. it is right but a
                        // bit short on other steps which we see in the http server code from lab 4.
                        loop = false;
                    }

                }

                if(commandsAndParams.length == 2) {
                    if (commandsAndParams[0].equals("RETR")) {
                        byte[] outputBuffer = ("RETR " + commandsAndParams[1]).getBytes();
                        if (outputS != null) outputS.write("RETR " + commandsAndParams[1]); //outputBuffer);

                        Socket dataSocket = new Socket(controlConnection.getInetAddress(), 3716);
                        InputStream fileInput = dataSocket.getInputStream();

                        String response = String.valueOf(inputS.read());

                        if (response.equals("RESPONSE: 225")) {
                            File ourFile = new File(commandsAndParams[1]);

                            //BufferedWriter ourFile = new BufferedWriter();
                            while (fileInput.read() != -1) {
                                //ourFile.add(String.valueOf(fileInput.read()));
                            }

                        } else {
                            System.out.println("Something went wrong with retrieve, server didn't respond correctly.");
                        }

//                    while( fileInput.read() != -1)
//                    {
//                        //read the data to a buffer or something,
//                    }
//                        //then right the buffer out to a file once its all present.

                        //.read() will return a -1 once the receiving of data is done so we can use that as our while loop control variable/flag.
//                        if(inputS != null)
//                        {
//                            int results = inputS.read(); //int results so isn't the right data type.
//                        }


                        //EDIT/RESPONSE Don't need a length, just need a while loop that
                        //is running as long as the input stream is receiving data. plus the port number we want to be
                        //receiving the data from/on.
                    }

                    if (commandsAndParams[0].equals("STOR")) {
                        byte[] outputBuffer = ("STOR " + commandsAndParams[1])
                                .getBytes();
                        //theoretically at this point is when we would send the
                        //file or we would send the file once we heard back
                        //from the server that it was ready to receive our file
                        outputS.write("STOR " + commandsAndParams[1]); //outputBuffer);
                        //once the server has been warned and we get a signal back,
                        //then we put out the file itself on the new output stream
                        // of our new data connection, created on the server side?

                        // controlConnection.getInetAddress(); can be used to get the ip address of our FTP server from our control socket.

                        Socket dataSocket = new Socket(controlConnection.getInetAddress(), 3716);
                        OutputStream StoreOutput = dataSocket.getOutputStream(); //feeling like this should be a file output stream but idk how.

                        //write the file to a byte array and then go from there and place it in the write constructor?
                        byte[] fileChoosen = null;
                        //the length of the file chosen byte array can be determined thanks to the file.getBytes.length or whatever method.

                        //while( StoreOutput.write() != -1)
                        {
                            //read the data to a buffer or something,
                        }
                    }

                    if(commandsAndParams[0].equals("PORT")){
                        byte[] outputBuffer = ("PORT " + commandsAndParams[1])
                                .getBytes();

                        if(outputS != null) outputS.write("PORT " + commandsAndParams[1]);//outputBuffer);

                    }
                }
            }
            catch(Exception e){
                System.out.println("Something went wrong, did you input a " +
                        "proper command or proper number of parameters?");
                System.out.println(e.toString());
            }
            //back to the top of the while loop
        }
     //bottom of main.
    }

    static boolean connect(String command, String ipAddr, int port) {
        try {
            controlConnection = new Socket(ipAddr, port); //or port 21
            inputS = new DataInputStream(controlConnection.getInputStream());
            //old way  outputS = new DataOutputStream(controlConnection.getOutputStream());
            outputS = new BufferedWriter( new OutputStreamWriter(controlConnection.getOutputStream()));
            outputS.write(command); //command.getBytes());
            //not sure if output stream is working at all honestly.

            byte[] buffer = "Response: 220 Welcome to JFTP.\r\n".getBytes();
            //the while loop should be rewriting buffer so if the connection code doesn't get sent back then we shouldn't
            //return true about having successfully made a connection.
            byte[] wantedResult = ("Response: 220 Welcome to JFTP.\r\n").getBytes();

            int totalBytes = 0;
            int bytesCaught;
            while(totalBytes < buffer.length){
                bytesCaught = inputS.read(buffer, totalBytes, buffer.length - totalBytes);
                totalBytes += bytesCaught;
            }

            //System.out.println(new String(buffer));
            System.out.println(new String(buffer));

            if(new String(buffer).equals(new String(wantedResult))){
                return true;
            }
            else{
                return false;
            }

        }catch (Exception e) {
            System.out.println("Connection Exception.");
            return false;
        }

    }
//end of class bracket here.
}