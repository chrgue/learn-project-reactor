import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import kotlin.random.Random.Default.nextLong

class TestUtils {

    companion object {
        const val PAYLOAD = "payload"
        const val HELLO = "hello"
        const val WORLD = "world"
    }

}

fun <T> Publisher<T>.stepVerify(): StepVerifier.FirstStep<T> =
    StepVerifier.create(this)

fun <T> Mono<T>.delayRandom(): Mono<T> =
    delayElement(Duration.ofMillis(nextLong(0, 200)))