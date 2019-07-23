import model.m4aFile
import java.io.*
import javax.sound.sampled.*
import org.apache.commons.codec.binary.Base64


fun main() {

    val filename = "./src/main/resources/sound/sample.m4a"


    val input = FileInputStream(File(filename))

    val result = input.readBytes()


    val m4a = m4aFile()

    m4a.read(result)

    println("${m4a}")

    m4a.printMdat(1,300)
}

