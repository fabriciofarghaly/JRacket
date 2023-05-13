package jracket.types;

import jracket.Frame;
import jracket.Interpreter;
import jracket.Utilities;

import java.util.Collections;
import java.util.List;

/**
 * A RacketClosure is any kind of RacketFunction defined with a lambda in
 * the Racket language itself, as opposed to a PrimitiveFunction, which is
 * coded in Java.
 */
public class RacketClosure extends RacketFunction {

    private final List<RacketSymbol> argumentNames;
    private final RacketExpression body;
    private final Frame environment;

    /**
     * Create a closure, given a list of arguments (as symbols), an expression
     * that is the body, and a Frame that is the environment.
     */
    public RacketClosure(List<RacketSymbol> args, RacketExpression bod, Frame env) {
        this.argumentNames = args;
        this.body = bod;
        this.environment = env;
    }

    /**
     * Return the argument names as symbols.
     */
    public List<RacketSymbol> getArgumentNames() {
        return Collections.unmodifiableList(argumentNames);
    }

    /**
     * Return the body of this closure.
     */
    public RacketExpression getBody() {
        return body;
    }

    /**
     * Return the environment of this closure that the body should be evaluated in.
     */
    public Frame getEnvironment() {
        return environment;
    }

    /**
     * Try to discover the name of this closure.  Does this by searching
     * the environment and returns 'anonymous' if not found.
     */
    public String getName() {
        RacketSymbol sym = environment.searchForValue(this);
        if (sym == null) {
            return "anonymous";
        } else {
            return sym.getSymbolName();
        }
    }

    /**
     * Apply the body of this function to the argument values given.
     * Return the result.
     */
    @Override
    public RacketExpression apply(List<RacketExpression> argValues) {
        // verify we have the same number of values as we do argument names
        Utilities.verifyArgumentCount(getName(), argValues, argumentNames.size());

        if (Interpreter.DEBUGGING)
            System.out.println("      Applying: " + this.toDetailedString());

        // create a new frame for the closure, with the closure's environment as its parent
        Frame newFrame = new Frame(environment);

        // bind the arguments to their values in the new frame
        for (int i = 0; i < argumentNames.size(); i++) {
            newFrame.defineVariable(argumentNames.get(i), argValues.get(i));
        }

        // evaluate the body in the new frame and return the result
        return body.eval(newFrame);
    }

    @Override
    public String toDisplayString() {
        return "#<function:" + getName() + ">";
    }

    @Override
    public String toDetailedString() {
        return "[Func:" + getName() + " " + argumentNames + " " + body + " " + environment + "]";
    }
}
