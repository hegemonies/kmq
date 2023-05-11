package site.hegemonies.kmq.router.http

import com.google.protobuf.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component
import site.hegemonies.kmq.metrics.QueueMetrics
import site.hegemonies.kmq.queue.contract.CreateQueueRequest
import site.hegemonies.kmq.queue.contract.ErrorResult
import site.hegemonies.kmq.queue.contract.ReceiveLastBatchMessagesRequest
import site.hegemonies.kmq.queue.contract.ReceiveLastMessageRequest
import site.hegemonies.kmq.queue.contract.SendMessageRequest
import site.hegemonies.kmq.queue.contract.createQueueResponse
import site.hegemonies.kmq.queue.contract.errorResult
import site.hegemonies.kmq.queue.contract.receiveLastBatchMessagesResponse
import site.hegemonies.kmq.queue.contract.receiveLastMessageResponse
import site.hegemonies.kmq.queue.contract.responseResult
import site.hegemonies.kmq.queue.contract.sendMessageResponse
import site.hegemonies.kmq.queue.contract.successResult
import site.hegemonies.kmq.service.queue.QueueService

@Component
class QueueHttpGateway(
    private val queueService: QueueService,
    private val queueMetrics: QueueMetrics
) : GrpcGateway {

    fun createQueue() =
        object : GrpcGatewayMethod {
            override fun getName(): String = "createQueue"

            override fun getProtoRequestMessageBuilder(): Message.Builder = CreateQueueRequest.newBuilder()

            override suspend fun handle(request: Message): Message {
                request as CreateQueueRequest

                queueMetrics.incrementCountQueue()
                queueService.createQueue(request.queueName, request.capacity).onFailure { error ->
                    return makeErrorResponse(error)
                }

                return createQueueResponse {
                    result = responseResult { makeSuccessResponse() }
                }
            }

            override suspend fun handleOutputStream(request: Message): Flow<Message> {
                TODO("Not yet implemented")
            }
        }

    fun sendMessage() =
        object : GrpcGatewayMethod {
            override fun getName(): String = "sendMessage"

            override fun getProtoRequestMessageBuilder(): Message.Builder = SendMessageRequest.newBuilder()

            override suspend fun handle(request: Message): Message {
                request as SendMessageRequest
                queueService.sendMessage(request.queueName, request.message)
                queueMetrics.incrementMessageInQueue(request.queueName)
                return sendMessageResponse { result = makeSuccessResponse() }
            }

            override suspend fun handleOutputStream(request: Message): Flow<Message> {
                TODO("Not yet implemented")
            }
        }

    fun sendStreamMessages() =
        object : GrpcGatewayMethod {
            override fun getName(): String = "sendStreamMessages"

            override fun getProtoRequestMessageBuilder(): Message.Builder = SendMessageRequest.newBuilder()

            override suspend fun handle(request: Message): Message {
                request as SendMessageRequest
                queueService.sendMessage(request.queueName, request.message)
                queueMetrics.incrementMessageInQueue(request.queueName)
                return sendMessageResponse { result = makeSuccessResponse() }
            }

            override suspend fun handleOutputStream(request: Message): Flow<Message> {
                TODO("Not yet implemented")
            }
        }

    fun receiveLastMessage() =
        object : GrpcGatewayMethod {
            override fun getName(): String = "receiveLastMessage"

            override fun getProtoRequestMessageBuilder(): Message.Builder = ReceiveLastMessageRequest.newBuilder()

            override suspend fun handle(request: Message): Message {
                request as ReceiveLastMessageRequest

                val message = queueService.receiveLastMessage(request.queueName).getOrElse { error ->
                    return receiveLastMessageResponse { result = makeErrorResponse(error) }
                }
                queueMetrics.decrementMessageInQueue(request.queueName)

                return receiveLastMessageResponse {
                    this.message = message
                    result = makeSuccessResponse()
                }
            }

            override suspend fun handleOutputStream(request: Message): Flow<Message> {
                TODO("Not yet implemented")
            }
        }

    fun receiveLastBatchMessages() =
        object : GrpcGatewayMethod {
            override fun getName(): String = "receiveLastBatchMessages"

            override fun getProtoRequestMessageBuilder(): Message.Builder =
                ReceiveLastBatchMessagesRequest.newBuilder()

            override suspend fun handle(request: Message): Message {
                request as ReceiveLastBatchMessagesRequest

                val messages =
                    queueService.receiveLastBatchMessage(request.queueName, request.amount).getOrElse { error ->
                        return receiveLastBatchMessagesResponse { result = makeErrorResponse(error) }
                    }
                queueMetrics.decrementMessagesInQueue(request.queueName, request.amount)

                return receiveLastBatchMessagesResponse {
                    result = makeSuccessResponse()
                    this.messages += messages
                }
            }

            override suspend fun handleOutputStream(request: Message): Flow<Message> {
                TODO("Not yet implemented")
            }
        }

    fun receiveStreamMessages() =
        object : GrpcGatewayMethod {
            override fun getName(): String = "receiveStreamMessages"

            override fun getProtoRequestMessageBuilder(): Message.Builder = ReceiveLastMessageRequest.newBuilder()

            override suspend fun handle(request: Message): Message {
                TODO("Not yet implemented")
            }

            override suspend fun handleOutputStream(request: Message): Flow<Message> {
                request as ReceiveLastMessageRequest
                return queueService.receiveStreamMessages(request.queueName).map { message ->
                    receiveLastMessageResponse {
                        this.message = message
                        result = makeSuccessResponse()
                    }
                }
            }
        }

    override fun methods(): List<GrpcGatewayMethod> =
        listOf(
            createQueue(),
            sendMessage(),
            sendStreamMessages(),
            receiveLastMessage(),
            receiveLastBatchMessages(),
            receiveStreamMessages()
        )

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
