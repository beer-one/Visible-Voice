import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.junit.Test;

import java.io.FileInputStream;

public class ServerTest {

    /*
    public void test() {
        //init firebase
        InputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream("visiblevoice-a4862-firebase-adminsdk-7f6ie-de22ce514a.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        GoogleCredentials credentials = null;
        try {
            credentials = GoogleCredentials.fromStream(serviceAccount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);

        Firestore db = FirestoreClient.getFirestore();



// asynchronously retrieve the document
        DocumentReference frankDocRef = db.collection("users").document("gygacpu@naver.com");


        ApiFuture<DocumentSnapshot> future = frankDocRef.get();
// ...
// future.get() blocks on response

        DocumentSnapshot document = null;
        try {
            document = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String token = "";
        if (document.exists()) {
            System.out.println("Document data: " + document.getData());
            token = document.getData().get("deviceToken").toString();
            System.out.println(token);

        } else {
            System.out.println("No such document!");
        }

        Message message = Message.builder()
                .putData("json", "JSON")
                .putData("png", "png")
                .setToken(token)
                .build();

        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

        System.out.println(response);

    }
    */
    @Test
    public void test() {
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
