package jracket.types;


import jracket.Frame;

/**
 * A RacketInteger is the integer data type in Racket.
 */
public class RacketInteger extends RacketExpression {

    private final int value;

    /**
     * Construct a new integer.
     */
    public RacketInteger(int value) {
        this.value = value;
    }

    /**
     * RacketIntegers are self-evaluating.
     */
    @Override
    public RacketExpression eval(Frame env) {
        return this;
    }

    /**
     * Get the value of this integer (used to convert a RacketInteger to a Java
     * int).
     */
    public int getValue() {
        return value;
    }

    @Override
    public String toDisplayString() {
        return Integer.toString(value);
    }

    @Override
    public String toDetailedString() {
        return "[Int " + toDisplayString() + "]";
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RacketInteger)) {
            return false;
        }

        RacketInteger other = (RacketInteger) o;
        return value == other.value;
    }
}
