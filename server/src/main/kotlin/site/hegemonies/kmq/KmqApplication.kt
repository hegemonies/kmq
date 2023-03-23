package site.hegemonies.kmq

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KmqApplication

fun main(args: Array<String>) {
	runApplication<KmqApplication>(*args)
}
