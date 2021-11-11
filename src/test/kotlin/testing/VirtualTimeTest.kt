package testing

import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.time.Duration

class VirtualTimeTest {

    @Test
    fun `mono resolves after a long time`() {
        val delay = Duration.ofMinutes(60)
        val payload = "payload"

        StepVerifier.withVirtualTime { payload.toMono().delayElement(delay) }
            .thenAwait(delay)
            .expectNext(payload)
            .verifyComplete()
    }
}