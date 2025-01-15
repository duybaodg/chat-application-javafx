package com.example;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Client {
	private static final int PORT = 8080;
	private static final String HOST = "127.0.0.1";
	private static final int PORT_REMOTE = 8080;
	private static final String HOST_REMOTE = "3.27.228.108";
	private static int port;
	private static String host;
	private static Socket socket;
	private static BufferedReader serverInput;
	private static PrintWriter out;
	private static BufferedReader userInput;
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	private static SecretKeySpec secretKey;
	private static Cipher myCipher;
	private static Scanner sc;
	private static boolean stop = true;
	private static String userName;

	public static void connection(String host, int port) throws IOException, NoSuchAlgorithmException {
		byte[] staticKeyBytes = "12345678".getBytes();
		secretKey = new SecretKeySpec(staticKeyBytes, "DES");
		String keyBase64 = Base64.getEncoder().encodeToString(staticKeyBytes);
		try (PrintWriter writer = new PrintWriter(new FileOutputStream("KeyFile.txt"))) {
			writer.println(keyBase64);
			// System.out.println("Static key saved to file.");
		}
		/*
		 * desKey = KeyGenerator.getInstance("DES"); desKey.init(56); secretKey =
		 * desKey.generateKey(); ObjectOutputStream outkey = new ObjectOutputStream(new
		 * FileOutputStream("./KeyFile.txt")); outkey.writeObject(secretKey);
		 * System.out.println("Encryption Key: " +
		 * Base64.getEncoder().encodeToString(secretKey.getEncoded()));
		 * 
		 * // System.out.println(secretKey); outkey.close();
		 */
		try {
			socket = new Socket(host, port);
			System.out.println("Connected to Server");
			serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			userInput = new BufferedReader(new InputStreamReader(System.in));
		} catch (SocketTimeoutException e) {
			System.err.println("Connection timed out: The server did not respond.");
		} catch (IOException e) {
			System.err.println("Failed to connect: " + e.getMessage() + " Please check the server status");
			return;
		}
	}
	public static boolean isServerAvailable(String host, int port) {
	    try (Socket socket = new Socket()) {
	        socket.connect(new InetSocketAddress(host, port), 5000); // 5-second timeout
	        return true;
	    } catch (IOException e) {
	        System.err.println("Server is not available: " + e.getMessage());
	        return false;
	    }
	}

	public static void sendMessagesByDESTest(String input) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		out.println("[DES]" + encryptbyDES(input)); // Send user input to server
	}

	public static void sendMessagesByDES() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		try {
			System.out.println("Connected to the server.");
			while (true) {
				String query = userInput.readLine(); // Get user input
				if (query.equalsIgnoreCase("exit")) {
					out.println(query);
					break;
				}
				out.println("[DES]" + encryptbyDES(query)); // Send user input to server
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendMessagesByCeasarTest(String input) throws IOException {
		System.out.println("Connected to the server.");
		out.println("[Ceasar]" + encryptCodeCeasar(input, 4)); // Send user input to server
	}

	public static void sendMessagesByCeasar() {
		try {
			System.out.println("Connected to the server.");
			while (true) {
				String query = userInput.readLine(); // Get user input
				if (query.equalsIgnoreCase("exit")) {
					out.println(query);
					break;
				}
				out.println("[Ceasar]" + encryptCodeCeasar(query, 4)); // Send user input to server
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String receiveMessagesFX() {
		try {
			String response;
			while ((response = serverInput.readLine()) != null) { // Read server messages
				System.out.println("Raw mess: " + response);
				if (response.contains("[DES]")) {
					String extractDES = response.substring(5);
					// System.out.println("extractDES => "+extractDES);
					String responseDES = decryptCodeByDES(extractDES);
					System.out.println(userName + " :" + responseDES);
					return responseDES;
				} else if (response.contains("[Ceasar]")) {
					String extractCeasar = response.substring(8);
					// System.out.println("extractCeasar => "+extractCeasar);
					String responseCeasar = decryptCodeCaesar(extractCeasar, 4);
					System.out.println(userName + " :" + responseCeasar);
					return responseCeasar;
				}
			}
		} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void receiveMessages() {

		try {
	        if (serverInput == null) {
	            System.err.println("Error: serverInput is null. Cannot receive messages."
	            		+ " Please check your server status and re-run the program");
	            return;
	        }
			String response;
			while ((response = serverInput.readLine()) != null) { // Read server messages
				// System.out.println("Raw mess: " + response);
				if (response.contains("[DES]")) {
					String extractDES = response.substring(5);
					// System.out.println("extractDES => "+extractDES);
					String responseDES = decryptCodeByDES(extractDES);
					System.out.println(userName + " :" + responseDES);
				} else if (response.contains("[Ceasar]")) {
					String extractCeasar = response.substring(8);
					// System.out.println("extractCeasar => "+extractCeasar);
					String responseCeasar = decryptCodeCaesar(extractCeasar, 4);
					System.out.println(userName + " :" + responseCeasar);
				}
			}

		} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			System.out.println("Server issues" + e.getMessage());
		}
	}

	public static String encryptCodeCeasar(String inputType, int key) {
		String lowerCaseInput = inputType.toLowerCase();
		String encrypt = "";
		for (int i = 0; i < lowerCaseInput.length(); i++) {
			char currentChar = lowerCaseInput.charAt(i);
			if (ALPHABET.indexOf(currentChar) != -1) {
				int position = ALPHABET.indexOf(lowerCaseInput.charAt(i));
				int encryptPos = (key + position) % 26;
				if (encryptPos < 0) {
					encryptPos = ALPHABET.length() + encryptPos;
				}
				char encryptChar = ALPHABET.charAt(encryptPos);
				encrypt += encryptChar;
			} else {
				encrypt += currentChar;
			}

		}
		return encrypt;
	}
	public static String encryptbyDES(String inputType) throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		byte[] messToBytes = inputType.getBytes();
		myCipher = Cipher.getInstance("DES");
		myCipher.init(Cipher.ENCRYPT_MODE, secretKey);
		// System.out.println("Key in client:" +secretKey);
		byte[] myEncryptedBytes = myCipher.doFinal(messToBytes);
		String encryptedData = Base64.getEncoder().encodeToString(myEncryptedBytes);
		// System.out.println("Encrypt: "+encryptedData);
		return encryptedData;
	}

	public static String decryptCodeCaesar(String inputType, int key) {
		inputType = inputType.toLowerCase();
		String decrypt = "";
		for (int i = 0; i < inputType.length(); i++) {
			char currentChar = inputType.charAt(i);
			if (ALPHABET.indexOf(currentChar) != -1) {
				int position = ALPHABET.indexOf(inputType.charAt(i));
				int decryptPost = (position - key) % 26;
				if (decryptPost < 0) {
					decryptPost = ALPHABET.length() + decryptPost;
				}
				char decryptChar = ALPHABET.charAt(decryptPost);
				decrypt += decryptChar;
			} else {
				decrypt += currentChar;
			}
		}

		return decrypt;
	}

	public static String decryptCodeByDES(String inputType) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] messToBytes = Base64.getDecoder().decode(inputType);
		myCipher = Cipher.getInstance("DES");
		myCipher.init(Cipher.DECRYPT_MODE, secretKey);
		// System.out.println("key in method:"+secretKey);
		byte[] decryptMess = myCipher.doFinal(messToBytes);
		String decryptData = new String(decryptMess);
		return decryptData;
	}

	public static void start() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		try {
			while (stop == true) {

				sc = new Scanner(System.in);
				System.out.println("Enter your user name: ");
				userName = sc.nextLine();
				System.out.println("Enter the host Address (default is 127.0.0.1): ");
				host = sc.nextLine();
				System.out.println("Enter the port Address (default is 8080): ");
				try {
					port = sc.nextInt();
				} catch (Exception e) {
					System.out.println("port must be a number. Eg: 8080, 9090, Please try again.");
				}
				if(!isServerAvailable(host, port)) {
					System.out.println("Server is not available: Connection refused\n"
							+ "Failed to connect: Connection refused Please check the server status");
				} else if ((host.equals(HOST) && port == PORT) || (host.equals(HOST_REMOTE) && port == PORT_REMOTE)) {
					connection(host, port);
					// Start a thread to handle incoming messages
					new Thread(Client::receiveMessages).start();
					
					// Handle sending messages in the main thread
					sc.nextLine();
					while (true) {
						System.out.println(
								"Please enter option 1 - Encrypt messenger by DES method \n Option 2 - Encrypt messenger by Ceasar method");
						String encryptMethod = sc.nextLine();
						if (encryptMethod.equalsIgnoreCase("1")) {
							System.out.println("Your messenger will be encrypt by DES method");
							sendMessagesByDES();

						} else if (encryptMethod.equalsIgnoreCase("2")) {
							System.out.println("Your messenger will be encrypt by Ceasar method");
							sendMessagesByCeasar();

						} else {
							System.out.println("Please enter 1 or 2");
							continue;
						}
					}
				} else {
					System.out.println("Connection error. Pls enter a valid port number of the server.");
				}
				System.out.println("you can try the default server port number '8080' default local host '127.0.0.1' ");
				System.out.println("type true for continue or false for stop");
				try {
					stop = sc.nextBoolean();
				} catch (Exception e) {
					System.out.println("Invalid input. Please enter 'true' or 'false'.");
					sc.next(); // Consume invalid input to avoid closing the scanner prematurely.
					continue;
				}
				if (stop == false) {
					System.out.println("Exiting...");
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		start();
	}

}
