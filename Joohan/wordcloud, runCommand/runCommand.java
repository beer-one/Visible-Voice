import java.io.*;
import java.nio.charset.StandardCharsets;

/** 
 * referenced from: 
 * https://www.edureka.co/community/358/how-to-execute-a-python-file-with-few-arguments-in-java
 * by DragonLord999 
*/

public class runCommand {
    Process mProcess;

    public void run(String [] args) {
        Process process;
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

    public static void main(String[] args) {
        runCommand scriptPython = new runCommand();
        scriptPython.run(new String[] { "python","generate_word_cloud_with_args.py", "한글인풋을 여기에 주황색 입어 주황색 입력합니다", "output.png" });
    }
}
