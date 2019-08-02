package model

class m4aFile {
    inner class FileType {
        val size: Int
        val ftype: String
        val others: MutableList<Byte>

        constructor(data: ByteArray, size: Int, offset: Int) {
            this.size = size
            others = mutableListOf<Byte>()
            ftype = intToString(readWord(data, offset))

            for(i in (offset+4)..(offset + size - 13))
                others.add(data[i])
        }
    }

    inner class Mdata {
        val size: Int
        val data: MutableList<Byte>

        constructor(data: ByteArray, size: Int, offset: Int) {
            this.size = size
            this.data = mutableListOf()
            for(i in offset..(offset + size - 9)) {
                this.data.add(data[i])
            }
        }
    }

    lateinit var ftype: FileType
    var isFree: Boolean = false
    lateinit var mdat: Mdata

    fun read(data: ByteArray) {
        var offset: Int = 0

        while(offset < data.size) {
            val size = readWord(data, offset)
            offset += 4

            val format = readWord(data, offset)
            offset += 4

            if(size < 8) {
                // 뭔가 이상하다
            } else {
                when(format) {
                    stringToInt("ftyp") -> {
                        ftype = FileType(data, size, offset)
                    }
                    stringToInt("free") -> {
                        isFree = true
                    }
                    stringToInt("mdat") -> {
                        mdat = Mdata(data, size, offset)
                    }
                }
            }
        }
    }

    private fun readWord(data: ByteArray, offset: Int): Int{
        var result: Int = 0

        for(i in offset..offset+3) {
            result = result.shl(8) + data[i].toUByte().toInt()
        }

        return result
    }

    private fun stringToInt(str: String): Int {
        var result: Int = 0

        for(c in str) {
            result = result.shl(8) + c.toInt()
        }

        return result
    }

    private fun intToString(num: Int): String {
        var result = ""

        for(i in 0..3) {
            result += (num.shr(24 - 8*i) % 256).toChar()
        }

        return result
    }
}
