package site.hegemonies.kmq.router.http

import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.bodyValueAndAwait

interface GrpcGateway {
    fun methods(): List<GrpcGatewayMethod>
    suspend fun handle(methodName: String, request: ServerRequest): ServerResponse {
        val method = methods().find { method -> method.getName() == methodName }
            ?: throw RuntimeException("Method not found")
        return method.handle(request)
    }
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

    suspend fun handle(request: Message): Message

    suspend fun handle(httpRequest: ServerRequest): ServerResponse {
        val request = map(httpRequest)
        val response = handle(request)
        val responseBody = printer.print(response)
        return ServerResponse.ok()
            .header("Content-Type", "application/json")
            .bodyValueAndAwait(responseBody)
    }
}
