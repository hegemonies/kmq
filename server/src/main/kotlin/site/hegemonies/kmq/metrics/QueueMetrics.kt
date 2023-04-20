package site.hegemonies.kmq.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Component
class QueueMetrics(
    private val meterRegistry: MeterRegistry
) {

    private val queueCounterMetrics by lazy {
        ConcurrentHashMap<String, AtomicLong>()
    }

    private val queueCount by lazy {
        meterRegistry.gauge(
            QUEUE_COUNT_METRIC_NAME,
            AtomicLong()
        )
    }

    private companion object {
        const val QUEUE_COUNT_METRIC_NAME = "queue-counter"
        const val QUEUE_COUNTERS_METRIC_NAME = "queue-counter-by-queue"
    }

    fun incrementMessageInQueue(queueName: String) {
        fun newGauge(): AtomicLong? = meterRegistry.gauge(
            QUEUE_COUNTERS_METRIC_NAME,
            listOf(Tag.of("queue_name", queueName)),
            AtomicLong()
        )

        val metric = queueCounterMetrics.getOrPut(queueName) { newGauge() }
        metric.incrementAndGet()
    }

    fun decrementMessageInQueue(queueName: String) {
        val metric = queueCounterMetrics[queueName] ?: return
        metric.decrementAndGet()
    }

    fun incrementCountQueue() {
        queueCount?.incrementAndGet()
    }
}
