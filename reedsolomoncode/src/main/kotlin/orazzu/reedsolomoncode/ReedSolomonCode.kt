package orazzu.reedsolomoncode


class ReedSolomonCode(val codeLen: Int, val errorQuan: Int) {
    val infoLen: Int = codeLen - 2 * errorQuan
    val h: Array<Array<GFNumber>> = Array(2 * errorQuan) { Array(codeLen) { j -> GFNumber(j + 1, codeLen + 1) } }
    val g: Array<Array<GFNumber>> =
        Array(infoLen) { i -> Array(codeLen) { j -> GFNumber(if (i == j || j >= infoLen) 1 else 0, codeLen + 1) } }
    val syndromeError: MutableMap<List<GFNumber>, Array<GFNumber>> = HashMap()


    init {
        initH()
        initG()
        initSyndromeError()
    }


    private fun initH() {
        for (i in 1 until 2 * errorQuan)
            for (j in 0 until codeLen)
                h[i][j] = h[i - 1][j] * h[0][j]
    }


    private fun initG() {
        val ght = Array(2 * errorQuan) { Array(2 * errorQuan + 1) { GFNumber(0, codeLen + 1) } }

        for (i in 0 until infoLen) {
            for (k in 0 until 2 * errorQuan) {
                ght[k][2 * errorQuan] = -h[k][i]

                for (j in infoLen until codeLen)
                    ght[k][j - infoLen] = h[k][j]
            }

            for (k in 0 until 2 * errorQuan) {
                var diag = ght[k][k]
                var j = k + 1
                while (diag.value == 0 && j < 2 * errorQuan) {
                    if (ght[j][k].value != 0) {
                        ght[k] = ght[j].also { ght[j] = ght[k] }
                        diag = ght[k][k]
                    }

                    j++
                }

                if (diag.value == 0)
                    throw IllegalArgumentException("Could not find non-zero diagonal element")

                for (j in 0 until codeLen - infoLen + 1)
                    ght[k][j] /= diag

                for (u in k + 1 until 2 * errorQuan) {
                    val underDiag = ght[u][k]

                    if (underDiag.value != 0) {
                        ght[u][k] = GFNumber(0, codeLen + 1)

                        for (j in k + 1 until codeLen - infoLen + 1)
                            ght[u][j] -= ght[k][j] * underDiag
                    }
                }
            }

            for (k in 0 until 2 * errorQuan - 1) {
                for (u in k + 1 until 2 * errorQuan) {
                    val aboveDiag = ght[k][u]

                    if (aboveDiag.value != 0) {
                        ght[k][u] = GFNumber(0, codeLen + 1)

                        for (j in u + 1 until codeLen - infoLen + 1)
                            ght[k][j] -= ght[u][j] * aboveDiag
                    }
                }
            }

            for (j in infoLen until codeLen)
                g[i][j] = ght[j - infoLen][2 * errorQuan]
        }
    }


    private fun incError(error: Array<GFNumber>) {
        val last =
            if (error.count { it.value != 0 } >= errorQuan)
                error.indexOfLast { it.value != 0 }
            else error.lastIndex

        for (i in last downTo 0) {
            error[i]++

            if (error[i].value != 0)
                break
        }
    }


    private fun initSyndromeError() {
        val error = Array(codeLen) { GFNumber(0, codeLen + 1) }

        do {
            val syndrome = Array(2 * errorQuan) { GFNumber(0, codeLen + 1) }

            for (i in 0 until 2 * errorQuan)
                for (j in 0 until codeLen)
                    syndrome[i] += error[j] * h[i][j]

            syndromeError[syndrome.toList()] = error.clone()

            incError(error)
        } while (error.any { it.value != 0 })
    }


    fun encode(message: Array<GFNumber>): Array<GFNumber> {
        if (message.size != infoLen)
            throw IllegalArgumentException("Wrong info length")

        for (num in message)
            if (num.order != codeLen + 1)
                throw IllegalArgumentException("Wrong order of number")

        val encoded = Array(codeLen) { GFNumber(0, codeLen + 1) }

        for (i in 0 until codeLen)
            for (j in 0 until infoLen)
                encoded[i] += message[j] * g[j][i]

        return encoded
    }


    fun decode(encoded: Array<GFNumber>): Array<GFNumber> {
        if (encoded.size != codeLen)
            throw IllegalArgumentException("Wrong info length")

        for (num in encoded)
            if (num.order != codeLen + 1)
                throw IllegalArgumentException("Wrong order of number")

        var decoded = Array(infoLen) { i -> encoded[i] }
        val syndrome = Array(2 * errorQuan) { GFNumber(0, codeLen + 1) }

        for (i in 0 until 2 * errorQuan)
            for (j in 0 until codeLen)
                syndrome[i] += encoded[j] * h[i][j]

        val error = syndromeError[syndrome.toList()]?: throw IllegalArgumentException("Failed to decode")

        for (i in decoded.indices)
            decoded[i] -= error[i]

        return decoded
    }
}