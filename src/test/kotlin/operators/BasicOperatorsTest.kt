package operators

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import stepVerify

class BasicOperatorsTest {

    @Test
    fun `map value`() {
        getResponseFromExternalService()
            .map { it.uppercase() }
            .stepVerify()
            .expectNext("SEARCH RESPONSE")
            .verifyComplete()
    }

    @Test
    fun `filter value`() {
        getResponseFromExternalService()
            .filter { it.startsWith("s") }
            .stepVerify()
            .expectNext("search response")
            .verifyComplete()


    }

    @Test
    fun `flatMap value`() {
        getResponseFromExternalService()
            .flatMap { p -> getSingleItemFromDB().map { "$it $p" } }
            .stepVerify()
            .expectNext("new search response")
            .verifyComplete()
    }

    @Test
    fun `flatMapMany value`() {
        getResponseFromExternalService()
            .flatMapMany { p -> getMultipleItemsFromDB().map { "$it $p" } }
            .stepVerify()
            .expectNext("new search response")
            .expectNext("super search response")
            .verifyComplete()
    }

    @Test
    fun `chain basic operators`() {
        getResponseFromExternalService()
            .filter { it.startsWith("s") }
            .flatMapMany { p ->
                getMultipleItemsFromDB().map { "$it $p" }
            }
            .filter { it.contains("super") }
            .map { it.uppercase() }
            .stepVerify()
            .expectNext("SUPER SEARCH RESPONSE")
            .verifyComplete()
    }

    private fun getResponseFromExternalService() = Mono.just("search response")
    private fun getSingleItemFromDB() = Mono.just("new")
    private fun getMultipleItemsFromDB() = Flux.just("new", "super")
}