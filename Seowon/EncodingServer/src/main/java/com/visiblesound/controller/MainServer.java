package com.visiblesound.controller;

import com.visiblesound.model.TestObj;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
public class MainServer {
    final String uploadedFilePath = "src/main/resources/uploaded/";

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

        return "OK";
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
