package ext

import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import kotlin.random.Random

fun <T> Publisher<T>.stepVerify(): StepVerifier.FirstStep<T> =
    StepVerifier.create(this)

fun <T> Mono<T>.delayRandom(): Mono<T> =
    delayElement(Duration.ofMillis(Random.nextLong(0, 10)))