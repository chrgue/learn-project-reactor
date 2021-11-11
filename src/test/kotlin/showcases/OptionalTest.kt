package showcases

import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.core.util.function.component3
import stepVerify
import java.util.Optional

data class PersonDTO(
    val name: String,
    val age: Int?,
    val hair: Int?
)

data class TestData(
    val arguments: Triple<String?, Int?, Int?>,
    val expectedResult: PersonDTO?,
    val implementation: (name: Mono<String>, age: Mono<Int>, hair: Mono<Int>) -> Mono<PersonDTO>,
    val implementationName: String
)

class PersonServiceTest {

    companion object {
        private const val NAME = "disco"
        private const val AGE = 99
        private const val HAIR = 12345

        private val TEST_DATA = listOf(
            Triple(NAME, AGE, HAIR) to PersonDTO(name = NAME, age = AGE, hair = HAIR),
            Triple(NAME, null, HAIR) to PersonDTO(name = NAME, age = null, hair = HAIR),
            Triple(NAME, AGE, null) to PersonDTO(name = NAME, age = AGE, hair = null),
            Triple(NAME, null, null) to PersonDTO(name = NAME, age = null, hair = null),
            Triple(null, AGE, HAIR) to null,
            Triple(null, null, HAIR) to null,
            Triple(null, null, null) to null
        )


        private val IMPLEMENTATIONS =
            listOf(
                this::createByOptionals to "With Optionals",
                this::createByDefaults to "With Defaults"
            )

        private val COMPLETE = TEST_DATA.flatMap { (arguments, expectedResult) ->
            IMPLEMENTATIONS.map { (implementation, implementationName) ->
                TestData(
                    arguments = arguments,
                    expectedResult = expectedResult,
                    implementation = implementation,
                    implementationName = implementationName
                )
            }
        }

        //E
        private fun createByOptionals(name: Mono<String>, age: Mono<Int>, hair: Mono<Int>): Mono<PersonDTO> {
            val ageOptional = age.map { Optional.of(it) }.defaultIfEmpty(Optional.empty())
            val hairOptional = hair.map { Optional.of(it) }.defaultIfEmpty(Optional.empty())

            return Mono
                .zip(name, ageOptional, hairOptional)
                .map { (name, age, hair) ->
                    PersonDTO(name, age.orElse(null), hair.orElse(null))
                }
        }

        //M
        private fun createByDefaults(name: Mono<String>, age: Mono<Int>, hair: Mono<Int>): Mono<PersonDTO> {
            val defaultAge = -2
            val defaultHair = -1

            val ageWithDefault = age.defaultIfEmpty(defaultAge)
            val hairWithDefault = hair.defaultIfEmpty(defaultHair)

            return Mono.zip(name, ageWithDefault, hairWithDefault)
                .map { (name, age, hair) ->

                    val nullableAge = age.takeUnless { it == defaultAge }
                    val nullableHair = hair.takeUnless { it == defaultHair }
                    PersonDTO(name, nullableAge, nullableHair)
                }
        }
    }

    @TestFactory
    fun `create person`() = COMPLETE.map { testData ->
        val shouldBeEmpty = testData.expectedResult == null
        val expectedMsg = if (shouldBeEmpty) "empty" else "${testData.expectedResult}"

        dynamicTest("${testData.implementationName}: name: ${testData.arguments.first}, age: ${testData.arguments.second}, hair: ${testData.arguments.third} -> person: $expectedMsg") {

            testData.implementation(
                testData.arguments.first.toMono(),
                testData.arguments.second.toMono(),
                testData.arguments.third.toMono()
            )
                .stepVerify()
                .recordWith { mutableListOf() }
                .expectNextCount(if (shouldBeEmpty) 0 else 1)
                .consumeRecordedWith {
                    if (!shouldBeEmpty) assert(it.first() == testData.expectedResult)
                }
                .verifyComplete()
        }
    }
}