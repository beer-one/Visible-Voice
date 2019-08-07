package model

class ByteReader(data: ByteArray) {
    val data: ByteArray = data
    var offset: Int = 0
    var size = data.size

    fun readByte(bytes: Int, bigEndian: Boolean): Int {
        var result: Int = 0

        if(bigEndian) {
            for (i in offset..offset + bytes-1) {
                result = result.shl(8) + data[i].toUByte().toInt()
            }
        } else {
            for(i in offset..offset + bytes-1) {
                result += data[i].toUByte().toInt().shl((i-offset)*8)
            }
        }
        offset += bytes
        return result
    }

    fun readByteToString(bytes: Int): String {
        var result = ""

        for(i in offset..offset + bytes-1) {
            result += data[i].toChar()
        }

        offset += bytes

        return result
    }

    fun readWord(bigEndian: Boolean): Int {
        return readByte(4, bigEndian)
    }

    fun readWordToString(): String {
        return readByteToString(4)
    }

    fun isFinished(): Boolean {
        return (offset >= size)
    }

    fun slice(): ByteReader {
        return slice(offset, size-1)
    }

    fun slice(start: Int, end: Int): ByteReader {
        val result = ByteReader(data.sliceArray(IntRange(start, end)))
        offset = end+1
        return result
    }

    override fun toString(): String {
        return """DataSize : ${size}
            |offset : ${offset}
        """.trimMargin()
    }

    fun moveOffset(go: Int) {
        offset += go
    }
}