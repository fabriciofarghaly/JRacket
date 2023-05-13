package jracket;

import jracket.types.RacketExpression;
import jracket.types.RacketPrimitiveFunction;
import jracket.types.RacketSymbol;

import java.util.HashMap;
import java.util.Map;

/**
 * A jracket.Frame stores a mapping between variables and their corresponding
 * values.
 */
public class Frame {

    private final Frame parentFrame;
    private final Map<RacketSymbol, RacketExpression> table;

    /**
     * Constructor for the "global" frame (a frame lacking a parent).
     */
    public Frame() {
        this(null);
    }

    /**
     * Constructor for a frame that points to another frame.  Use this
     * when you create a new frame but lexical scope says you should
     * point to a parent frame if a variable isn't found in this frame.
     */
    public Frame(Frame parent) {
        parentFrame = parent;
        table = new HashMap<RacketSymbol, RacketExpression>();
    }

    /**
     * Look up the value of a variable, given its symbol.  Throw an
     * InterpreterException if not found (never return null).
     */
    public RacketExpression lookupVariableValue(RacketSymbol var) {
        RacketExpression value = table.get(var);
        if (value == null && parentFrame != null) {
            value = parentFrame.lookupVariableValue(var);
        }
        if (value == null) {
            throw new InterpreterException("Variable " + var + " is undefined.");
        }
        return value;
    }

    /**
     * Define a variable to have a certain value.
     */
    public void defineVariable(RacketSymbol var, RacketExpression value) {
        table.put(var, value);
    }

    /**
     * Set a variable to a new value (var must already exist).
     */
    public void setVariable(RacketSymbol var, RacketExpression value) {
		// WRITE ME -- OPTIONAL
    }

    /**
     * Search for a value in a frame and return a corresponding variable
     * as a symbol.  This is only here to let functions find their names to
     * enable pretty-printing.
     */
    public RacketSymbol searchForValue(RacketExpression value) {
        for (Map.Entry<RacketSymbol, RacketExpression> e : table.entrySet()) {
            if (e.getValue().equals(value))
                return e.getKey();
        }
        if (parentFrame != null)
            return parentFrame.searchForValue(value);

        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("jracket.Frame:{");

        for (Map.Entry<RacketSymbol, RacketExpression> entry : table.entrySet()) {
            if (!(entry.getValue() instanceof RacketPrimitiveFunction)) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
                sb.append(", ");
            }
        }

        sb.delete(sb.length() - 2, sb.length());
        sb.append("}");

        return sb.toString();
    }
}
