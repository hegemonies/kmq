package site.hegemonies.kmq.storage

import mu.KLogging
import org.springframework.stereotype.Component
import site.hegemonies.kmq.factory.QueueFactory
import site.hegemonies.kmq.model.KMQueue
import site.hegemonies.kmq.queue.contract.QueueType
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PreDestroy

@Component
class QueueStorage(
    private val queueFactory: QueueFactory
) : IQueueStorage {

    private val storage = ConcurrentHashMap<String, KMQueue>(INITIAL_STORAGE_CAPACITY)

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

    override fun create(name: String, capacity: Int, persist: Boolean, type: QueueType): Result<Unit> {
        logger.info { "Creating queue: name=$name, capacity=$capacity" }

        return runCatching {
            val queue = queueFactory.createQueue(name, capacity, persist, type)
            storage[name] = queue
        }
    }

    override fun get(name: String): Result<KMQueue> {
        val queue = storage[name]
        return if (queue == null) {
            Result.failure(Throwable("No such queue as $name"))
        } else {
            Result.success(queue)
        }
    }
}
