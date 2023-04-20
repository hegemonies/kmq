package site.hegemonies.kmq.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "coroutines")
data class CoroutineProperties(
    val size: Int = (Runtime.getRuntime().availableProcessors() - 1).coerceAtLeast(1)
)
