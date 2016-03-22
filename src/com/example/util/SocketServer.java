package com.example.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
	/*
	 * socket server implement, note that this program should run on a computer instead of android mobile.
	 * so you will have to copy this file to another directory, and run it on your computer alone.
	 */
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static InputStreamReader inputStreamReader;
	private static BufferedReader bufferedReader;
	private static String message;
	

	public static void main(String[] args) {
	
	    try {
	        serverSocket = new ServerSocket(8080);  //Server socket
	
	    } catch (IOException e) {
	        System.out.println("Could not listen on port: 8080");
	    }
	
	    System.out.println("Server started. Listening to the port 8080");
	
	    while (true) {
	        try {
	
	            clientSocket = serverSocket.accept();   //accept the client connection
	            inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
	            bufferedReader = new BufferedReader(inputStreamReader); //get client msg                    
	            message = bufferedReader.readLine();
	            
	            processMessage();
	            
	            inputStreamReader.close();
	            clientSocket.close();
	            
	        } catch (IOException ex) {
	            System.out.println("Problem in message reading");
	        }
	    }
     }

	private static void processMessage() {
		/*
		 * process the received message.
		 */
		
		System.out.println(message);
	}
}  


