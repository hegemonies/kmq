package site.hegemonies.client.grpc

import kotlinx.coroutines.flow.Flow
import site.hegemonies.model.Message

interface IKmqGrpcClient {
    suspend fun createQueue(name: String): Result<Unit>
    suspend fun sendMessage(message: Message, queueName: String): Result<Unit>
    suspend fun sendStreamMessages(messages: Flow<Message>, queueName: String): Result<Unit>
    suspend fun receiveLastMessage(queueName: String): Result<Message>
    suspend fun receiveLastBatchMessages(queueName: String, amount: Int = 10): Result<List<Message>>
    suspend fun receiveStreamMessages(queueName: String): Result<Flow<Message>>
}
