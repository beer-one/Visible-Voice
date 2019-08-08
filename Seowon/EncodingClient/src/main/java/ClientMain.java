
import okhttp3.*;

import java.io.*;

public class ClientMain {
    static String url = "http://localhost:8080/upload";
    static String filename = "src/main/resources/eg1.m4a";

    public static void main(String[] args) {
        File file = new File(filename);

        send2Server(file);

    }

    static void send2Server(File file) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uploadFile", file.getName(), RequestBody.create(MultipartBody.FORM, file))
                .build();


        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException
            {
                System.out.printf("TEST : " , response.body().string());
            }
        });
    }
}
