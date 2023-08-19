package site.hegemonies.client.grpc.mapper

import org.springframework.stereotype.Component
import site.hegemonies.kmq.queue.contract.SendMessageRequest
import site.hegemonies.kmq.queue.contract.message
import site.hegemonies.kmq.queue.contract.sendMessageRequest
import site.hegemonies.model.Message
import site.hegemonies.kmq.queue.contract.Message as MessageProto
import site.hegemonies.model.Message as MessageDto

@Component
class KmqGrpcClientMapper {

    fun map(message: MessageDto): MessageProto =
        message { body = message.body }

    fun map(message: MessageProto): MessageDto =
        MessageDto(message.body)

    fun map(message: Message, queueName: String): SendMessageRequest =
        sendMessageRequest {
            this.message = map(message)
            this.queueName = queueName
        }
}
