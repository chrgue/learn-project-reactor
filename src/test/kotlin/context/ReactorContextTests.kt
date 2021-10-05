package context

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import stepVerify

class ReactorContextTests {

    companion object {
        private const val CONTEXT_VARIABLE = "var"
    }

    @Test
    fun `enrich pipeline result by context`() {
        "hello"
                .toMono()
                .flatMap { fromPipeline ->

                    // read from context
                    Mono.deferContextual { ctx ->
                        ctx.getOrDefault<String>(CONTEXT_VARIABLE, null)
                                .toMono()
                                // combine pipeline data & context data
                                .map { fromContext -> fromPipeline + fromContext }
                    }

                }
                // enrich upstream operators
                .contextWrite { ctx -> ctx.put(CONTEXT_VARIABLE, " world") }
                .stepVerify()
                .expectNext("hello world")
                .verifyComplete()
    }

    @Test
    fun `use context side effect behavior`() {
        val logger: reactor.util.Logger = mockk(relaxed = true)

        "hello"
                .toMono()
                .doOnEach { signal ->
                    signal
                            .takeIf { it.isOnNext }
                            ?.let {
                                val fromContext = it.contextView.getOrDefault<String>(CONTEXT_VARIABLE, null)
                                logger.debug("While processing got '$fromContext'")
                            }
                }
                .contextWrite { ctx -> ctx.put(CONTEXT_VARIABLE, "world") }
                .stepVerify()
                .expectNext("hello")
                .verifyComplete()

        verify(exactly = 1) { logger.debug("While processing got 'world'") }
    }


    @Test
    fun `test context contains value`() {
        "hello"
                .toMono()
                .contextWrite { ctx -> ctx.put(CONTEXT_VARIABLE, "world") }
                .stepVerify()
                .expectAccessibleContext()
                .contains(CONTEXT_VARIABLE, "world")
    }
}