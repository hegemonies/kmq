package site.hegemonies.kmq.storage

import site.hegemonies.kmq.model.KMQueue
import site.hegemonies.kmq.queue.contract.QueueType

interface IQueueStorage {
    fun create(name: String, capacity: Int, persist: Boolean, type: QueueType, ifNotExists: Boolean): Result<Boolean>
    fun get(name: String): Result<KMQueue>
}
