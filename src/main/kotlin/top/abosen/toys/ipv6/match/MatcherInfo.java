package top.abosen.toys.ipv6.match;

import inet.ipaddr.IPAddress;
import org.jetbrains.annotations.NotNull;

/**
 * @author qiubaisen
 * @date 2020/7/21
 */
public class MatcherInfo<T> implements Comparable<MatcherInfo<T>>{
    private int order;
    private String pattern;
    private T value;
    private IPAddress address;

    protected MatcherInfo(String pattern, IPAddress address, T value, int order) {
        this.pattern = pattern;
        this.value = value;
        this.order = order;
        this.address = address;
    }


    public boolean matches(IPAddress ip) {
        return ip != null && address.contains(ip);
    }

    @Override
    public int compareTo(@NotNull MatcherInfo<T> o) {
        return order - o.order;
    }
// region getter or setter

    public int getOrder() {
        return order;
    }

    public String getPattern() {
        return pattern;
    }

    public T getValue() {
        return value;
    }

// endregion getter or setter
}