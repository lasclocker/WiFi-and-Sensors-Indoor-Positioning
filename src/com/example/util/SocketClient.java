package com.example.util;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.Toast;

import com.example.activity.MainActivity;
import com.example.activity.SocketActivity;

public class SocketClient {
	/*
	 * socket client implement, this program should run on an android mobile.
	 */
	
	static Socket client;
	static PrintWriter printwriter;
	static Timer timer;
	static TimerTask timertask;
	
	static String IP = "192.168.2.101";
	static int port = 8080;
	
	static String message = "";
	static boolean sendOK = true;
	static boolean conncetSucessfully = true;
	
	private static MainActivity parent;
	

	
	public static void connectServer(MainActivity parent) {
		/*
		 * connect server.
		 */
		
		SocketClient.parent = parent;
		
		sendOK = true;
		conncetSucessfully = true;
		
		IP = SocketActivity.getSocketIP();
		port = SocketActivity.getSocketPort();
		
		timer = new Timer();
		timertask = new TimerTask() { 
		    @Override
		    public void run() {
	    		try {
	    			client = new Socket();  
	    			client.connect(new InetSocketAddress(IP, port), 1000); 
	    			printwriter = new PrintWriter(client.getOutputStream(),true);
	    			message = ObtainStepData.getStepMessage();
	    			printwriter.write(message); 
	    			printwriter.flush();
	    			printwriter.close();
	    			client.close();  
	    			if (conncetSucessfully) {
	    				callRunOnUiThread("Conncet Sucessfully");
	    				conncetSucessfully = false;
	    			}
	    		} catch (final Exception e) {
	    			callRunOnUiThread(e.toString());
	    			e.printStackTrace();
	    			disconnectServer();
	    			sendOK = false;
	    		}
		    }
		};
	    timer.schedule(timertask, 1000,1000);
	}
	
	public static void disconnectServer() {
		/*
		 * disconnect server.
		 */
		if (timer != null && timertask != null) {
			timer.cancel();
			timertask.cancel();
			timer = null;
			timertask = null;
		}
	}
	
	public static void callRunOnUiThread(final String data) {
		/*
		 * show the connect information of the socket.
		 */
		parent.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(parent.getBaseContext(), data, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public static String getSocketIP() {
		/*
		 * get the IP of network socket.
		 */
		return IP;
	}
	
	public static int getSocketPort() {
		/*
		 * get the port of network socket.
		 */
		return port;
	}

	public static boolean sendSucess() {
		/*
		 * judge it sends successfully or not.
		 */
		return sendOK;
	}

	public static String getToSendMessage() {
		/*
		 * get the message which will send to the server.
		 */
		return message;
	}

}
