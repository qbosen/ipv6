package top.abosen.toys.ipv6

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration

@SpringBootApplication
@EnableConfigurationProperties(Config::class)
class Ipv6Application

fun main(args: Array<String>) {
    runApplication<Ipv6Application>(*args)
}


@ConfigurationProperties("ipv6")
@ConstructorBinding
data class Config(val host: String,val port:Int)