import orazzu.reedsolomoncode.GFNumber
import orazzu.reedsolomoncode.ReedSolomonCode
import kotlin.random.Random


fun messageToFourWords(message: String): List<Array<GFNumber>> {
    val bytes = message.encodeToByteArray().map { it.toUByte() }
    val byteThirds = mutableListOf<Int>()
    for (byte in bytes) {
        val byteInt = byte.toInt()
        byteThirds.add(byteInt shr 5)
        byteThirds.add((byteInt and 0b00011100) shr 2)
        byteThirds.add(byteInt and 0b00000011)
    }
    val gf11Numbers = byteThirds.map { GFNumber(it, 11) }.toMutableList()

    var extraZeros = 0
    while (gf11Numbers.size % 4 != 0) {
        gf11Numbers.add(GFNumber(0, 11))
        extraZeros++
    }

    val fourWords = mutableListOf<Array<GFNumber>>()
    for (i in gf11Numbers.indices step 4)
        fourWords.add(
            arrayOf(
                gf11Numbers[i],
                gf11Numbers[i + 1],
                gf11Numbers[i + 2],
                gf11Numbers[i + 3]
            )
        )
    fourWords.add(arrayOf(GFNumber(extraZeros, 11), GFNumber(0, 11), GFNumber(0, 11), GFNumber(0, 11)))

    return fourWords
}


fun fourWordsToMessage(fourWords: List<Array<GFNumber>>): String {
    val extraZeros = fourWords.last()[0].value
    val gf11Numbers = mutableListOf<GFNumber>()
    for (i in 0 until fourWords.lastIndex)
        for (j in fourWords[i].indices)
            gf11Numbers.add(fourWords[i][j])

    for (i in 0 until extraZeros)
        gf11Numbers.removeLast()

    val byteThirds = gf11Numbers.map { it.value }
    val bytes = mutableListOf<Byte>()
    for (i in byteThirds.indices step 3)
        bytes.add(
            (
                    (byteThirds[i] shl 5) + (byteThirds[i + 1] shl 2) + byteThirds[i + 2]
                    ).toUByte().toByte()
        )
    val byteArray = ByteArray(bytes.size) { i -> bytes[i] }

    return String(byteArray, Charsets.UTF_8)
}


fun main() {
    val rsc = ReedSolomonCode(10, 3)
    val message = "Ab яё 大家好 1?"
    val fourWords = messageToFourWords(message)
    val encoded = fourWords.map { rsc.encode(it) }
    val noised = encoded.map { word ->
        val noisedWord = word.clone()
        for (i in 1..3)
            noisedWord[Random.nextInt(0, 10)] = GFNumber(Random.nextInt(), 11)
        noisedWord
    }
    val decoded = noised.map { rsc.decode(it) }
    val decodedMessage = fourWordsToMessage(decoded)

    println("H")
    rsc.h.forEach { str ->
        str.forEach { num ->
            print(num.value)
            print('\t')
        }
        println()
    }
    println()

    println("G")
    rsc.g.forEach { str ->
        str.forEach { num ->
            print(num.value)
            print('\t')
        }
        println()
    }
    println()

    println("fourWords")
    fourWords.forEach { println(it.toList()) }
    println()

    println("encoded")
    encoded.forEach { println(it.toList()) }
    println()

    println("noised")
    noised.forEach { println(it.toList()) }
    println()

    println("decoded")
    decoded.forEach { println(it.toList()) }
    println()

    println("decoded message")
    println(decodedMessage)
    println()
}