package showcases

import TestUtils.Companion.PAYLOAD
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.publisher.TestPublisher
import stepVerify

class MultipleSubscriptionTest {

    private val mapper: (String) -> String = mockk(relaxed = true)
    private val getSearchResponse: () -> Mono<String> = mockk()

    @Test
    fun `resubscribe source by concatWith - tested implicitly by operator invocation`() {
        every { getSearchResponse() } answers { PAYLOAD.toMono() }
        every { mapper(any()) } returns "foo"

        val response = getSearchResponse().map { mapper(it) }

        response
                .concatWith(response)
                .stepVerify()
                .expectNext("foo")
                .expectNext("foo")
                .verifyComplete()

        verify(exactly = 1) { getSearchResponse() }
        verify(exactly = 2) { mapper(any()) }
    }

    @Test
    fun `resubscribe source by concatWith - tested via test publisher`() {
        val searchResponsePublisher = TestPublisher.createCold<String>()
        every { getSearchResponse() } answers { searchResponsePublisher.also { it.emit(PAYLOAD) }.mono() }

        val response = getSearchResponse()

        response
                .concatWith(response)
                .stepVerify()
                .expectNext(PAYLOAD)
                .expectNext(PAYLOAD)
                .then { searchResponsePublisher.assertSubscriptionCount(2) }
                .verifyComplete()

        verify(exactly = 1) { getSearchResponse() }
    }


    @Test
    fun `resubscribe source by zip - tested via test publisher`() {
        val searchResponsePublisher = TestPublisher.createCold<String>()
        every { getSearchResponse() } answers { searchResponsePublisher.also { it.emit(PAYLOAD) }.mono() }

        val response = getSearchResponse()

        Mono.zip(response, response, response, response, response, response)
                .stepVerify()
                .expectNextCount(1)
                .then { searchResponsePublisher.assertSubscriptionCount(6) }
                .verifyComplete()

        verify(exactly = 1) { getSearchResponse() }
    }

    @Test
    fun `cache results`() {
        val searchResponsePublisher = TestPublisher.createCold<String>()
        every { getSearchResponse() } answers { searchResponsePublisher.also { it.emit(PAYLOAD) }.mono() }

        val response = getSearchResponse().cache()

        Mono.zip(response, response, response, response, response, response)
                .stepVerify()
                .expectNextCount(1)
                .then { searchResponsePublisher.assertSubscriptionCount(1) }
                .verifyComplete()

        verify(exactly = 1) { getSearchResponse() }
    }

    private fun <T> TestPublisher<T>.assertSubscriptionCount(count: Long) =
        assertThat(subscribeCount()).isEqualTo(count)
}