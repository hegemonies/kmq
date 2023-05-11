package site.hegemonies.kmq.router.http

import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToFlow
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

interface GrpcGateway {
    fun methods(): List<GrpcGatewayMethod>

    suspend fun handle(methodName: String, request: ServerRequest): ServerResponse {
        val method = findMethod(methodName)
        return method.handle(request)
    }

    suspend fun handleInputStream(methodName: String, request: ServerRequest): ServerResponse {
        val method = findMethod(methodName)
        return method.handleInputStream(request)
    }

    suspend fun handleOutputStream(methodName: String, request: ServerRequest): ServerResponse {
        val method = findMethod(methodName)
        return method.handleOutputStream(request)
    }

    private fun findMethod(methodName: String): GrpcGatewayMethod =
        methods().find { method -> method.getName() == methodName } ?: throw RuntimeException("Method not found")
}

interface GrpcGatewayMethod {
    private companion object {
        val printer: JsonFormat.Printer by lazy { JsonFormat.printer() }
    }

    fun getName(): String

    fun getProtoRequestMessageBuilder(): Message.Builder

    suspend fun map(request: ServerRequest): Message {
        val body = request.bodyToMono<String>().awaitSingle()
        val builder = getProtoRequestMessageBuilder()
        JsonFormat.parser().merge(body, builder)
        return builder.build()
    }

    suspend fun mapStream(request: ServerRequest): Flow<Message> {
        return request.bodyToFlow<String>().map {  body ->
            val builder = getProtoRequestMessageBuilder()
            JsonFormat.parser().merge(body, builder)
            builder.build()
        }
    }

    suspend fun handle(request: Message): Message

    suspend fun handleOutputStream(request: Message): Flow<Message>

    suspend fun handle(httpRequest: ServerRequest): ServerResponse {
        val request = map(httpRequest)
        val response = handle(request)
        val responseBody = printer.print(response)
        return ServerResponse.ok()
            .header("Content-Type", "application/json")
            .bodyValueAndAwait(responseBody)
    }

    suspend fun handleInputStream(httpRequest: ServerRequest): ServerResponse {
        val request = mapStream(httpRequest)
        request.collect { message ->
            handle(message)
        }
        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun handleOutputStream(httpRequest: ServerRequest): ServerResponse {
        val request = map(httpRequest)
        return ServerResponse.ok()
            .body<Message>(handleOutputStream(request))
            .awaitSingle()
    }
}
