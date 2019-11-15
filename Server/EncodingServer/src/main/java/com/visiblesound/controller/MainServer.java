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
	public final int BUFFER_SIZE = 1024;
    public byte buffer1[] = new byte[BUFFER_SIZE];
    public byte buffer2[] = new byte[BUFFER_SIZE];

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

		String filename = storagePath + f.getUsername() + "/" + f.getFilename();
		File existFile = new File(filename);
		System.out.println("FILENAME: " + f.getFilename());
		int extensionPoint = filename.lastIndexOf(".");
		String samenameFilename = filename.substring(0, extensionPoint - 6) + filename.substring(extensionPoint);
		File samenameFile = new File(samenameFilename);

		if(existFile.isFile()) {
			if(filename.matches(".*__temp.*")) {
				if(isSameFile(existFile, samenameFile)) {
					existFile.delete();
					if(isCompleted(filename)) {
						SpeechToTextThread stt = new SpeechToTextThread(f.getUsername(), samenameFile.getName(), false);
						stt.start();
						return "EXIST";
					}
					SpeechToTextThread stt = new SpeechToTextThread(f.getUsername(), samenameFile.getName(), true);
					stt.start();
					return "OK";
				} else {
					existFile.renameTo(samenameFile);
					SpeechToTextThread stt = new SpeechToTextThread(f.getUsername(), samenameFile.getName(), true);
					stt.start();
					return "OK";
				}
			}
			System.out.println("File exist, run STT Thread");
			SpeechToTextThread stt = new SpeechToTextThread(f.getUsername(), f.getFilename(), true);
			stt.start();
			return "OK";
		} else {
			System.out.println("Not exist file");
			return "Fail";
		}

	}

	public boolean isCompleted(String filename) {
		int extensionPoint = filename.lastIndexOf(".");
		String jsonFilename = filename.substring(0, extensionPoint) + ".json";
		String pngFilename = filename.substring(0, extensionPoint) + ".png";

		File jsonFile = new File(jsonFilename);
		File pngFile = new File(pngFilename);

		return jsonFile.exists() && pngFile.exists();
	}

	public boolean isSameFile(File file, File sameFile) {

        try {
            FileInputStream fio = new FileInputStream(file);
            FileInputStream sameFio = new FileInputStream(sameFile);

            while(true) {
                int len1 = fio.read(buffer1);
                int len2 = fio.read(buffer2);
                
                if(len1 == -1 && len2 == -1) break;
                
                for(int i = 0; i < len1; i++) {
                    if(buffer1[i] != buffer2[i]) return false;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
	
	}
}
