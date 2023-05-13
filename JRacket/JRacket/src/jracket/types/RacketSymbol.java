package jracket.types;

import jracket.Frame;

/**
 * A RacketSymbol is a symbol in Racket.  For example, in
 * (define x 3), x is a symbol.
 */
public class RacketSymbol extends RacketExpression {

    private final String sym;

    /**
     * Construct a symbol from a String.
     */
    public RacketSymbol(String sym) {
        this.sym = sym;
    }

    /**
     * Racket symbols, when evaluated, should be interpreted as variables
     * and looked up in the current environment.
     */
    @Override
    public RacketExpression eval(Frame env) {
        printDebuggingString();
        return env.lookupVariableValue(this);
    }

    /**
     * Return the symbol as a String.
     */
    public String getSymbolName() {
        return sym;
    }

    public String toDisplayString() {
        return sym;
    }

    public String toDetailedString() {
        return "[SYM " + toDisplayString() + "]";
    }

    @Override
    public int hashCode() {
        return sym.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RacketSymbol)) return false;

        RacketSymbol other = (RacketSymbol)o;
        return sym.equals(other.sym);
    }
}
