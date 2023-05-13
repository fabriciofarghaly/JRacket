package jracket.types;

import jracket.Frame;

import java.util.List;

/**
 * RacketFunction is an abstract base class that represents any kind of
 * callable object in Racket.  The two subclasses are primitive functions,
 * which are built into the Racket language (that is, can't be implemented
 * in Racket itself), and closures, which are functions that are defined
 * in terms of primitive functions.
 */
public abstract class RacketFunction extends RacketExpression {

    /**
     * Applies this function to the argument values given.
     */
    public abstract RacketExpression apply(List<RacketExpression> argumentValues);

    /**
     * RacketFunctions are self-evaluating.
     */
    @Override
    public RacketExpression eval(Frame env) {
        return this;
    }
}
