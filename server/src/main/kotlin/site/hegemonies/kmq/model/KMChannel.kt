package site.hegemonies.kmq.model

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import site.hegemonies.kmq.queue.contract.Message

class KMChannel(
    val name: String,
    val capacity: Int,
    val persist: Boolean,
    private val channel: Channel<Message>
) : KMQueue {

    override suspend fun send(message: Message) = channel.send(message)
    override fun stream() = channel.receiveAsFlow()
    override suspend fun receive() = channel.receive()
    override fun close() = channel.close()
}
