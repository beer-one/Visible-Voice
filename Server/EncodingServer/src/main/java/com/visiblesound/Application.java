package com.visiblesound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;

@SpringBootApplication
public class Application {
    public static void main(String arg[]) {
		initializeFirestore();
		SpringApplication.run(Application.class, arg);
    }

	public static void initializeFirestore() {
		FileInputStream serviceAccount = null;
        try{
            serviceAccount = new FileInputStream("visiblevoice-a4862-firebase-adminsdk-7f6ie-48cf0a7c77.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();
            FirebaseApp.initializeApp(options);
        }catch(Exception e){
            System.out.println("LOG_STT_THREAD_FireBase Init error: "+e.getMessage());
            return;
        }
	}
}
