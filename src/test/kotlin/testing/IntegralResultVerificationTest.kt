package testing

import TestUtils.Companion.HELLO
import TestUtils.Companion.WORLD
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import stepVerify

class IntegralResultVerificationTest {

    @Test
    fun `contains only items with a length of 5`() {
        Flux.just(HELLO, WORLD)
            .stepVerify()
            .recordWith { mutableListOf() }
            .thenConsumeWhile { true }
            .consumeRecordedWith { records ->
                assertThat(records.map { it.length }).containsOnly(5)
            }.verifyComplete()
    }
}