package testing

import TestUtils.Companion.HELLO
import TestUtils.Companion.PAYLOAD
import TestUtils.Companion.WORLD
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono
import stepVerify

class AssertionTest {

    @Test
    fun `assert next with mono`() {
        PAYLOAD
                .toMono()
                .stepVerify()
                .assertNext { assertThat(it).startsWith("p") }
                .verifyComplete()
    }

    @Test
    fun `assert next with flux`() {
        Flux.just(HELLO, WORLD)
                .stepVerify()
                .assertNext { assertThat(it).startsWith("h") }
                .assertNext { assertThat(it).endsWith("d") }
                .verifyComplete()
    }
}