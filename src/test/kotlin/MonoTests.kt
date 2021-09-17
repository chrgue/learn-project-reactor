import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class MonoTests {

    @Test
    fun `create simple`() {
        Mono.just("hello")
            .stepVerify()
            .expectNext("hello")
            .verifyComplete()
    }

    @Test
    fun `create empty`() {
        Mono.empty<String>()
            .stepVerify()
            .verifyComplete()
    }

    @Test
    fun `create via justOrEmpty`() {
        Mono.justOrEmpty<String>(null)
            .stepVerify()
            .verifyComplete()
    }

    @Test
    fun `create via extension function`() {
        "hello"
            .toMono()
            .stepVerify()
            .expectNext("hello")
            .verifyComplete()
    }

    @Test
    fun `map to upper case`() {
        "hello"
            .toMono()
            .map { it.uppercase() }
            .stepVerify()
            .expectNext("HELLO")
            .verifyComplete()
    }

    @Test
    fun `flapMap with db`() {
        val fromDB = "something".toMono().map { it.uppercase() }

        "hello"
            .toMono()
            .flatMap { fromUpStream -> fromDB.map { "$fromUpStream - $it" } }
            .stepVerify()
            .expectNext("hello - SOMETHING")
            .verifyComplete()
    }

    @Test
    fun `filter starts with 'h'`() {
        "hello"
            .toMono()
            .filter { it.startsWith("h") }
            .map { it.uppercase() }
            .stepVerify()
            .expectNext("HELLO")
            .verifyComplete()
    }

    @Test
    fun `throw exception`() {
        val dbPublisher = "something".toMono().map { throw IllegalArgumentException() }

        "hello"
            .toMono()
            .flatMap { dbPublisher }
            .stepVerify()
            .verifyError(IllegalArgumentException::class.java)
    }


    @Test
    fun `consume twice`() {

        val dbPublisher = "something".toMono().map { it.uppercase() }

        dbPublisher
            .concatWith(dbPublisher)
            .stepVerify()
            .expectNext("SOMETHING")
            .expectNext("SOMETHING")
            .verifyComplete()
    }
}