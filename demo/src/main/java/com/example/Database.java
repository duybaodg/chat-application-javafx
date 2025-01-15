package com.example;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Database {


	static GoogleCredentials authExplicit(String jsonPath) throws IOException {
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
				.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
		return credentials;	
	}
	public Firestore getInstance(GoogleCredentials credentials, String projectId) {
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(credentials)
				.setProjectId(projectId)
				.build();
		FirebaseApp.initializeApp(options);
		Firestore db = FirestoreClient.getFirestore();
		return db;
		
	}
	public Database() throws IOException, InterruptedException, ExecutionException {
		GoogleCredentials credentials = authExplicit("/Users/caubanh/Downloads/javafx/demo/src/main/java/com/example/menudatabase-fa7ed-7bac7aa3478f.json");
		Firestore db = getInstance(credentials, "menudatabase-fa7ed");
		
		//db action
		DocumentReference docRef = db.collection("users").document("messenger");
		Map<String, Object> data = new HashMap<>();
		data.put("1", ThreadServerController.getMessData().toString());
		ApiFuture<WriteResult> result = docRef.set(data);
		System.out.println(data.values());
		System.out.println("Update time : " + result.get().getUpdateTime());
		}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		System.out.println("Program begin");
		try {
			new Database();
		} catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("Program end");
	}
	
}
  
