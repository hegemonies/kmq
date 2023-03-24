package site.hegemonies.kmq.router.grpc

import kotlinx.coroutines.flow.Flow
import net.devh.boot.grpc.server.service.GrpcService
import site.hegemonies.kmq.queue.contract.CreateQueueRequest
import site.hegemonies.kmq.queue.contract.CreateQueueResponse
import site.hegemonies.kmq.queue.contract.QueueServiceGrpcKt
import site.hegemonies.kmq.queue.contract.ReceiveLastBatchMessagesRequest
import site.hegemonies.kmq.queue.contract.ReceiveLastBatchMessagesResponse
import site.hegemonies.kmq.queue.contract.ReceiveLastMessageRequest
import site.hegemonies.kmq.queue.contract.ReceiveLastMessageResponse
import site.hegemonies.kmq.queue.contract.ReceiveMessageByIndexRequest
import site.hegemonies.kmq.queue.contract.ReceiveMessageByIndexResponse
import site.hegemonies.kmq.queue.contract.SendMessageRequest
import site.hegemonies.kmq.queue.contract.SendMessageResponse

@GrpcService
class QueueGrpcRouter : QueueServiceGrpcKt.QueueServiceCoroutineImplBase() {

    override suspend fun createQueue(request: CreateQueueRequest): CreateQueueResponse {
        return super.createQueue(request)
    }

    override suspend fun sendMessage(request: SendMessageRequest): SendMessageResponse {
        return super.sendMessage(request)
    }

    override suspend fun sendStreamMessages(requests: Flow<SendMessageRequest>): SendMessageResponse {
        return super.sendStreamMessages(requests)
    }

    override suspend fun receiveLastMessage(request: ReceiveLastMessageRequest): ReceiveLastMessageResponse {
        return super.receiveLastMessage(request)
    }

    override suspend fun receiveLastBatchMessages(request: ReceiveLastBatchMessagesRequest): ReceiveLastBatchMessagesResponse {
        return super.receiveLastBatchMessages(request)
    }

    override suspend fun receiveStreamMessages(requests: Flow<ReceiveLastMessageRequest>): ReceiveLastMessageResponse {
        return super.receiveStreamMessages(requests)
    }

    override suspend fun receiveMessageByIndex(request: ReceiveMessageByIndexRequest): ReceiveMessageByIndexResponse {
        return super.receiveMessageByIndex(request)
    }
}
