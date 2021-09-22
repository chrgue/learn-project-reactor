import ext.stepVerify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono

class TestingTests {

    @Test
    fun `expect next count with mono`() {
        "hello"
            .toMono()
            .stepVerify()
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun `expect next count with flux`() {
        Flux.just("hello", "world")
            .stepVerify()
            .expectNextCount(2)
            .verifyComplete()
    }

    @Test
    fun `expect next with mono`() {
        "hello"
            .toMono()
            .stepVerify()
            .expectNext("hello")
            .verifyComplete()
    }

    @Test
    fun `expect next with flux`() {
        Flux.just("hello", "world")
            .stepVerify()
            .expectNext("hello")
            .expectNext("world")
            .verifyComplete()
    }

    @Test
    fun `assert next with mono`() {
        "hello"
            .toMono()
            .stepVerify()
            .assertNext { assertThat(it).startsWith("h") }
            .verifyComplete()
    }

    @Test
    fun `assert next with flux`() {
        Flux.just("hello", "world")
            .stepVerify()
            .assertNext { assertThat(it).startsWith("h") }
            .assertNext { assertThat(it).endsWith("d") }
            .verifyComplete()
    }

    @Test
    fun `assert all elements in a flux together`() {
        Flux.just("hello", "world")
            .stepVerify()
            .recordWith { mutableListOf() }
            .thenConsumeWhile { true }
            .consumeRecordedWith { records ->
                assertThat(records.map { it.length }).containsOnly(5)
            }.verifyComplete()
    }
}