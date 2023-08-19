package site.hegemonies.client.grpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import site.hegemonies.client.grpc.mapper.KmqGrpcClientMapper
import site.hegemonies.kmq.queue.contract.QueueServiceGrpcKt
import site.hegemonies.kmq.queue.contract.createQueueRequest
import site.hegemonies.kmq.queue.contract.receiveLastBatchMessagesRequest
import site.hegemonies.kmq.queue.contract.receiveLastMessageRequest
import site.hegemonies.model.Message

@Service
class KmqGrpcClient(
    private val queueServiceStub: QueueServiceGrpcKt.QueueServiceCoroutineStub,
    private val kmqGrpcClientMapper: KmqGrpcClientMapper
) : IKmqGrpcClient {

    override suspend fun createQueue(name: String): Result<Unit> = runCatching {
        queueServiceStub.createQueue(
            createQueueRequest { this.queueName = queueName }
        )
    }

    override suspend fun sendMessage(message: Message, queueName: String): Result<Unit> = runCatching {
        queueServiceStub.sendMessage(kmqGrpcClientMapper.map(message, queueName))
    }

    override suspend fun sendStreamMessages(messages: Flow<Message>, queueName: String): Result<Unit> = runCatching {
        queueServiceStub.sendStreamMessages(
            messages.map { message -> kmqGrpcClientMapper.map(message, queueName) }
        )
    }

    override suspend fun receiveLastMessage(queueName: String): Result<Message> = runCatching {
        val response = queueServiceStub.receiveLastMessage(
            receiveLastMessageRequest { this.queueName = queueName }
        )

        kmqGrpcClientMapper.map(response.message)
    }

    override suspend fun receiveLastBatchMessages(queueName: String, amount: Int): Result<List<Message>> = runCatching {
        val response = queueServiceStub.receiveLastBatchMessages(
            receiveLastBatchMessagesRequest { this.queueName = queueName; this.amount = amount }
        )

        response.messagesList.map(kmqGrpcClientMapper::map)
    }

    override suspend fun receiveStreamMessages(queueName: String): Result<Flow<Message>> = runCatching {
        val response = queueServiceStub.receiveStreamMessages(
            receiveLastMessageRequest { this.queueName = queueName }
        )

        response.map { kmqGrpcClientMapper.map(it.message) }
    }
}
