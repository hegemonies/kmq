package site.hegemonies.kmq.model

import kotlinx.coroutines.flow.Flow
import site.hegemonies.kmq.queue.contract.Message

interface KMQueue {
    suspend fun send(message: Message)
    fun stream(): Flow<Message>
    suspend fun receive(): Message?
    fun close(): Boolean
}
