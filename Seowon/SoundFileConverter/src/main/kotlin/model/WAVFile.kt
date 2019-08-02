package model

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter

class WAVFile {
    var totalSize: Int = 0
    var format: String = ""
    var fmtSize: Int = 0
    var audioFormat: Short = 0
    var numChannel: Short = 0
    var sampleRate: Int = 0
    var byteRate: Int = 0
    var blockRate: Short = 0
    var bitPerSample: Short = 0
    var listSize: Int = 0
    var listFormat: String = ""
    var informations = mutableMapOf<String, String>()
    var dataSize: Int = 0
    lateinit var data: ByteReader

    constructor(totalSize: Int, format: String, fmtSize: Int, audioFormat: Short, numChannel: Short,
                sampleRate: Int, byteRate: Int, blockRate: Short, bitPerSample: Short,
                listSize: Int, listFormat: String,
                informations: MutableMap<String, String>, dataSize: Int, data: ByteArray) {
        this.totalSize = totalSize
        this.format = format
        this.fmtSize = fmtSize
        this.audioFormat = audioFormat
        this.numChannel = numChannel
        this.sampleRate = sampleRate
        this.byteRate = byteRate
        this.blockRate = blockRate
        this.bitPerSample = bitPerSample
        this.listSize = listSize
        this.listFormat = listFormat
        this.informations = informations
        this.dataSize = dataSize
        this.data = ByteReader(data)
    }

    constructor(filename: String) {
        val byteReader = ByteReader(FileInputStream(File(filename)).readBytes())

        while(!byteReader.isFinished()) {
            val id = byteReader.readWordToString()

            when (id) {
                "RIFF" -> {
                    totalSize = byteReader.readWord(false)
                    format = byteReader.readWordToString()
                }

                "fmt " -> {
                    fmtSize = byteReader.readWord(false)
                    audioFormat = byteReader.readByte(2, false).toShort()
                    numChannel = byteReader.readByte(2, false).toShort()
                    sampleRate = byteReader.readWord(false)
                    byteRate = byteReader.readWord(false)
                    blockRate = byteReader.readByte(2, false).toShort()
                    bitPerSample = byteReader.readByte(2, false).toShort()
                }

                "LIST" -> {
                    listSize = byteReader.readWord(false)
                    var lsize = listSize
                    while (lsize > 0) {
                        listFormat = byteReader.readWordToString()
                        lsize -= 4
                        when (listFormat) {
                            "INFO" -> {
                                val key = byteReader.readWordToString()
                                lsize -= 4
                                val valueSize = byteReader.readWord(false)
                                lsize -= 4
                                val value = byteReader.readByteToString(valueSize)
                                lsize -= valueSize
                                informations.put(key, value)
                            }
                        }
                    }
                }

                "data" -> {
                    dataSize = byteReader.readWord(false)
                    data = byteReader.slice()
                }
            }
        }
    }

    override fun toString(): String {
        var info = ""
        for((k, v) in informations) {
            info += "${k} : ${v}\n"
        }

        return """TotalSize : ${totalSize}
            |File format : ${format}
            |fmt size : ${fmtSize}
            |Audio format : ${audioFormat}
            |Number of channels : ${numChannel}
            |Sample rate : ${sampleRate}
            |Byte rate : ${byteRate}
            |Block rate : ${blockRate}
            |Bit per sample : ${bitPerSample}
            |Informations
            |${info}
            |Audio data size : ${dataSize}""".trimMargin()
    }

    fun save(filename: String) {
        try {
            val fos = FileOutputStream(File(filename))
            fos.write(this.toByteArray())
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toByteArray(): ByteArray {
        val list = mutableListOf<Byte>()

        list.addAll("RIFF".toByteArray().toList())
        list.addAll(totalSize.toByteArray().toList())
        list.addAll(format.toByteArray().toList())

        list.addAll("fmt ".toByteArray().toList())
        list.addAll(fmtSize.toByteArray().toList())
        list.addAll(audioFormat.toByteArray().toList())
        list.addAll(numChannel.toByteArray().toList())
        list.addAll(sampleRate.toByteArray().toList())
        list.addAll(byteRate.toByteArray().toList())
        list.addAll(blockRate.toByteArray().toList())
        list.addAll(bitPerSample.toByteArray().toList())

        list.addAll("LIST".toByteArray().toList())
        list.addAll(listSize.toByteArray().toList())
        list.addAll(listFormat.toByteArray().toList())
        for((k, v) in informations) {
            list.addAll(k.toByteArray().toList())
            list.addAll(v.length.toByteArray().toList())
            list.addAll(v.toByteArray().toList())
        }

        list.addAll("data".toByteArray().toList())
        list.addAll(data.size.toByteArray().toList())
        list.addAll(data.data.toList())

        return list.toByteArray()
    }

    /*
     totalSize: Int, format: String, fmtSize: Int, audioFormat: Short, numChannel: Short,
                sampleRate: Int, byteRate: Int, blockRate: Short, bitPerSample: Short,
                listSize: Int, listFormat: String,
                informations: MutableMap<String, String>, dataSize: Int, data: ByteArray*/

    fun crop(startSample: Int, endSample: Int): WAVFile {
        val startBit = startSample * numChannel * bitPerSample / 8
        val endBit = (endSample+1) * numChannel * bitPerSample / 8 - 1
        val cropped = data.slice(startBit, endBit)


        return WAVFile(totalSize = totalSize - data.size + cropped.size, format = format, fmtSize = fmtSize,
            audioFormat = audioFormat, numChannel = numChannel, sampleRate = sampleRate, byteRate = byteRate,
            blockRate = blockRate, bitPerSample = bitPerSample, listSize = listSize, listFormat = listFormat,
            informations = informations, dataSize = cropped.size, data = cropped.data)
    }

}

fun Int.toByteArray(): ByteArray {
    val byteArray = ByteArray(4)
    var n = this
    for(i in 0..3) {
        byteArray[i] = (n % 256).toByte()
        n = n.shr(8)
    }
    return byteArray
}

fun Short.toByteArray(): ByteArray {
    val byteArray = ByteArray(2)
    var n = this
    for(i in 0..1) {
        byteArray[i] = (n % 256).toByte()
        n = n.toInt().shr(8).toShort()
    }
    return byteArray
}