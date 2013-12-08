package com.airtactics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;


public class TCPServer implements Runnable{
	
    public static final String SERVERIP = "127.0.0.1";
    public static final int SERVERPORT = 4444;
         
    public void run() {
         try {
              Log.d("TCP", "S: Connecting...");
              ServerSocket serverSocket = new ServerSocket(SERVERPORT);
              //serverSocket.g
              while (true) {
            	  Socket client = serverSocket.accept();
            	  Log.d("TCP", "S: Receiving...");
            	  try {
                      BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                      String str = in.readLine();
                      Log.d("TCP", "S: Received: '" + str + "'");
                    } catch(Exception e) {
                        Log.e("TCP", "S: Error", e);
                    } finally {
                    	client.close();
                    	Log.d("TCP", "S: Done.");
                    }

              }
              
         } catch (Exception e) {
        	 Log.e("TCP", "S: Error", e);
         }
    }
}

