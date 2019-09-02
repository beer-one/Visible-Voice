package com.visiblesound.controller;

import com.visiblesound.model.TestObj;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.nio.charset.StandardCharsets;

// google client lib
import static java.nio.charset.StandardCharsets.UTF_8;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.apache.commons.io.IOUtils;

@RestController
public class MainServer {
    private final String uploadedFilePath = "upload/";
    private final String ffmpegPath = "/usr/bin/ffmpeg";
    private final String ffprobePath = "/usr/bin/ffprobe";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSSSSS");
    
    @RequestMapping("/")
    public String helloServer() {
        return "Hello world!!";
    }

    @GetMapping("/test")
    @ResponseBody
    public TestObj getTestObj(@RequestParam(name="id") int id,
                              @RequestParam(name="name") String name) {
        return new TestObj(id, name);
    }

    @RequestMapping(value="upload", method=RequestMethod.POST)
    public @ResponseBody byte[] getFile(@RequestBody MultipartFile uploadFile) throws Exception {

        MultipartFile file = uploadFile;
        saveFile(file);
        String convertedFilePath = convertFile(file.getOriginalFilename(), "flac");
        Date now = new Date();
        Random rand = new Random();
        String newFileNameBase = dateFormat.format(now) + "_"+rand.nextInt(10000);
        String newFileName = newFileNameBase + ".flac";
        renameFile(convertedFilePath,uploadedFilePath+newFileName);
        System.out.println("newFileName: "+newFileName);

        //rename
        //runCommand(new String[] {"python" ,"src/main/java/com/visiblesound/controller/upload_from_server_to_GCP.py",uploadedFilePath+newFileName,"upload/"+newFileName}); 

        //run Speech 2 Text 
        //python src/main/java/com/visiblesound/controller/transcribe_async.py gs://visible_voice/out09.flac
        //runCommand(new String[] { "python","src/main/java/com/visiblesound/controller/transcribe_async.py", "gs://visible_voice/upload/"+newFileName });
        
        //make Wordclout PNG
        //python src/main/java/com/visiblesound/controller/generate_word_cloud_with_args.py /upload/results/out09.json /upload/results/fileNameHere.png
        //runCommand(new String[] { "python","src/main/java/com/visiblesound/controller/generate_word_cloud_with_args.py", "upload/results/"+newFileNameBase+".json"  ,"upload/results/"+ newFileNameBase + ".png"});

        
        //client will rename the files once the client gets files

        //code below needs to be tested
        // InputStream json = getClass().getResourceAsStream("upload/results/"+newFileNameBase+".json");
        // InputStream PNG = getClass().getResourceAsStream("upload/results/"+ newFileNameBase + ".png");
        
        InputStream json = getClass().getResourceAsStream("/home/ubuntu/2019SWChallenge/Seowon/EncodingServer/upload/results/2019_08_25_05_56_42_000288_8748.json");
        System.out.println("json: "+json);
        //InputStream PNG = getClass().getResourceAsStream("upload/results/2019_08_25_05_56_42_000288_8748.png");
        
        System.out.println("return value: "+IOUtils.toByteArray(json));
        System.out.println("sending json and PNG to client");
        return IOUtils.toByteArray(json);
        //return new byte[][] { IOUtils.toByteArray(json) , IOUtils.toByteArray(PNG) };
    }

    


    public boolean renameFile(String sourceFileName, String destFileName){

        System.out.println("sourceFileName: "+sourceFileName);
        System.out.println("destFileName: "+destFileName);
        File f1 = new File(sourceFileName);
        File f2 = new File(destFileName);

        boolean b = f1.renameTo(f2);

        if (!b) {
            System.err.println("Rename Failed");
        }
        return b;
    }

    public void runCommand(String [] args) {
        Process process = null;
       
        try {
            /* run command*/
            System.out.println("running command ["+args[0]+" "+ args[1]+"]");
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

    public String convertFile(String filename, String format) {
        String convertFileName = filename.split("\\.")[0] + "." + format;
        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
            FFprobe ffprobe = new FFprobe(ffprobePath);

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(uploadedFilePath + filename)
                    .overrideOutputFiles(true)
                    .addOutput(uploadedFilePath + convertFileName)
                    .setAudioChannels(1)
		    .setAudioSampleRate(16000) //(16_000)???
		    .setFormat(format)
                    .done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            executor.createJob(builder).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uploadedFilePath + convertFileName;
    }


    public void saveFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = file.getInputStream();
            File newFile = new File(uploadedFilePath + filename);

            if (newFile.exists()) newFile.createNewFile();

            outputStream = new FileOutputStream(newFile);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
