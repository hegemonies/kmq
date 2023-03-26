package site.hegemonies.kmq.configuration.grpc

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import org.springframework.context.annotation.Configuration
import site.hegemonies.kmq.metrics.GrpcMetrics

@Configuration
class GrpcMetricInterceptorConfiguration(
    private val grpcMetrics: GrpcMetrics
) {

    @GrpcGlobalServerInterceptor
    fun grpcMetricInterceptor() = object : ServerInterceptor {
        override fun <ReqT : Any, RespT : Any> interceptCall(
            call: ServerCall<ReqT, RespT>,
            headers: Metadata,
            next: ServerCallHandler<ReqT, RespT>
        ): ServerCall.Listener<ReqT> =
            grpcMetrics.recordInboundRequestDuration { next.startCall(call, headers) }
    }
}
