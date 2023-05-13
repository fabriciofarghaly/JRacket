package jracket.types;

import jracket.Frame;

/**
 * A RacketBoolean is a boolean type in Racket -- either #t or #f.
 * The constructor is private so that you can't create new RacketBooleans ---
 * just use RacketBoolean.TRUE and RacketBoolean.FALSE.
 */
public class RacketBoolean extends RacketExpression {

    private final boolean value;
    public static final RacketBoolean TRUE = new RacketBoolean(true);
    public static final RacketBoolean FALSE = new RacketBoolean(false);

    /**
     * Private constructor. Don't call this -- instead use RacketBoolean.TRUE and
     * RacketBoolean.FALSE.
     */
    private RacketBoolean(boolean value) {
        this.value = value;
    }

    /**
     * Return the value of this RacketBoolean as a Java boolean.
     */
    private boolean getValue() {
        return value;
    }

    /**
     * RacketBooleans are self-evaluating.
     */
    @Override
    public RacketExpression eval(Frame env) {
        return this;
    }

    @Override
    public String toDisplayString() {
        return (this == TRUE) ? "#t" : "#f";
    }

    @Override
    public String toDetailedString() {
        return "[Bool " + toDisplayString() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RacketBoolean)) {
            return false;
        }

        RacketBoolean other = (RacketBoolean) o;
        return other.value == value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }
}
