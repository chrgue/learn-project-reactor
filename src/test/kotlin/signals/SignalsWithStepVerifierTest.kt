package signals

import TestUtils.Companion.PAYLOAD
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import stepVerify

class SignalsWithStepVerifierTest {

    @Test
    fun `mono executes onNext & onComplete once`() {
        Mono.just(PAYLOAD)
                .stepVerify()
                .expectNextCount(1)
                .verifyComplete()
    }

    @Test
    fun `flux executes onNext for every item & onComplete once`() {
        val items = listOf(PAYLOAD, PAYLOAD, PAYLOAD)

        Flux.fromIterable(items)
                .stepVerify()
                .expectNextCount(items.size.toLong())
                .verifyComplete()
    }


    @Test
    fun `mono with error executes onError only`() {

        val exception = IllegalArgumentException()

        Mono.error<String>(exception)
                .stepVerify()
                .verifyError(exception::class.java)
    }

    @Test
    fun `flux with error executes onError only`() {
        val exception = IllegalArgumentException()

        Flux.error<String>(exception)
                .stepVerify()
                .verifyError(exception::class.java)
    }
}