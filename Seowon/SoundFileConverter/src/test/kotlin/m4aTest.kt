import model.ByteReader
import model.WAVFile
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.sound.sampled.AudioSystem

internal class HibernateRepositoryTest {

    @Test
    fun `wav structure`() {
        val filename = "./src/main/resources/sound/test_30.wav"
        val wavFile = WAVFile(filename)

        print("$wavFile")

        //wavFile.save("test.wav")
        wavFile.crop(0, 88200).save("1sec.wav")
    }

    @Test
    fun `wav file with library`() {
        try {
            val stream = AudioSystem.getAudioInputStream(File("./src/main/resources/sound/test_30.wav"))
            val clip = AudioSystem.getClip()
            clip.open(stream)
            println("Frame length: ${clip.frameLength}")
            println("Microsecond length: ${clip.microsecondLength}")
            //clip.

        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    fun mean(list: MutableList<Short>, degree: Double): Double {
        var result: Double = 0.0
        val size = list.size.toDouble()
        for(i in list) {
            result += Math.pow(Math.abs(i.toDouble()), degree) / size
        }
        return result
    }

    fun sAbs(num: Short): Short {
        if(num < 0) return (-num).toShort()
        else return num
    }
}
