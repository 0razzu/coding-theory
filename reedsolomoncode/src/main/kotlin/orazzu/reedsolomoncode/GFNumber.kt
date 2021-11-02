package orazzu.reedsolomoncode


class GFNumber(val value: Int, val order: Int) {
    private fun checkOrders(other: GFNumber) {
        if (order != this.order)
            throw IllegalArgumentException("Orders must be equal")
    }


    operator fun plus(other: GFNumber): GFNumber {
        checkOrders(other)

        return GFNumber((value + other.value) % order, order)
    }


    operator fun minus(other: GFNumber): GFNumber {
        checkOrders(other)

        return GFNumber((value - other.value + order) % order, order)
    }


    operator fun times(other: GFNumber): GFNumber {
        checkOrders(other)

        return GFNumber((value * other.value) % order, order)
    }


    operator fun div(other: GFNumber): GFNumber {
        checkOrders(other)

        var inverse = other.value
        while ((inverse * other.value) % order != 1)
            inverse = (inverse * other.value) % order

        return GFNumber((value * inverse) % order, order)
    }


    operator fun unaryMinus(): GFNumber {
        return GFNumber(order - value % order, order)
    }


    override fun toString(): String {
        return "$value($order)"
    }
}