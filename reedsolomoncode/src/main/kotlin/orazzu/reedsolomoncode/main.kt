import orazzu.reedsolomoncode.GFNumber
import orazzu.reedsolomoncode.ReedSolomonCode


fun main(args: Array<String>) {
    println("Hello World!")

    val n = GFNumber(10, 11)
    val m = GFNumber(2, 11)
    println((m - n).value)

    println((GFNumber(3, 11) / GFNumber(5, 11)).value)

    val rsc = ReedSolomonCode(10, 3)
    rsc.h.forEach { str ->
        str.forEach { num ->
            print(num.value)
            print('\t')
        }
        println()
    }

    println()

    rsc.g.forEach { str ->
        str.forEach { num ->
            print(num.value)
            print('\t')
        }
        println()
    }
}