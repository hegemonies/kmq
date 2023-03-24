package site.hegemonies.kmq.model

import kotlinx.coroutines.channels.Channel
import site.hegemonies.kmq.queue.contract.Message

data class Queue(
    val name: String,
    val capacity: Int,
    private val channel: Channel<Message>
) {
    suspend fun send(message: Message) = channel.send(message)
    suspend fun receive() = channel.receive()
    fun close() = channel.close()
}
