package top.abosen.toys.ipv6.match;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author qiubaisen
 * @date 2020/7/21
 */
public class MatcherBuilder<T> {

    private final List<MatcherInfo<T>> matchers;

    public MatcherBuilder() {
        matchers = new ArrayList<>();
    }

    public MatcherBuilder<T> register(String pattern, T value) {
        return register(pattern, value, Integer.MAX_VALUE);
    }

    public MatcherBuilder<T> register(String pattern, T value, int order) {
        IPAddress address = new IPAddressString(pattern).getAddress();
        if (address == null) throw new IllegalArgumentException("非法的IP pattern:" + pattern);
        matchers.add(new MatcherInfo<>(pattern, address, value, order));
        return this;
    }

    public RouteMatcher<T> build() {
        List<MatcherInfo<T>> sorts = new ArrayList<>(matchers);
        Collections.sort(sorts);
        return new RouteMatcher<>(sorts);
    }
}
