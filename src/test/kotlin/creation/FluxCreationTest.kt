package creation

import TestUtils.Companion.HELLO
import TestUtils.Companion.PAYLOAD
import TestUtils.Companion.WORLD
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import stepVerify

class FluxCreationTest {

    @Test
    fun `flux by 'just'`() {
        Flux.just(PAYLOAD)
                .stepVerify()
                .expectNext(PAYLOAD)
                .verifyComplete()
    }

    @Test
    fun `flux by 'fromIterable'`() {
        Flux.fromIterable(listOf(HELLO, WORLD))
                .stepVerify()
                .expectNext(HELLO)
                .expectNext(WORLD)
                .verifyComplete()
    }

    @Test
    fun `flux by extension`() {
        listOf(HELLO, WORLD)
                .toFlux()
                .stepVerify()
                .expectNext(HELLO)
                .expectNext(WORLD)
                .verifyComplete()
    }

    @Test
    fun `flux by 'range'`() {
        Flux.range(0, 10)
                .stepVerify()
                .expectNextCount(10)
                .verifyComplete()
    }

    @Test
    fun `flux by 'empty'`() {
        Flux.empty<String>()
                .stepVerify()
                .expectNextCount(0)
                .verifyComplete()
    }

    @Test
    fun `flux by 'error'`() {
        val exception = IllegalArgumentException()

        Flux.error<String>(exception)
                .stepVerify()
                .expectNextCount(0)
                .verifyError(exception::class.java)
    }
}