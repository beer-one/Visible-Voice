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

@RestController
public class MainServer {
    private final String uploadedFilePath = "src/main/resources/uploaded/";
    private final String ffmpegPath = "/usr/bin/ffmpeg";
    private final String ffprobePath = "/usr/bin/ffprobe";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSSSSS");
    
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
    public String getFile(@RequestBody MultipartFile uploadFile) throws Exception {

        MultipartFile file = uploadFile;
        saveFile(file);
        String convertedFilePath = convertFile(file.getOriginalFilename(), "wav");

        Date now = new Date();
        Random rand = new Random();
        String newFileName = dateFormat.format(now) + "."+rand.nextInt(10000)+".wav";
        String renamedFilePath = uploadedFilePath+newFileName;
        renameFile(convertedFilePath,uploadedFilePath+newFileName);
        uploadToBucket(uploadedFilePath+newFileName);

        /** code below needs to be tested !!! */
        //json for athentication required !!!
        //runCommand(new String[] { "python3","transcribe_async.py", "gs://visible_voice/"+String(newFileName) });
        
        //runCommand(new String[] { "python","generate_word_cloud_with_args.py", "한글을 이곳에 입력하면 워드클라우드를 생성합니다" ,String(newFileName)+".png"});
        

        
        return "OK";
    }

    public void uploadToBucket(String fileName){
          // [START storage_upload_file]
          Storage storage = StorageOptions.getDefaultInstance().getService();
          BlobId blobId = BlobId.of("visible_voice", fileName);
          BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("audio/wav").build();
          Blob blob = storage.create(blobInfo, "Hello, Cloud Storage!".getBytes(UTF_8));
          // [END storage_upload_file]
          System.out.println("upload finished");
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
        Process process;
        Process mProcess=null;
        try {
            /* run command*/
            process = Runtime.getRuntime().exec(args);
            mProcess = process;
        } catch (Exception e) {
            /* Exception handling*/
            System.out.println("Exception Raised" + e.toString());
        }
        /* get stdout from the execution*/
        InputStream stdout = mProcess.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println("stdout: " + line);
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
            FFprobe fFprobe = new FFprobe(ffprobePath);

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(uploadedFilePath + filename)
                    .overrideOutputFiles(true)
                    .addOutput(uploadedFilePath + convertFileName)
                    .setFormat(format)
                    .done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, fFprobe);
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
