package testing

import TestUtils.Companion.PAYLOAD
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.Scannable
import reactor.kotlin.core.publisher.toMono

class CacheTest {
    /*
        Based on: https://stackoverflow.com/q/68587400/9978524
     */

    @Test
    fun `test if mono is cached`() {
        getCachedSearchResponse()
            .let { Scannable.from(it).stepName() }
            .let { assertThat(it).isEqualTo("cacheTime") }
    }

    private fun getCachedSearchResponse() = PAYLOAD.toMono().cache()
}