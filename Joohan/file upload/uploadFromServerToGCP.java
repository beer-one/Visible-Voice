import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import static java.nio.charset.StandardCharsets.UTF_8;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;

public class uploadFromServerToGCP {

        public static void main(String... args) {
          // [START storage_upload_file]
          Storage storage = StorageOptions.getDefaultInstance().getService();
          BlobId blobId = BlobId.of("visible_voice", "fileName");
          BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
          Blob blob = storage.create(blobInfo, "Hello, Cloud Storage!".getBytes(UTF_8));
          // [END storage_upload_file]
        }
      }
