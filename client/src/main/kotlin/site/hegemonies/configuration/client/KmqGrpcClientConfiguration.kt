package site.hegemonies.configuration.client

import io.grpc.netty.NettyChannelBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import site.hegemonies.client.grpc.IKmqGrpcClient
import site.hegemonies.client.grpc.KmqGrpcClient
import site.hegemonies.client.grpc.mapper.KmqGrpcClientMapper
import site.hegemonies.configuration.properties.KmqGrpcClientProperties
import site.hegemonies.kmq.queue.contract.QueueServiceGrpcKt

@Configuration
class KmqGrpcClientConfiguration(
    private val kmqGrpcClientProperties: KmqGrpcClientProperties,
    private val kmqGrpcClientMapper: KmqGrpcClientMapper
) {

    @Bean
    fun kmqGrpcClient(): IKmqGrpcClient {
        val (host, port, timeout) = kmqGrpcClientProperties

        val channel = NettyChannelBuilder
            .forAddress(host, port)
            .defaultLoadBalancingPolicy("round_robin")
            .usePlaintext()
            .build()

        val stub = QueueServiceGrpcKt.QueueServiceCoroutineStub(channel)

        return KmqGrpcClient(stub, kmqGrpcClientMapper)
    }
}
