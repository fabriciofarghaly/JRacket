package jracket.types;

import jracket.Frame;

/**
 * A RacketExpression is the superclass from which all possible
 * expressions in racket are derived.
 */
public abstract class RacketExpression {

    /**
     * Evaluate this expression and return its value.
     */
    public abstract RacketExpression eval(Frame env);

    /**
     * Return a String representation of this value that the interpreter
     * will print.
     */
    public abstract String toDisplayString();

    /**
     * Return a String representation of this value that is more detailed
     * than toDisplayString().
     */
    public abstract String toDetailedString();

    public void printDebuggingString() {
        if (jracket.Interpreter.DEBUGGING) {
            System.out.println("      Evaluating: " + this);
        }
    }

    public String toString() {
        return toDisplayString();
    }


}
