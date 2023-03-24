package site.hegemonies.kmq.storage

import site.hegemonies.kmq.model.Queue

interface IQueueStorage {
    fun create(name: String, capacity: Int): Result<Unit>
    fun get(name: String): Result<Queue>
}
