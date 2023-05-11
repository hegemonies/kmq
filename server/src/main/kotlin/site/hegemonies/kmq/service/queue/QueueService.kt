package site.hegemonies.kmq.service.queue

import kotlinx.coroutines.flow.Flow
import mu.KLogging
import org.springframework.stereotype.Service
import site.hegemonies.kmq.model.Queue
import site.hegemonies.kmq.queue.contract.Message
import site.hegemonies.kmq.storage.IQueueStorage

@Service
class QueueService(
    private val queueStorage: IQueueStorage
) : IQueueService {

    private companion object : KLogging()

    override fun createQueue(name: String, capacity: Int): Result<Unit> =
        queueStorage.create(name, capacity)

    override suspend fun sendMessage(queueName: String, message: Message): Result<Unit> {
        logger.debug { "Send message to queue=$queueName, message_size=${message.body.length}" }
        val queue: Queue = queueStorage.get(queueName).getOrElse { error -> return Result.failure(error) }
        return runCatching { queue.send(message) }
    }

    override suspend fun receiveLastMessage(queueName: String): Result<Message> {
        logger.debug { "Receive message from queue=$queueName" }
        val queue: Queue = queueStorage.get(queueName).getOrElse { error -> return Result.failure(error) }
        return runCatching { queue.receive() }
    }

    override suspend fun receiveLastBatchMessage(queueName: String, amount: Int): Result<List<Message>> {
        logger.debug { "Receive batch messages from queue=$queueName" }
        val queue: Queue = queueStorage.get(queueName).getOrElse { error -> return Result.failure(error) }
        return runCatching {
            (0 until amount).map { queue.receive() }
        }
    }

    override fun receiveStreamMessages(queueName: String): Flow<Message> {
        logger.debug { "Receive stream messages from queue=$queueName" }
        val queue: Queue = queueStorage.get(queueName).getOrThrow()
        return queue.stream()
    }
}
