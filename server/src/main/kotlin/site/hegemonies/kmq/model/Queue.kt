package site.hegemonies.kmq.model

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Component
import site.hegemonies.kmq.queue.contract.Message
import site.hegemonies.kmq.queue.contract.QueueType

//data class Queue(
//    val name: String,
//    val capacity: Int,
//    private val channel: Channel<Message>
//) {
//    suspend fun send(message: Message) = channel.send(message)
//    fun stream() = channel.receiveAsFlow()
//    suspend fun receive() = channel.receive()
//    fun close() = channel.close()
//}

interface KMQueue

class Channel(
    val name: String,
    val capacity: Int,
    val persist: Boolean,
    private val channel: Channel<Message>
) : KMQueue {

    suspend fun send(message: Message) = v.send(message)
    fun stream() = channel.receiveAsFlow()
    suspend fun receive() = channel.receive()
    fun close() = channel.close()
}

@Component
class QueueFactory {

    fun createQueue(
        name: String,
        capacity: Int,
        persist: Boolean,
        type: QueueType
    ): Queue =
        object : Queue(name, capacity, persist) {
            private val q:
            override fun getChannel(): IQueueChannel =
                when (type) {
                    QueueType.CHANNEL = Channel<Message>(capacity)
                }
        }
}
