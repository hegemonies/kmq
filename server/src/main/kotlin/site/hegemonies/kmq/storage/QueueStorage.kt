package site.hegemonies.kmq.storage

import kotlinx.coroutines.channels.Channel
import mu.KLogging
import org.springframework.stereotype.Component
import site.hegemonies.kmq.model.Queue
import site.hegemonies.kmq.queue.contract.Message
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PreDestroy

@Component
class QueueStorage : IQueueStorage {

    private val storage = ConcurrentHashMap<String, Queue>(INITIAL_STORAGE_CAPACITY)

    companion object : KLogging() {
        private const val INITIAL_STORAGE_CAPACITY = 10
    }

    @PreDestroy
    fun gracefulShutdown() {
        storage.forEach { (name, queue) ->
            logger.info { "Closing $name queue" }
            queue.close()
        }
    }

    override fun create(name: String, capacity: Int): Result<Unit> {
        logger.info { "Creating queue: name=$name, capacity=$capacity" }

        return runCatching {
            val channel = Channel<Message>(capacity)
            val queue = Queue(name, capacity, channel)
            storage[name] = queue
        }
    }

    override fun get(name: String): Result<Queue> {
        val queue = storage[name]
        return if (queue == null) {
            Result.failure(Throwable("No such queue as $name"))
        } else {
            Result.success(queue)
        }
    }
}
