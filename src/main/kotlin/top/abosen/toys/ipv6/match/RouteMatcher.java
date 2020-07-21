package top.abosen.toys.ipv6.match;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

import java.util.List;

/**
 * @author qiubaisen
 * @date 2020/7/21
 */
public class RouteMatcher<T> {

    private final List<MatcherInfo<T>> sortedList;

    public RouteMatcher(List<MatcherInfo<T>> sortedList) {
        this.sortedList = sortedList;
    }

    public T matching(String ip) {
        IPAddress otherIp = new IPAddressString(ip).getAddress();
        if (otherIp == null) return null;

        for (MatcherInfo<T> matcher : sortedList) {
            if (matcher.matches(otherIp)) {
                return matcher.getValue();
            }
        }
        return null;
    }

}
