package showcases

import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.core.util.function.component3
import java.util.Optional


data class PersonDTO(
    val name: String,
    val age: Int?,
    val hair: Int?
)

interface Implementation {
    fun create(name: Mono<String>, age: Mono<Int>, hair: Mono<Int>): Mono<PersonDTO>
}

/**
 * - does not work
 * - does not work
 * - does not work
 */
object WithNull : Implementation {

    override fun create(name: Mono<String>, age: Mono<Int>, hair: Mono<Int>): Mono<PersonDTO> =
        Mono.zip(
            name,
            age.defaultIfEmpty(null),
            hair.defaultIfEmpty(null)
        ).map { (name, age, hair) ->
            PersonDTO(name, age, hair)
        }
}

/**
 * Kudos goes to: https://github.com/mle-idealo
 *
 * + no additional wrapper object
 *
 * - can collide with your domain (What if -1 is means to cut the hair?)
 * - compiler does not force handling empty case (You need to remember to transform -1 to null.)
 */
object WithDefault : Implementation {

    private const val DEFAULT = -1

    override fun create(name: Mono<String>, age: Mono<Int>, hair: Mono<Int>): Mono<PersonDTO> =
        Mono.zip(
            name,
            age.defaultIfEmpty(DEFAULT),
            hair.defaultIfEmpty(DEFAULT)
        ).map { (name, age, hair) ->
            val nullableAge = age.takeUnless { it == DEFAULT }
            val nullableHair = hair.takeUnless { it == DEFAULT }
            PersonDTO(name, nullableAge, nullableHair)
        }
}

/**
 * Kudos goes to: https://github.com/evainga
 *
 * + compiler forces handling empty case
 * - wrapper object is needed
 */
object WithJavaOptionals : Implementation {

    override fun create(name: Mono<String>, age: Mono<Int>, hair: Mono<Int>): Mono<PersonDTO> =
        Mono.zip(
            name,
            age.map { Optional.of(it) }.defaultIfEmpty(Optional.empty()),
            hair.map { Optional.of(it) }.defaultIfEmpty(Optional.empty())
        ).map { (name, age, hair) ->
            PersonDTO(name, age.orElse(null), hair.orElse(null))
        }
}

/**
 * + forces handling empty case
 * - wrapper object is needed
 * - write additional wrapper classes
 * + wrapper are compiler optimized (https://kotlinlang.org/docs/inline-classes.html)
 * + no "empty check" when building domain object
 */
object WithKotlinValueClasses : Implementation {

    @JvmInline
    private value class OptionalHair(val value: Int? = null)

    @JvmInline
    private value class OptionalAge(val value: Int? = null)

    override fun create(name: Mono<String>, age: Mono<Int>, hair: Mono<Int>): Mono<PersonDTO> =
        Mono.zip(
            name,
            age.map { OptionalAge(it) }.defaultIfEmpty(OptionalAge()),
            hair.map { OptionalHair(it) }.defaultIfEmpty(OptionalHair())
        ).map { (name, age, hair) ->
            PersonDTO(name, age.value, hair.value)
        }
}