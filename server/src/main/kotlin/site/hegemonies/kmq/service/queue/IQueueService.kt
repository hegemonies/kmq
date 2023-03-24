package site.hegemonies.kmq.service.queue

import site.hegemonies.kmq.queue.contract.Message

interface IQueueService {
    fun createQueue(name: String, capacity: Int): Result<Unit>
    suspend fun sendMessage(queueName: String, message: Message): Result<Unit>
    suspend fun receiveLastMessage(queueName: String): Result<Message>
}
