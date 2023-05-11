package site.hegemonies.kmq.router.http

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.coRouter
import site.hegemonies.kmq.queue.contract.CreateQueueResponse
import site.hegemonies.kmq.queue.contract.ErrorResult
import site.hegemonies.kmq.queue.contract.SendMessageRequest
import site.hegemonies.kmq.queue.contract.SendMessageResponse
import site.hegemonies.kmq.queue.contract.createQueueResponse
import site.hegemonies.kmq.queue.contract.errorResult
import site.hegemonies.kmq.queue.contract.responseResult
import site.hegemonies.kmq.queue.contract.sendMessageResponse
import site.hegemonies.kmq.queue.contract.successResult
import site.hegemonies.kmq.service.queue.QueueService

@RestController
class QueueHttpRouter(
    private val queueService: QueueService
) {

    @Bean
    fun router(queueHttpGateway: QueueHttpGateway) = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            POST("/api/v2/queue/{methodName}") { request ->
                val methodName = request.pathVariable("methodName")
                when {
                    methodName.startsWith("sendStream") ->
                        queueHttpGateway.handleInputStream(methodName, request)

                    methodName.startsWith("receiveStream") ->
                        queueHttpGateway.handleOutputStream(methodName, request)

                    else -> queueHttpGateway.handle(methodName, request)
                }
            }
        }
    }

    @GetMapping("/api/v1/queue/create-queue")
    suspend fun createQueue(
        @RequestParam("queueName") queueName: String,
        @RequestParam("capacity") capacity: Int
    ): CreateQueueResponse {
        queueService.createQueue(queueName, capacity).onFailure { error ->
            return createQueueResponse { result = makeErrorResponse(error) }
        }

        return createQueueResponse { result = makeSuccessResponse() }
    }

    @PostMapping("/api/v1/queue/send-message")
    suspend fun sendMessage(
        @RequestBody request: SendMessageRequest
    ): SendMessageResponse {
        queueService.sendMessage(request.queueName, request.message).onFailure { error ->
            return sendMessageResponse { result = makeErrorResponse(error) }
        }

        return sendMessageResponse { result = makeSuccessResponse() }
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
