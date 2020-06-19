package com.visiblesound.job;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.*;

import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.UTF_8;

//gcp bucket upload
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;


//firebase
import com.google.firebase.*;
import com.google.firebase.auth.*;
import com.google.auth.oauth2.*;
import com.google.firebase.database.*;
import com.google.firebase.messaging.*;
import com.google.cloud.firestore.*;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import com.google.api.core.ApiFuture;
import com.google.firebase.cloud.FirestoreClient;

public class SpeechToTextThread extends Thread {
	private String username;
	private String filename;
	private boolean flag;

    private static final String ffmpegPath = "/usr/bin/ffmpeg";
	private static final String ffprobePath = "/usr/bin/ffprobe";
	private static final String formatName = "flac";
	private static final String storagePath = "/home/vvuser/";


	public SpeechToTextThread(String username, String filename, boolean flag) {
		this.username = username;
		this.filename = filename;
		this.flag = flag;
	}

	public void run() {
		if(flag) {
			String convertedFileName = convertFile(username, filename);
			System.out.println("LOG_STT_THREAD: " + "convert to flac");

			runCommand(new String[] { "python", "src/main/java/com/visiblesound/gcp/upload_from_server_to_GCP.py", storagePath + username + "/" + convertedFileName, username + "/" + convertedFileName });
	
			System.out.println("LOG_STT_THREAD: " + "upload to gc");

        	// Request Speech 2 Text
        	runCommand(new String[] { "python", "src/main/java/com/visiblesound/gcp/transcribe_async.py", "gs://visible_voice/", username, convertedFileName });
			System.out.println("LOG_STT_THREAD: " + "stt success!!");

			runCommand(new String[] { "python", "src/main/java/com/visiblesound/wordcloud/generate_word_cloud_with_args.py", storagePath + username + "/" + filename.split("\\.")[0] + ".json", storagePath + username + "/" + filename.split("\\.")[0] + ".png"});
		}

        sendNotification();
        System.out.println("LOG_STT_THREAD: " + "sent notification!!!");
	}

    public void sendNotification(){

        System.out.println("LOG_STT_THREAD: running sendNotification");
        //init firebase
		Firestore db = FirestoreClient.getFirestore();
		DocumentReference docRef = db.collection("users").document(username);	
        ApiFuture<DocumentSnapshot> future = docRef.get();

		try {
			DocumentSnapshot document = future.get();
			String token = "";
        	if (document.exists()) {
            	System.out.println("Document data: " + document.getData());
            	token = document.getData().get("deviceToken").toString();
            	System.out.println(token);

        	} else {
            	System.out.println("No such document!");
				return;
        	}
			
			Message message = Message.builder()
                .putData("json", filename.split("\\.")[0] + ".json")
                .putData("png", filename.split("\\.")[0] + ".png")
                .putData("music", filename)
				.setToken(token)
                .build();

			System.out.println("Json: "+ filename.split("\\.")[0] + ".json");
			System.out.println("Png: "+ filename.split("\\.")[0] + ".png");

        	String response = FirebaseMessaging.getInstance().send(message);
			System.out.println("Response: " + response);

		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (ExecutionException e) {
            e.printStackTrace();
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

	public String convertFile(String username, String filename) {
        String convertFileName = filename.split("\\.")[0] + "." + formatName;
        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
            FFprobe ffprobe = new FFprobe(ffprobePath);

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(storagePath + username + "/" + filename)
                    .overrideOutputFiles(true)
                    .addOutput(storagePath + username + "/" + convertFileName)
                    .setAudioChannels(1)
            .setAudioSampleRate(16000) //(16_000)???
            .setFormat(formatName)
                    .done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            executor.createJob(builder).run();
			runCommand(new String[] { "chmod", "757" ,(storagePath + username + "/" + convertFileName) } );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertFileName;
    }

	public void runCommand(String [] args) {
        Process process = null;

        try {
            /* run command*/
			System.out.print("running command [");
			for(int i = 0; i < args.length; i++)
            	System.out.print(args[i] + " ");
			System.out.println("]\n");
            process = Runtime.getRuntime().exec(args);

        } catch (Exception e) {
            /* Exception handling*/
            System.out.println("Exception Raised" + e.toString());
        }

        /* get stdout from the execution*/
        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();
        BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));
        BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(stderr, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = stdOutReader.readLine()) != null) {
                System.out.println("stdout: " + line);
            }
            while ((line = stdErrReader.readLine()) != null) {
                System.out.println("stderr: " + line);
            }
        } catch (IOException e) {
            /* Exception handling*/
            System.out.println("Exception in reading output" + e.toString());
        }


    }
}

