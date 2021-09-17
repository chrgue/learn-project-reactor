import org.reactivestreams.Publisher
import reactor.test.StepVerifier

fun <T> Publisher<T>.stepVerify() =
    StepVerifier.create(this)
