package site.hegemonies.kmq.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.stereotype.Component
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.toJavaDuration

@Component
class GrpcMetrics(
    private val meterRegistry: MeterRegistry
) {

    private companion object {
        const val GRPC_INBOUND_REQUEST_DURATION_METRIC_NAME = "inbound-request-duration"
    }

    private val inboundRequestDurationTimer by lazy {
        meterRegistry.timer(
            GRPC_INBOUND_REQUEST_DURATION_METRIC_NAME,
            listOf(Tag.of("grpc", "inbound"))
        )
    }

    @OptIn(ExperimentalTime::class)
    fun <T : Any> recordInboundRequestDuration(method: () -> T): T {
        var result: T
        val duration = measureTime { result = method() }
        inboundRequestDurationTimer.record(duration.toJavaDuration())
        return result
    }
}
