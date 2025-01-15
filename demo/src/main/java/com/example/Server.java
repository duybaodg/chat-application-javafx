package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class Server {
	public static void main(String[] args) {

		int port = 8080;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server is listening on port " + port);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("Client Connect ...");
				new ThreadServerController(socket).start();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
