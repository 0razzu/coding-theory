package orazzu.reedsolomoncode


class GFNumber(value: Int, val order: Int) {
    val value: Int = (value + ((order - value) / order) * order) % order


    private fun checkOrders(other: GFNumber) {
        if (order != this.order)
            throw IllegalArgumentException("Orders must be equal")
    }


    operator fun plus(other: GFNumber): GFNumber {
        checkOrders(other)

        return GFNumber(value + other.value, order)
    }


    operator fun minus(other: GFNumber): GFNumber {
        checkOrders(other)

        return GFNumber(value - other.value, order)
    }


    operator fun times(other: GFNumber): GFNumber {
        checkOrders(other)

        return GFNumber(value * other.value, order)
    }


    operator fun div(other: GFNumber): GFNumber {
        checkOrders(other)

        if (other.value == 0)
            throw ArithmeticException("/ by zero")

        var inverse = other.value
        while ((inverse * other.value) % order != 1)
            inverse = (inverse * other.value) % order

        return GFNumber(value * inverse, order)
    }


    operator fun unaryMinus(): GFNumber {
        return GFNumber(-value, order)
    }


    override fun toString(): String {
        return "$value($order)"
    }
}