package signals

import TestUtils.Companion.PAYLOAD
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SignalsManuallyTest {

    @Test
    fun `mono executes onNext & onComplete once`() {
        val signals = mutableListOf<String>()
        val countDown = CountDownLatch(1)

        Mono.just(PAYLOAD)
            .subscribe(
                { signals.add("onNext") },
                { signals.add("onError") },
                {
                    signals.add("onComplete")
                    countDown.countDown()
                }
            )

        await(countDown)
        assertThat(signals).containsExactly("onNext", "onComplete")
    }

    @Test
    fun `flux executes onNext for every item & onComplete once`() {
        val signals = mutableListOf<String>()
        val countDown = CountDownLatch(1)

        val items = listOf(PAYLOAD, PAYLOAD, PAYLOAD)

        Flux.fromIterable(items)
            .subscribe(
                { signals.add("onNext") },
                { signals.add("onError") },
                {
                    signals.add("onComplete")
                    countDown.countDown()
                }
            )

        await(countDown)
        assertThat(signals).containsExactly("onNext", "onNext", "onNext", "onComplete")
    }

    @Test
    fun `mono with error executes onError only`() {
        val signals = mutableListOf<String>()
        val countDown = CountDownLatch(1)

        val exception = IllegalArgumentException()

        Mono.error<String>(exception)
            .subscribe(
                { signals.add("onNext") },
                {
                    signals.add("onError")
                    countDown.countDown()
                },
                { signals.add("onComplete") }
            )

        await(countDown)
        assertThat(signals).containsExactly("onError")
    }

    @Test
    fun `flux with error executes onError only`() {

        val signals = mutableListOf<String>()
        val countDown = CountDownLatch(1)

        val exception = IllegalArgumentException()

        Flux.error<String>(exception)
            .subscribe(
                { signals.add("onNext") },
                {
                    signals.add("onError")
                    countDown.countDown()
                },
                { signals.add("onComplete") }
            )

        await(countDown)
        assertThat(signals).containsExactly("onError")
    }

    private fun await(countDown: CountDownLatch) {
        assertThat(countDown.await(10, TimeUnit.MILLISECONDS)).isTrue
    }
}