/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2014, Yannis Gonianakis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.ripe.commons.ip;

import static java.math.BigInteger.ZERO;
import static net.ripe.commons.ip.RangeUtils.checkRange;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class PrefixUtils {

    private PrefixUtils() {
    }

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    boolean isLegalPrefix(AbstractIpRange<C, R> range) {
        int prefixLength = range.start().getCommonPrefixLength(range.end());
        C lowerBoundForPrefix = range.start().lowerBoundForPrefix(prefixLength);
        C upperBoundForPrefix = range.end().upperBoundForPrefix(prefixLength);
        return range.start().equals(lowerBoundForPrefix) && range.end().equals(upperBoundForPrefix);
    }

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    int getPrefixLength(AbstractIpRange<C, R> range) {
        Validate.isTrue(isLegalPrefix(range), range.toStringInRangeNotation() + " is not a legal prefix, cannot get prefix length!");
        return range.start().getCommonPrefixLength(range.end());
    }

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    Optional<R> findMinimumPrefixForPrefixLength(R range, int prefixLength) {
        checkRange(prefixLength, 0, range.start().bitSize());
        return findPrefixForPrefixLength(range, prefixLength, SizeComparator.<R>get());
    }

    public static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    Optional<R> findMaximumPrefixForPrefixLength(R range, int prefixLength) {
        checkRange(prefixLength, 0, range.start().bitSize());
        return findPrefixForPrefixLength(range, prefixLength, SizeComparator.<R>reverse());
    }

    private static <C extends AbstractIp<C, R>, R extends AbstractIpRange<C, R>>
    Optional<R> findPrefixForPrefixLength(R range, int prefixLength, Comparator<? super R> comparator) {
        List<R> prefixes = range.splitToPrefixes();
        Collections.sort(prefixes, comparator);
        for (R prefix : prefixes) {
            if (prefixLength >= PrefixUtils.getPrefixLength(prefix)) {
                return Optional.of(prefix);
            }
        }
        return Optional.absent();
    }

    // TODO(yg): generify and move to AbstractIp
    public static int findMaxPrefixLengthForAddress(Ipv6 address) {
        return getMaxValidPrefix(address.value());
    }

    private static int getMaxValidPrefix(BigInteger number) {
        int powerOfTwo = 0;
        int maxPowerOfTwo = powerOfTwo;

        while (powerOfTwo <= Ipv6.NUMBER_OF_BITS && number.divideAndRemainder(BigInteger.ONE.shiftLeft(powerOfTwo))[1].compareTo(ZERO) == 0) {
            maxPowerOfTwo = powerOfTwo;
            powerOfTwo++;
        }
        return Ipv6.NUMBER_OF_BITS - maxPowerOfTwo;
    }

}
