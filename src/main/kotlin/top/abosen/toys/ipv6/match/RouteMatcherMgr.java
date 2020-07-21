package top.abosen.toys.ipv6.match;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author qiubaisen
 * @date 2020/7/21
 */
public class RouteMatcherMgr<T> {

    private final List<RouteMatcher<T>> matchers;
    private final ReadWriteLock lock;
    private volatile boolean sorted;
    private List<RouteMatcher<T>> sortedView;

    public RouteMatcherMgr(Class<T> valueType) {
        lock = new ReentrantReadWriteLock();
        matchers = new ArrayList<>();
    }

    public RouteMatcherMgr<T> register(String pattern, T value) {
        return register(pattern, value, Integer.MAX_VALUE);
    }

    public RouteMatcherMgr<T> register(String pattern, T value, int order) {
        IPAddress address = new IPAddressString(pattern).getAddress();
        if (address == null) throw new IllegalArgumentException("非法的IP pattern:" + pattern);
        matchers.add(new RouteMatcher<>(pattern, address, value, order));

        return this;
    }

    public T matching(String ip) {
        IPAddress otherIp = new IPAddressString(ip).getAddress();
        if (otherIp == null) return null;

        for (RouteMatcher<T> matcher : sortedView) {
            if (matcher.matches(otherIp)) {
                return matcher.getValue();
            }
        }
        return null;
    }


    public void printDetail() {
        System.out.println(matchers);
    }
}
