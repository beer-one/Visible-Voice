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
// google client lib
import static java.nio.charset.StandardCharsets.UTF_8;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@RestController
public class MainServer {
    final String uploadedFilePath = "src/main/resources/uploaded/";
    final String ffmpegPath = "/usr/bin/ffmpeg";
    final String ffprobePath = "/usr/bin/ffprobe";

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
        convertFile(file.getOriginalFilename(), "wav");
        uploadToBucket(file.getOriginalFilename());

        return "OK";
    }

    public void uploadToBucket(String fileName){
          // [START storage_upload_file]
          Storage storage = StorageOptions.getDefaultInstance().getService();
          BlobId blobId = BlobId.of("visible_voice", fileName);
          BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
          Blob blob = storage.create(blobInfo, "Hello, Cloud Storage!".getBytes(UTF_8));
          // [END storage_upload_file]
          System.out.println("upload finished");
    }

    public void convertFile(String filename, String format) {
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
