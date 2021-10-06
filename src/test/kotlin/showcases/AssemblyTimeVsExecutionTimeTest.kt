package showcases

import TestUtils.Companion.PAYLOAD
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.test.publisher.TestPublisher

class AssemblyTimeVsExecutionTimeTest {

    private val mapper: (String) -> String = mockk(relaxed = true)
    private val dbPublisher = TestPublisher.createCold<String>()


    @Test
    fun `has either a subscription nor executes operators if 'subscribe' was not called`() {
        getTransformedSearchResult()

        verify(exactly = 0) { mapper(any()) }
        dbPublisher.assertWasNotSubscribed()
    }

    @Test
    fun `has a subscription and executes operators when if 'subscribe' was called`() {
        getTransformedSearchResult()
            .subscribe()

        verify(exactly = 1) { mapper(any()) }
        dbPublisher.assertWasSubscribed()
    }

    private fun getTransformedSearchResult() =
        dbPublisher
            .also { it.emit(PAYLOAD) }
            .mono()
            .map { mapper(it) }
}