package showcases

import TestUtils.Companion.PAYLOAD
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toMono

class AssemblyTimeVsExecutionTimeTest {

    private val mapper: (String) -> Unit = mockk(relaxed = true)

    @Test
    fun `no operator will be executed happen until you subscribe`() {
        getPipeline()

        verify(exactly = 0) { mapper(any()) }
    }

    @Test
    fun `operators will be executed if you subscribe`() {
        getPipeline()
                .subscribe()

        verify(exactly = 1) { mapper(any()) }
    }

    private fun getPipeline() =
        PAYLOAD
                .toMono()
                .map { mapper(it) }
}