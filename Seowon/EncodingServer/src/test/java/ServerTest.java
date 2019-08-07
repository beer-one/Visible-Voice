import org.junit.Test;

public class ServerTest {
    @Test
    public void test() {
        String[] str = "output.m4a".split("\\.");

        for(int i = 0; i < str.length; i++)
            System.out.println(str[i]);


    }
}
