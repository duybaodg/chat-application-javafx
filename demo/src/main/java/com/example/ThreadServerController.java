package com.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.PrintWriter;
import java.net.Socket;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class ThreadServerController extends Thread {
	private Socket socket;
	private static ArrayList<PrintWriter> msgStore = new ArrayList<>();
	private BufferedReader in;
	private PrintWriter out;
	public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	private static SecretKeySpec secretKey;
	private static Cipher myCipher;
	private static Map<PrintWriter, String> messData = new HashMap<>();
	
	public static Map<PrintWriter, String> getMessData() {
		return messData;
	}
	public static void setMessData(Map<PrintWriter, String> messData) {
		ThreadServerController.messData = messData;
	}
	public ThreadServerController(Socket socket) {
		this.socket = socket;
	}
	@Override
	public void run() {
		try {
			InputStream inputStream = ThreadServerController.class.getResourceAsStream("KeyFile.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String keyBase64 = reader.readLine();
			reader.close();
			byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
			secretKey = new SecretKeySpec(keyBytes, "DES");
			System.out.println("Static key loaded successfully.");
			/*
			 * ObjectInputStream inKey = new ObjectInputStream(new
			 * FileInputStream("./KeyFile.txt")); secretKey = (Key)inKey.readObject();
			 * //System.out.println("key:"+secretKey.toString());
			 * System.out.println("Encryption Key: " +
			 * Base64.getEncoder().encodeToString(secretKey.getEncoded())); inKey.close();
			 */
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			msgStore.add(out); // Add this client's writer to the shared list

			String messenger;
			while ((messenger = in.readLine()) != null) { // Continuously read messages
				System.out.println("messenger from client: " + messenger);
				if (messenger.startsWith("[DES]")) {
					String messengerExtract = messenger.substring(5);
					// System.out.println("messengerExtract=> "+messengerExtract);
					String decryptMess = decryptCodeByDES(messengerExtract);
					System.out.println("decryptMessDES =>" + decryptMess);

					String encryptCeasear = encryptCodeCeasar(decryptMess, 4);
					sendMessToAllClient("[Ceasar]" + encryptCeasear, out);

					// System.out.println("Method "+ "[Ceasar]"+encryptCeasear);

				} else if (messenger.startsWith("[Ceasar]")) {
					String messengerExtract = messenger.substring(8);
					// System.out.println(messengerExtract);
					String decryptMess = decryptCodeCaesar(messengerExtract, 4);
					// System.out.println("decryptMessCeasar => "+ decryptMess);
					String encryptDES = encryptbyDES(decryptMess);

					System.out.println("encryptDES=> " + encryptDES);
					sendMessToAllClient("[DES]" + encryptDES, out);

					System.out.println("method " + "[DES]" + encryptDES);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();

		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
			try {
				synchronized (msgStore) {
					msgStore.remove(out); // Remove the client's writer on disconnect
				}
				// in.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendMessToAllClient(String messenger, PrintWriter client) throws InterruptedException, ExecutionException {
		for (PrintWriter writer : msgStore) {
			if (client != writer) {
				writer.println(messenger);
				messData.put(writer, messenger);
				System.out.println(messData.values());
				writer.flush();
			}

		}
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

}
