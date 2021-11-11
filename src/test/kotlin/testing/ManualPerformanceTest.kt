package testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import stepVerify
import java.time.Duration

class ManualPerformanceTest {

    @Test
    fun `flatMap runs in parallel`() {
        getImplementationToAnalyse()
            .stepVerify()
            .expectNextCount(10)
            .verifyComplete()
            .let { assertThat(it).isLessThan(Duration.ofMillis(2000)) }
    }

    private fun getImplementationToAnalyse() =
        Flux.range(0, 10)
            .flatMap { Mono.just("").delayElement(Duration.ofMillis(200)) }
}