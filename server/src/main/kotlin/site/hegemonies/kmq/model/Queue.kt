package site.hegemonies.kmq.model

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import site.hegemonies.kmq.queue.contract.Message

data class Queue(
    val name: String,
    val capacity: Int,
    private val channel: Channel<Message>
) {
    suspend fun send(message: Message) = channel.send(message)
    fun stream() = channel.receiveAsFlow()
    suspend fun receive() = channel.receive()
    fun close() = channel.close()
}
