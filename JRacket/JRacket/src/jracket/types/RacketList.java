package jracket.types;

import jracket.Frame;
import jracket.InterpreterException;
import jracket.Utilities;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A RacketList represents a (linked) list in Racket. Note that our version of
 * Racket does not support pairs that are not lists. So (cons 1 2) is an error.
 */
public class RacketList extends RacketExpression {

    private final RacketExpression car;
    private final RacketList cdr;
    public static final RacketList EMPTY_LIST = new RacketList();

    /**
     * Create a new empty list. Do not call this when you need an empty list,
     * instead use RacketList.EMPTY_LIST.
     */
    private RacketList() {
        this.car = this.cdr = null;
    }

    /**
     * Create a new list from a CAR and a CDR.
     */
    public RacketList(RacketExpression car, RacketList cdr) {
        assert car != null;
        assert cdr != null;

        this.car = car;
        this.cdr = cdr;
    }

    /**
     * Retrieve the CAR of this list.
     */
    public RacketExpression getCar() {
        return car;
    }

    /**
     * Retrieve the CDR of this list.
     */
    public RacketList getCdr() {
        return cdr;
    }

    /**
     * Evaluate this RacketList *as an expression* rather than as a *literal
     * list*. This function should handle any expression which takes the form of a
     * list, which is pretty much everything that's not a boolean, symbol, or
     * integer. That is, this function handles defines, lambdas, quotes, function
     * calls, etc.
     */
    @Override
    public RacketExpression eval(Frame env) {
        printDebuggingString();

        if (this.equals(EMPTY_LIST)) {
            throw new InterpreterException("Can't evaluate the empty list: " + this);
        }

        // from here on, we know our expression is a regular racket list
        // that is non-empty.

        if (getCar().equals(new RacketSymbol("quote"))) {
            return evalQuote(env);
        } else if (getCar().equals(new RacketSymbol("define"))) {
            return evalDefine(env);
        } else if (getCar().equals(new RacketSymbol("lambda"))) {
            return evalLambda(env);
        } else if (getCar().equals(new RacketSymbol("if"))) {
            return evalIf(env);
        } else { // must be a function call!
            return evalCall(env);
        }
    }

    /**
     * A function call expression is a list containing any number of
     * sub-expressions: (e1 e2 ... en)  The first sub-expression, e1,
     * when evaluated, must evaluate to a RacketFunction of some kind.
     * Assuming it does, evaluate e2 through en, then apply e1 to
     * the argument values.
     */
    private RacketExpression evalCall(Frame env) {
        List<RacketExpression> exprAsList = Utilities.racketListToJavaList(this);
        RacketExpression funcObj = exprAsList.get(0).eval(env);
        if (!(funcObj instanceof RacketFunction)) {
            throw new InterpreterException("Can't call " + funcObj + " as a function.");
        }

        List<RacketExpression> args = exprAsList.subList(1, exprAsList.size());
        List<RacketExpression> evaluatedArgs = args.stream()
                .map(arg -> arg.eval(env))
                .collect(Collectors.toList());

        return ((RacketFunction) funcObj).apply(evaluatedArgs);
    }


    /**
     * A lambda expression is a list of three items: the symbol LAMBDA, followed
     * by a RacketList of RacketSymbols (the names of the arguments), followed by
     * a single expression that constitutes the body of the lambda.
     */
    private RacketExpression evalLambda(Frame env) {
        List<RacketExpression> exprAsList = Utilities.racketListToJavaList(this);
        if (exprAsList.size() <= 2) {
            throw new InterpreterException("Wrong number of parts to lambda: " + exprAsList);
        }

        RacketList argNamesList = (RacketList) exprAsList.get(1);
        List<RacketExpression> argNameExprs = Utilities.racketListToJavaList(argNamesList);
        List<RacketSymbol> argNames = argNameExprs.stream()
                .map(expr -> (RacketSymbol) expr)
                .collect(Collectors.toList());
        RacketExpression body = exprAsList.get(2);

        return new RacketClosure(argNames, body, env);
    }


    /**
     * A quoted expression is a list of two items: the symbol QUOTE, and an
     * expression. The result of evaluating the quoted expression is the
     * expression inside.
     */
    private RacketExpression evalQuote(Frame env) {
        List<RacketExpression> exprAsList = Utilities.racketListToJavaList(this);

        if (exprAsList.size() != 2) {
            throw new InterpreterException("Wrong number of parts to quote: " + exprAsList);
        }

        return exprAsList.get(1);
    }

    /**
     * An if expression is a list of four items: the symbol IF, followed by three
     * sub-expressions, called expr1, expr2, and expr3. To evaluate an if,
     * evaluate expr1 and if it evaluates to TRUE, then return the evaluated
     * expr2. Otherwise, return the evaluated expr3.
     */
    private RacketExpression evalIf(Frame env) {
        List<RacketExpression> exprAsList = Utilities.racketListToJavaList(this);

        if (exprAsList.size() != 4) {
            throw new InterpreterException("Wrong number of parts in if: " + exprAsList);
        }

        RacketExpression test = exprAsList.get(1).eval(env);

        if (test.equals(RacketBoolean.TRUE)) {
            return exprAsList.get(2).eval(env);
        } else {
            return exprAsList.get(3).eval(env);
        }
    }


    /**
     * A define expression is a list of three items: the symbol DEFINE, a symbol
     * to be used as a variable, and an expression. Evaluating the define
     * expression should evaluate the sub-expression and store the result in the
     * variable.
     */
    private RacketExpression evalDefine(Frame env) {
        List<RacketExpression> exprAsList = Utilities.racketListToJavaList(this);

        if (exprAsList.size() <= 2) {
            throw new InterpreterException("Wrong number of parts to define: " + exprAsList);
        }

        RacketExpression var = exprAsList.get(1);
        RacketExpression value = exprAsList.get(2).eval(env);
        env.defineVariable((RacketSymbol) var, value);

        return value;
    }


    public String toDisplayString() {
        if (this == EMPTY_LIST) {
            return "()";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(getCar().toDisplayString());

        RacketList curr = getCdr();
        while (curr != EMPTY_LIST) {
            sb.append(" ");
            sb.append(curr.getCar().toDisplayString());
            curr = curr.getCdr();
        }
        sb.append(")");

        return sb.toString();
    }

    public String toDetailedString() {
        if (this == EMPTY_LIST) {
            return "[List]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[List");
        sb.append(getCar().toDisplayString());

        RacketList curr = getCdr();
        while (curr != EMPTY_LIST) {
            sb.append(" ");
            sb.append(curr.getCar().toDisplayString());
            curr = curr.getCdr();
        }
        sb.append("]");

        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(car, cdr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RacketList)) {
            return false;
        }

        RacketList other = (RacketList) o;

        if (this == EMPTY_LIST) {
            return other == EMPTY_LIST;
        } else {
            return this.car.equals(other.car) && this.cdr.equals(other.cdr);
        }
    }
}
