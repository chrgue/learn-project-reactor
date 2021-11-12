package showcases

import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import reactor.kotlin.core.publisher.toMono
import stepVerify


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
                WithDefault,
                WithJavaOptionals,
                WithKotlinValueClasses,
                //WithNull
            )
    }

    @TestFactory
    fun `create person`() =
        TEST_DATA.flatMap { (arguments, expectedResult) ->
            IMPLEMENTATIONS.map { implementation ->
                Triple(arguments, expectedResult, implementation)
            }
        }.map { (arguments, expectedResult, implementation) ->
            val shouldBeEmpty = expectedResult == null
            val expectedMsg = if (shouldBeEmpty) "empty" else "$expectedResult"

            dynamicTest("${implementation.javaClass.simpleName}: name: ${arguments.first}, age: ${arguments.second}, hair: ${arguments.third} -> person: $expectedMsg") {

                implementation.create(
                    arguments.first.toMono(),
                    arguments.second.toMono(),
                    arguments.third.toMono()
                )
                    .stepVerify()
                    .recordWith { mutableListOf() }
                    .expectNextCount(if (shouldBeEmpty) 0 else 1)
                    .consumeRecordedWith {
                        if (!shouldBeEmpty) assert(it.first() == expectedResult)
                    }
                    .verifyComplete()
            }
        }
}