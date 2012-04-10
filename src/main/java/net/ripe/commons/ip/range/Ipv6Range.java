package net.ripe.commons.ip.range;

import net.ripe.commons.ip.resource.InternetResourceRange;
import net.ripe.commons.ip.resource.Ipv6;

public class Ipv6Range extends AbstractRange<Ipv6, Ipv6Range> implements InternetResourceRange<Ipv6, Ipv6Range> {

    protected Ipv6Range(Ipv6 start, Ipv6 end) {
        super(start, end);
    }

    @Override
    protected Ipv6Range newInstance(Ipv6 start, Ipv6 end) {
        return new Ipv6Range(start, end);
    }

    public static Ipv6RangeBuilder from(Ipv6 from) {
        return new Ipv6RangeBuilder(from);
    }

    public static class Ipv6RangeBuilder extends AbstractRangeBuilder<Ipv6, Ipv6Range> {
        protected Ipv6RangeBuilder(Ipv6 from) {
            super(from, Ipv6Range.class);
        }
    }
}