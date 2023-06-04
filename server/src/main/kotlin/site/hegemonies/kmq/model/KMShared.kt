package site.hegemonies.kmq.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import site.hegemonies.kmq.queue.contract.Message

class KMShared(
    val name: String,
    val capacity: Int,
    val persist: Boolean,
    private val channel: MutableSharedFlow<Message>
) : KMQueue {
    override suspend fun send(message: Message) = channel.emit(message)
    override fun stream(): Flow<Message> = channel.asFlux().asFlow()
    override suspend fun receive(): Message? = channel.lastOrNull()
    override fun close(): Boolean = true
}
