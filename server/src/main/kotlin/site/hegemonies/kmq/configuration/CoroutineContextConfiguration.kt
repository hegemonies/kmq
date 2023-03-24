package site.hegemonies.kmq.configuration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import site.hegemonies.kmq.configuration.properties.CoroutineProperties
import kotlin.coroutines.CoroutineContext

@Configuration
class CoroutineContextConfiguration(
    private val coroutineProperties: CoroutineProperties
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Bean
    fun grpcCoroutineScope(): CoroutineScope =
        object : CoroutineScope {
            override val coroutineContext: CoroutineContext =
                Dispatchers.IO.limitedParallelism(coroutineProperties.size) + SupervisorJob()
        }
}
