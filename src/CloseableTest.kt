import java.io.Closeable

fun main(args: Array<String>) {
    println("begin")
    test1()
    println("end\n")

    println("begin")
    try {
        test2()
    } catch (e: Exception) {
    }
    println("end\n")

    println("begin")
    test3()
    println("end\n")

    println("begin")
    test4()
    println("end\n")

    println("begin")
    test5()
    println("end\n")
}

class TestCloseable(private val name: String) : Closeable {

    init {
        println("$name: init")
    }

    fun success() {
    }

    fun error() {
        throw Exception()
    }

    override fun close() {
        println("$name: close")
    }

}

fun test1() {
    TestCloseable("Test 1").use {
        it.success()
    }
}

fun test2() {
    TestCloseable("Test 2").use {
        it.error()
    }
}

fun test3() {
    listOf(1, 2, 3).forEach { i: Int ->
        TestCloseable("Test 3-$i").use { testCloseable: TestCloseable ->
            if (i >= 2) return@forEach
            testCloseable.success()
        }
    }
}

fun test4() {
    listOf(1, 2, 3).forEach { i: Int ->
        TestCloseable("Test 4-$i").use { testCloseable: TestCloseable ->
            if (i >= 2) return@test4
            testCloseable.success()
        }
    }
}


inline fun <T1 : Closeable?, T2 : Closeable?, R> Pair<T1, T2>.use(block: (T1, T2) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this.first, this.second)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        if (this.second != null) this.second.closeFinally(exception)
        if (this.first != null) this.first.closeFinally(exception)
    }
}

fun Closeable?.closeFinally(cause: Throwable?) {
    when {
        this == null -> {}
        cause == null -> close()
        else ->
            try {
                close()
            } catch (closeException: Throwable) {
                cause.addSuppressed(closeException)
            }
    }
}

fun test5() {
    val a = TestCloseable("Test 5-a")
    val b = TestCloseable("Test 5-b")
    Pair(a, b).use { a: TestCloseable, b: TestCloseable ->
        a.success()
        b.success()
    }
}
