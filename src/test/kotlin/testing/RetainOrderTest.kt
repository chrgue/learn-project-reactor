package testing

import delayRandom
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import stepVerify

class RetainOrderTest {

    private val getProductFromDB: (Long) -> Mono<String> = mockk(relaxed = true)

    @Test
    fun `products retain order according to the search result`() {
        mockProducts()

        getOrderedProductsFromSearchImplementation()
            .stepVerify()
            .expectNext("1")
            .expectNext("2")
            .expectNext("3")
            .expectNext("4")
            .verifyComplete()
    }

    private fun getOrderedProductsFromSearchImplementation() =
        Flux.just(1L, 2L, 3L, 4L)
            .flatMap { getProductFromDB(it) }

    private fun mockProducts() =
        every { getProductFromDB(any()) } answers { firstArg<Long>().toString().toMono().delayRandom() }
}