package site.hegemonies.kmq.factory

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import org.springframework.stereotype.Component
import site.hegemonies.kmq.model.KMChannel
import site.hegemonies.kmq.model.KMQueue
import site.hegemonies.kmq.model.KMShared
import site.hegemonies.kmq.queue.contract.QueueType

@Component
class QueueFactory {

    fun createQueue(
        name: String,
        capacity: Int,
        persist: Boolean,
        type: QueueType
    ): KMQueue =
        when (type) {
            QueueType.CHANNEL ->
                KMChannel(name, capacity, persist, Channel(capacity))

            QueueType.SHARED ->
                KMShared(
                    name,
                    capacity,
                    persist,
                    MutableSharedFlow(extraBufferCapacity = capacity, onBufferOverflow = BufferOverflow.DROP_OLDEST)
                )

            else -> throw RuntimeException("No match queue type for $type")
        }
}
