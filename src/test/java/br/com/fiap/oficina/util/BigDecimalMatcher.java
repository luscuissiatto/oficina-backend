package br.com.fiap.oficina.util;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.math.BigDecimal;

public class BigDecimalMatcher extends TypeSafeMatcher<Number> {

    private final BigDecimal expectedValue;

    private BigDecimalMatcher(BigDecimal expectedValue) {
        this.expectedValue = expectedValue;
    }

    @Override
    protected boolean matchesSafely(Number actualValue) {
        if (actualValue == null) {
            return false;
        }
        return new BigDecimal(actualValue.toString()).compareTo(expectedValue) == 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(expectedValue);
    }

    public static TypeSafeMatcher<Number> comparesEqualTo(BigDecimal expected) {
        return new BigDecimalMatcher(expected);
    }
}