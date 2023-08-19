package site.hegemonies.configuration.properties

import org.jetbrains.annotations.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties("kmq.grpc.client")
data class KmqGrpcClientProperties(
    @field:NotNull
    val host: String,
    val port: Int = 9090,
    val timeout: Duration = Duration.ofSeconds(1)
)
