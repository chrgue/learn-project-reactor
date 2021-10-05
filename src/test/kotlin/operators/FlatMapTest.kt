package operators

import delayRandom
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import stepVerify

class FlatMapTest {

    @Test
    fun `simple flatmap`() {

        Flux.just("demo", "cat")
                .flatMap { Mono.just("this is a $it") }
                .stepVerify()
                .expectNext("this is a demo")
                .expectNext("this is a cat")
                .verifyComplete()

    }

    @Test
    fun `flatmap with random delay`() {

        Flux.just("demo", "cat")
                /*
                    using a simple flatMap will randomly fail because element #1 can overtake #2
                    a flatMapSequential will maintain the input order
                */
                .flatMapSequential { Mono.just("this is a $it").delayRandom() }
                .stepVerify()
                .expectNext("this is a demo")
                .expectNext("this is a cat")
                .verifyComplete()
    }
}