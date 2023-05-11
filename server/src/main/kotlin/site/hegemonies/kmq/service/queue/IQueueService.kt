package site.hegemonies.kmq.service.queue

import kotlinx.coroutines.flow.Flow
import site.hegemonies.kmq.queue.contract.Message

interface IQueueService {
    fun createQueue(name: String, capacity: Int): Result<Unit>
    suspend fun sendMessage(queueName: String, message: Message): Result<Unit>
    suspend fun receiveLastMessage(queueName: String): Result<Message>
    suspend fun receiveLastBatchMessage(queueName: String, amount: Int): Result<List<Message>>
    fun receiveStreamMessages(queueName: String): Flow<Message>
}
