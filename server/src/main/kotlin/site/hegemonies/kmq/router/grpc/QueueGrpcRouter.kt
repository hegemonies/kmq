package site.hegemonies.kmq.router.grpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.devh.boot.grpc.server.service.GrpcService
import site.hegemonies.kmq.metrics.QueueMetrics
import site.hegemonies.kmq.queue.contract.CreateQueueRequest
import site.hegemonies.kmq.queue.contract.CreateQueueResponse
import site.hegemonies.kmq.queue.contract.ErrorResult
import site.hegemonies.kmq.queue.contract.QueueServiceGrpcKt
import site.hegemonies.kmq.queue.contract.ReceiveLastBatchMessagesRequest
import site.hegemonies.kmq.queue.contract.ReceiveLastBatchMessagesResponse
import site.hegemonies.kmq.queue.contract.ReceiveLastMessageRequest
import site.hegemonies.kmq.queue.contract.ReceiveLastMessageResponse
import site.hegemonies.kmq.queue.contract.ReceiveMessageByIndexRequest
import site.hegemonies.kmq.queue.contract.ReceiveMessageByIndexResponse
import site.hegemonies.kmq.queue.contract.SendMessageRequest
import site.hegemonies.kmq.queue.contract.SendMessageResponse
import site.hegemonies.kmq.queue.contract.createQueueResponse
import site.hegemonies.kmq.queue.contract.errorResult
import site.hegemonies.kmq.queue.contract.receiveLastBatchMessagesResponse
import site.hegemonies.kmq.queue.contract.receiveLastMessageResponse
import site.hegemonies.kmq.queue.contract.responseResult
import site.hegemonies.kmq.queue.contract.sendMessageResponse
import site.hegemonies.kmq.queue.contract.successResult
import site.hegemonies.kmq.service.queue.IQueueService

@GrpcService
class QueueGrpcRouter(
    grpcCoroutineScope: CoroutineScope,
    private val queueService: IQueueService,
    private val queueMetrics: QueueMetrics
) : QueueServiceGrpcKt.QueueServiceCoroutineImplBase(grpcCoroutineScope.coroutineContext) {

    override suspend fun createQueue(request: CreateQueueRequest): CreateQueueResponse {
        queueService.createQueue(request.queueName, request.capacity, request.persist, request.type)
        queueMetrics.incrementCountQueue()
        return createQueueResponse { result = makeSuccessResponse() }
    }

    override suspend fun sendMessage(request: SendMessageRequest): SendMessageResponse {
        queueService.sendMessage(request.queueName, request.message)
        queueMetrics.incrementMessageInQueue(request.queueName)
        return sendMessageResponse { result = makeSuccessResponse() }
    }

    override suspend fun sendStreamMessages(requests: Flow<SendMessageRequest>): SendMessageResponse {
        requests.collect { request -> sendMessage(request) }
        return sendMessageResponse { result = makeSuccessResponse() }
    }

    override suspend fun receiveLastMessage(request: ReceiveLastMessageRequest): ReceiveLastMessageResponse {
        val message = queueService.receiveLastMessage(request.queueName).getOrElse { error ->
            return receiveLastMessageResponse { result = makeErrorResponse(error) }
        }
        queueMetrics.decrementMessageInQueue(request.queueName)

        return receiveLastMessageResponse {
            if (message != null) this.message = message
            result = makeSuccessResponse()
        }
    }

    override suspend fun receiveLastBatchMessages(
        request: ReceiveLastBatchMessagesRequest
    ): ReceiveLastBatchMessagesResponse {
        val messages = queueService.receiveLastBatchMessage(request.queueName, request.amount).getOrElse { error ->
            return receiveLastBatchMessagesResponse { result = makeErrorResponse(error) }
        }
        queueMetrics.decrementMessagesInQueue(request.queueName, request.amount)

        return receiveLastBatchMessagesResponse {
            result = makeSuccessResponse()
            this.messages += messages
        }
    }

    override fun receiveStreamMessages(request: ReceiveLastMessageRequest): Flow<ReceiveLastMessageResponse> {
        return queueService.receiveStreamMessages(request.queueName).map { message ->
            receiveLastMessageResponse {
                this.message = message
                result = makeSuccessResponse()
            }
        }
    }

    override suspend fun receiveMessageByIndex(request: ReceiveMessageByIndexRequest): ReceiveMessageByIndexResponse {
        return super.receiveMessageByIndex(request)
    }

    private fun makeSuccessResponse() =
        responseResult { success = successResult { success = true } }

    private fun makeErrorResponse(e: Throwable) =
        responseResult {
            error = errorResult {
                code = ErrorResult.Code.INTERNAL_ERROR
                message = e.message ?: "Unknown error"
            }
        }
}
