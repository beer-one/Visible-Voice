package model

class m4aFile {
    inner class Header {
        var size: Int = 0
        var ftype: String = ""
        var others: MutableList<Byte> = mutableListOf<Byte>()
        override fun toString(): String {
            return """size: ${size}
                |ftype: ${ftype}
            """.trimMargin()
        }
    }

    val header:Header = Header()
    var mdatSize: Int = 0
    var mdat: MutableList<Byte> = mutableListOf()
    var moovSize: Int = 0
    var moov: MutableList<Byte> = mutableListOf()

    fun read(data: ByteArray) {
        var i: Int = 0
        while (i < data.size) {
            var size = 0
            for (j in 0..3) {
                size *= 256
                size += data[i].toUByte().toInt()
                i++
            }
            println(size)

            size -= 4
            var format: Int = 0
            for (j in 1..4) {
                format *= 256
                format += data[i].toUByte().toInt()
                i++
                //println(format)
            }
            size -= 4

            if (format == 0x66747970) {  // "ftyp"
                header.size = size + 8
                for (j in 1..4) {
                    header.ftype += data[i].toChar()
                    i++
                }
                for(j in 5..size) {
                    header.others.add(data[i])
                    i++
                }
            } else if (format == 0x66726565) { // "free"

            } else if (format == 0x6D646174) { // "mdat"
                if(size > 0) { // mdat <size> [datas]
                    mdatSize = size + 8
                    for (j in 9..mdatSize) {
                        mdat.add(data[i])
                        i++
                    }
                } else { // <00000001> mdat <00000000> <size> []
                    i += 4
                    var size2 = 0
                    for (j in 1..4) {
                        size2 *= 256
                        size2 += data[i].toUByte().toInt()
                        i++
                    }
                    mdatSize = size2
                    for (j in 17..mdatSize) {
                        mdat.add(data[i])
                        i++
                    }
                }
            } else if(format == 0x6D6F6F76) { //moov
                moovSize = size + 8
                for (j in 9..moovSize) {
                    moov.add(data[i])
                    i++
                }
            }
        }

    }

    override fun toString(): String {
        return """<HEADER>
            |${header}
            |<mdat>
            |size: ${mdatSize}
            |<moov>
            |size: ${moovSize}
        """.trimMargin()
    }

    fun printMdat(s: Int, e: Int) {
        var cnt: Int = 1
        for(i in s..e) {
            print("${mdat[i]} ")
            if(cnt % 16 == 0) println("")
            cnt++
        }
    }
}
