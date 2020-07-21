package top.abosen.toys.ipv6.match

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * @author qiubaisen
 * @date 2020/7/21
 */

class ExceptionTest {

    private lateinit var matcherMgr: MatcherBuilder<String>

    @BeforeEach
    fun `初始化默认mgr`() {
        matcherMgr = MatcherBuilder()
    }

    @Test
    fun `测试错误的pattern`() {
        assertThrows(IllegalArgumentException::class.java) { matcherMgr.register("1.3.4.5.6", "错误的模式") }
        assertThrows(IllegalArgumentException::class.java) { matcherMgr.register("256.1.1.1", "错误的模式") }
        assertThrows(IllegalArgumentException::class.java) { matcherMgr.register("good::1", "错误的模式") }
    }

    @Test
    fun `测试错误的subnet`() {
        assertThrows(IllegalArgumentException::class.java) { matcherMgr.register("1.3.4.5/63", "错误的模式") }
        assertThrows(IllegalArgumentException::class.java) { matcherMgr.register("1.3.4.5/33", "错误的模式") }
        assertThrows(IllegalArgumentException::class.java) { matcherMgr.register("1.3.4.5/-1", "错误的模式") }
        assertThrows(IllegalArgumentException::class.java) { matcherMgr.register("fc00::1/-1", "错误的模式") }
        assertThrows(IllegalArgumentException::class.java) { matcherMgr.register("fc00::1/133", "错误的模式") }
    }

}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class `测试CIDR表示法` {
    private lateinit var matcher: RouteMatcher<Handler>

    enum class Handler {
        `168网段`,
        `确定的ip`,
        `23网段`,
        `ipv6-64`,
        `fc00-7`,

    }

    @BeforeAll
    fun `初始化默认mgr`() {
        matcher = with(MatcherBuilder<Handler>()) {
            register("192.168.0.0/16", Handler.`168网段`)
            register("127.0.0.1", Handler.确定的ip)
            register("10.7.23.0/24", Handler.`23网段`)
            register("fe80::/64", Handler.`ipv6-64`)
            register("fc00::/7", Handler.`fc00-7`)
            build()
        }
    }

    @Test
    fun `测试CIDR 表示法`() {
        assertEquals(Handler.`168网段`, matcher.matching("192.168.6.30"))
        assertEquals(Handler.确定的ip, matcher.matching("127.0.0.1"))
        assertNull(matcher.matching("127.0.0.2"))
        assertEquals(Handler.`23网段`, matcher.matching("10.7.23.1"))
        assertEquals(Handler.`23网段`, matcher.matching("10.7.23.255"))
        assertNull(matcher.matching("10.7.24.255"))
        assertEquals(Handler.`ipv6-64`, matcher.matching("fe80::a28c:fdff:fec5:c0d5"))
        assertEquals(Handler.`ipv6-64`, matcher.matching("fe80:0000:0000:0000:0fff:0006:0000:0000"))
        assertNull(matcher.matching("fe80:0002:0003:0000:0000:0006:0000:0000"))
        assertEquals(Handler.`fc00-7`, matcher.matching("fc00::a28c:fdff:fec5:c0d5"))
        assertEquals(Handler.`fc00-7`, matcher.matching("fd00::1"))
        assertNull(matcher.matching("fe00::1"))

    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class `测试 - * 表示法` {
    private lateinit var matcher: RouteMatcher<Handler>

    enum class Handler {
        `168网段`,
        `确定的ip`,
        `23网段,7-8`,
        `ipv6-64`,
        `fc00-7`,

    }

    @BeforeAll
    fun `初始化默认mgr`() {
        matcher = with(MatcherBuilder<Handler>()) {
            register("192.168.*.*", Handler.`168网段`)
            register("127.0.0.1", Handler.确定的ip)
            register("10.7-8.23.*", Handler.`23网段,7-8`)
            register("fe80:0:0:0:*", Handler.`ipv6-64`)
            register("fc00-fdff:*", Handler.`fc00-7`)
            build()
        }
    }

    @Test
    fun `测试- * 表示法`() {
        assertEquals(Handler.`168网段`, matcher.matching("192.168.6.30"))
        assertEquals(Handler.确定的ip, matcher.matching("127.0.0.1"))
        assertNull(matcher.matching("127.0.0.2"))
        assertEquals(Handler.`23网段,7-8`, matcher.matching("10.7.23.1"))
        assertEquals(Handler.`23网段,7-8`, matcher.matching("10.8.23.255"))
        assertNull(matcher.matching("10.7.24.255"))
        assertNull(matcher.matching("10.8.24.255"))
        assertEquals(Handler.`ipv6-64`, matcher.matching("fe80::a28c:fdff:fec5:c0d5"))
        assertEquals(Handler.`ipv6-64`, matcher.matching("fe80:0000:0000:0000:0fff:0006:0000:0000"))
        assertNull(matcher.matching("fe80:0002:0003:0000:0000:0006:0000:0000"))
        assertEquals(Handler.`fc00-7`, matcher.matching("fc00::a28c:fdff:fec5:c0d5"))
        assertEquals(Handler.`fc00-7`, matcher.matching("fd00::1"))
        assertNull(matcher.matching("fe00::1"))
    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class `优先级测试` {
    private lateinit var matcher: RouteMatcher<Int>

    @BeforeAll
    fun `初始化默认mgr`() {
        matcher = with(MatcherBuilder<Int>()) {
            register("192.168.*.*", 168, 60)
            register("192.168.6.*", 6, 30)
            register("192.168.6.30", 30, 1)
            build()
        }
    }

    @Test
    fun `优先级测试`() {
        assertEquals(6, matcher.matching("192.168.6.17"))
        assertEquals(30, matcher.matching("192.168.6.30"))
        assertEquals(168, matcher.matching("192.168.7.17"))
    }

}