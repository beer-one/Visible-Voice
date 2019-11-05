package com.visiblesound.controller;

import com.visiblesound.model.TestObj;
import com.visiblesound.model.FileInfo;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.util.HashMap;
import java.util.Map;

// google client lib
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import com.visiblesound.job.SpeechToTextThread;

@RestController
public class MainServer {
	public static final String storagePath = "/home/vvuser/";
    @RequestMapping("/")
    public String helloServer() {
		
		//testing, delete below later
		//System.out.println("running helloServer");

		System.out.println("Hello");
        return "Hello world!!";
    }

    @GetMapping("/test")
    @ResponseBody
    public TestObj getTestObj(@RequestParam(name="id") int id,
                              @RequestParam(name="name") String name) {
        return new TestObj(id, name);
    }

	@PostMapping("/totext")
	@ResponseBody
	public String toTextPost(FileInfo f) {

		System.out.println("[toTextPost ]");

		File existFile = new File(storagePath + f.getUsername() + "/" + f.getFilename());
		System.out.println("FILENAME: " + f.getFilename());

		if(existFile.isFile()) {
			System.out.println("File exist, run STT Thread");
			SpeechToTextThread stt = new SpeechToTextThread(f.getUsername(), f.getFilename());
			stt.start();
			return "OK";
		} else {
			System.out.println("Not exist file");
			return "Fail";
		}

		//System.out.println("[running helloServer]");
		//SpeechToTextThread.sendNotification();
	}

	/*
	@GetMapping("/totext")
	public String toText(
			@RequestParam(value = "username") String username,
			@RequestParam(value = "filename") String filename) {
        File existFile = new File(storagePath + username + "/" + filename);
        System.out.println("FILENAME: " + filename);

        if(existFile.isFile()) {
            System.out.println("File exist, run STT Thread");
            SpeechToTextThread stt = new SpeechToTextThread(username, filename);
            stt.start();
            return "OK";
        } else {
            System.out.println("Not exist file");
            return "Fail";
        }   
    } */  

	@PostMapping("/posttest")
	@ResponseBody
	public FileInfo postTest(FileInfo f) {
		System.out.println(f.getUsername() + " " + f.getFilename());
		return f;
	}

}
