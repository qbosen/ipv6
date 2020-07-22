package top.abosen.toys.ipv6.thrift

import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.server.TServer
import org.apache.thrift.server.TSimpleServer
import org.apache.thrift.server.TThreadPoolServer
import org.apache.thrift.transport.TServerSocket
import org.apache.thrift.transport.TServerTransport
import org.apache.thrift.transport.TSocket
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import top.abosen.toys.ipv6.Config
import java.net.InetSocketAddress
import javax.annotation.PostConstruct
import kotlin.concurrent.thread


/**
 * @author qiubaisen
 * @date 2020/7/20
 */

@RestController
class Client(@Autowired private val config: Config) {
    @GetMapping("hello")
    fun hello(): String {
        val transport = TSocket(config.connectHost, config.connectPort)
        val protocol = TBinaryProtocol(transport)
        transport.use {
            it.open()
            return HelloService.Client(protocol).hello()
        }
    }

    @GetMapping("direct")
    fun direct(): String = "direct"
}


@Service
class HelloServiceImpl(@Autowired private val config: Config) : HelloService.Iface {
    override fun hello(): String {
        println("thrift invoke")
        return "hello world"
    }

    var server: TServer? = null

    @PostConstruct
    fun server() {
        val serverTransport: TServerTransport = TServerSocket(InetSocketAddress(config.serverHost, config.serverPort))
        val server = TSimpleServer(TThreadPoolServer.Args(serverTransport).processor(HelloService.Processor(this)))
        this.server = server
        thread(isDaemon = true) {
            server.serve()
        }
    }
}