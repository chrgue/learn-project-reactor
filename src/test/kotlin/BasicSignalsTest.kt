import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class BasicSignalsTest {

    private val onNext: (String) -> Unit = mockk(relaxed = true)
    private val onError: (Throwable) -> Unit = mockk(relaxed = true)
    private val onComplete: () -> Unit = mockk(relaxed = true)

    companion object {
        private const val PAYLOAD = "payload"
    }

    @Test
    fun `mono executes onNext & onComplete once`() {

        Mono.just(PAYLOAD)
                .subscribe(
                    { onNext(it) },
                    { onError(it) },
                    { onComplete() }
                )

        block()

        verify(exactly = 1) { onNext(PAYLOAD) }
        verify(exactly = 0) { onError(any()) }
        verify(exactly = 1) { onComplete() }
    }

    @Test
    fun `flux executes onNext for every item & onComplete once`() {
        val items = listOf(PAYLOAD, PAYLOAD, PAYLOAD)

        Flux.fromIterable(items)
                .subscribe(
                    { onNext(it) },
                    { onError(it) },
                    { onComplete() }
                )

        block()

        verify(exactly = items.size) { onNext(PAYLOAD) }
        verify(exactly = 0) { onError(any()) }
        verify(exactly = 1) { onComplete() }
    }


    @Test
    fun `mono with error executes onError only`() {

        val exception = IllegalArgumentException()

        Mono.error<String>(exception)
                .subscribe(
                    { onNext(it) },
                    { onError(it) },
                    { onComplete() }
                )

        block()

        verify(exactly = 0) { onNext(any()) }
        verify(exactly = 1) { onError(exception) }
        verify(exactly = 0) { onComplete() }
    }

    @Test
    fun `flux with error executes onError only`() {

        val exception = IllegalArgumentException()

        Flux.error<String>(exception)
                .subscribe(
                    { onNext(it) },
                    { onError(it) },
                    { onComplete() }
                )


        block()

        verify(exactly = 0) { onNext(any()) }
        verify(exactly = 1) { onError(exception) }
        verify(exactly = 0) { onComplete() }
    }

    private fun block() {
        // Don't do that in your code. This is just for demonstration.
        Thread.sleep(500)
    }
}