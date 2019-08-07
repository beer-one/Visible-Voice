
import com.google.cloud.speech.v1.RecognitionAudio
import com.google.cloud.speech.v1.RecognitionConfig
import com.google.cloud.speech.v1.SpeechClient
import com.google.protobuf.ByteString
import model.WAVFile
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.sound.sampled.AudioSystem


internal class HibernateRepositoryTest {

    @Test
    fun `WAVFile can watch wav structure`() {
        val filename = "./src/main/resources/sound/test_30.wav"
        val wavFile = WAVFile(filename)

        print("$wavFile")
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

    @Test
    fun `WAVFile can crop`() {
        val filename = "./src/main/resources/sound/test_30.wav"
        val wavFile = WAVFile(filename)


        // 1초짜리 음성파일로 자름
        wavFile.crop(0, wavFile.sampleRate).save("crop_test.wav")
    }

    @Test
    fun `Apply google Speech to text`() {
        try {
            val speechClient = SpeechClient.create()

            val filename = "./src/main/resources/sound/audio.raw"

            /*
            val wavFile = WAVFile(filename)

            val cropped = wavFile.crop(0, wavFile.sampleRate).toByteArray()
*/

            val data = Files.readAllBytes(Paths.get(filename))
            val audioBytes = ByteString.copyFrom(data)

            val config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setLanguageCode("en-US")
                .build()

            val audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build()

            val response = speechClient.recognize(config, audio)
            val results = response.resultsList

            for(result in results) {
                val alternative = result.alternativesList.get(0)
                println("Transcription: ${alternative.transcript}")
            }
        } catch (e: Exception) {
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

/*
You've successfully set up your project and created the service account,
479413456337-compute, with the role roles/editor.
Next, download the private key you'll use to authenticate your service account. Store this file securely, as anyone with this key can act as the service account to access your GCP resources
 */