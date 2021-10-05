package creation

import TestUtils.Companion.PAYLOAD
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import stepVerify

class MonoCreationTest {

    @Test
    fun `mono by 'just'`() {
        Mono.just(PAYLOAD)
                .stepVerify()
                .expectNext(PAYLOAD)
                .verifyComplete()
    }

    @Test
    fun `mono by 'empty'`() {
        Mono.empty<String>()
                .stepVerify()
                .expectNextCount(0)
                .verifyComplete()
    }

    @Test
    fun `mono by 'justOrEmpty'`() {
        Mono.justOrEmpty<String>(null)
                .stepVerify()
                .expectNextCount(0)
                .verifyComplete()
    }

    @Test
    fun `mono by extension`() {
        PAYLOAD
                .toMono()
                .stepVerify()
                .expectNext(PAYLOAD)
                .verifyComplete()
    }

    @Test
    fun `mono by 'error'`() {
        val exception = IllegalArgumentException()
        Mono.error<String>(exception)
                .stepVerify()
                .expectNextCount(0)
                .verifyError(exception::class.java)
    }
}