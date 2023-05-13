package jracket.types;

import jracket.InterpreterException;
import jracket.Utilities;

import java.util.Arrays;
import java.util.List;

/**
 * A RacketPrimitiveFunction is a built-in function to our mini-Racket language
 * that cannot be expressed in Racket itself (or if it were, it would be very
 * slow, such as if we tried to express multiplication in terms of addition.)
 * <p>
 * You may add more primitive functions if you'd like.  Follow the examples
 * below: you'll need to define a new class and add the name of the function
 * to ALL_PRIMITIVE_FUNCTIONS defined at the end of this file.
 */
public abstract class RacketPrimitiveFunction extends RacketFunction {

    private final String name;

    public RacketPrimitiveFunction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract RacketExpression apply(List<RacketExpression> args);

    public String toDisplayString() {
        return "#<primitive-function:" + getName() + ">";
    }

    public String toDetailedString() {
        return "[PrimFunc]";
    }

    public final static RacketPrimitiveFunction ADD = new RacketPrimitiveFunction("+") {
        @Override
        public RacketExpression apply(List<RacketExpression> args) {
            int ans = 0;
            for (RacketExpression t : args) {
                if (t instanceof RacketInteger) {
                    ans += ((RacketInteger) t).getValue();
                } else {
                    throw new InterpreterException("+ given non-integer: " + t);
                }
            }
            return new RacketInteger(ans);
        }
    };

    public final static RacketPrimitiveFunction MULTIPLY = new RacketPrimitiveFunction("*") {
        @Override
        public RacketExpression apply(List<RacketExpression> args) {
            int ans = 1;
            for (RacketExpression t : args) {
                if (t instanceof RacketInteger) {
                    ans *= ((RacketInteger) t).getValue();
                } else {
                    throw new InterpreterException("+ given non-integer: " + t);
                }
            }
            return new RacketInteger(ans);
        }
    };

    public final static RacketPrimitiveFunction SUBTRACT = new RacketPrimitiveFunction("-") {
        @Override
        public RacketExpression apply(List<RacketExpression> args) {
            RacketExpression t = args.get(0);
            if (!(t instanceof RacketInteger)) {
                throw new InterpreterException("- given non-integer: " + t);
            }
            int ans = ((RacketInteger) t).getValue();

            if (args.size() == 1) {
                return new RacketInteger(-ans);
            }

            for (RacketExpression t2 : args.subList(1, args.size())) {
                if (t2 instanceof RacketInteger) {
                    ans -= ((RacketInteger) t2).getValue();
                } else {
                    throw new InterpreterException("- given non-integer: " + t2);
                }
            }
            return new RacketInteger(ans);
        }
    };

    public final static RacketPrimitiveFunction DIVIDE = new RacketPrimitiveFunction("/") {
        @Override
        public RacketExpression apply(List<RacketExpression> args) {
            RacketExpression t = args.get(0);
            if (!(t instanceof RacketInteger)) {
                throw new InterpreterException("/ given non-integer: " + t);
            }
            int ans = ((RacketInteger) t).getValue();

            if (args.size() == 1) {
                return new RacketInteger(1 / ans);
            }

            for (RacketExpression t2 : args.subList(1, args.size())) {
                if (t2 instanceof RacketInteger) {
                    ans /= ((RacketInteger) t2).getValue();
                } else {
                    throw new InterpreterException("/ given non-integer: " + t2);
                }
            }
            return new RacketInteger(ans);
        }
    };

    public final static RacketPrimitiveFunction LESS_THAN = new RacketPrimitiveFunction("<") {
        @Override
        public RacketBoolean apply(List<RacketExpression> args) {

            Utilities.verifyArgumentCount("<", args, 2);
            Utilities.verifyArgumentTypes("<", args, RacketInteger.class, RacketInteger.class);

            int a = ((RacketInteger) args.get(0)).getValue();
            int b = ((RacketInteger) args.get(1)).getValue();

            if (a < b) {
                return RacketBoolean.TRUE;
            } else {
                return RacketBoolean.FALSE;
            }
        }
    };

    public final static RacketPrimitiveFunction CONS = new RacketPrimitiveFunction("cons") {
        @Override
        public RacketList apply(List<RacketExpression> args) {

            Utilities.verifyArgumentCount("cons", args, 2);
            Utilities.verifyArgumentTypes("cons", args, RacketExpression.class, RacketList.class);

            return new RacketList(args.get(0), (RacketList) args.get(1));
        }
    };

    public final static RacketPrimitiveFunction CAR = new RacketPrimitiveFunction("car") {
        @Override
        public RacketExpression apply(List<RacketExpression> args) {

            Utilities.verifyArgumentCount("car", args, 1);
            Utilities.verifyArgumentTypes("car", args, RacketList.class);

            return ((RacketList) args.get(0)).getCar();
        }
    };

    public final static RacketPrimitiveFunction CDR = new RacketPrimitiveFunction("cdr") {
        @Override
        public RacketList apply(List<RacketExpression> args) {

            Utilities.verifyArgumentCount("cdr", args, 1);
            Utilities.verifyArgumentTypes("cdr", args, RacketList.class);

            return ((RacketList) args.get(0)).getCdr();
        }
    };

    public final static RacketPrimitiveFunction INTEGER_EQUALS = new RacketPrimitiveFunction("=") {
        @Override
        public RacketBoolean apply(List<RacketExpression> args) {

            Utilities.verifyArgumentCount("=", args, 2);
            Utilities.verifyArgumentTypes("=", args, RacketInteger.class, RacketInteger.class);

            int a = ((RacketInteger) args.get(0)).getValue();
            int b = ((RacketInteger) args.get(1)).getValue();

            if (a == b) {
                return RacketBoolean.TRUE;
            } else {
                return RacketBoolean.FALSE;
            }
        }
    };

    public final static RacketPrimitiveFunction ANY_EQUALS = new RacketPrimitiveFunction("equal?") {
        @Override
        public RacketBoolean apply(List<RacketExpression> args) {

            Utilities.verifyArgumentCount("equal?", args, 2);

            RacketExpression a = args.get(0);
            RacketExpression b = args.get(1);

            if (a.equals(b)) {
                return RacketBoolean.TRUE;
            } else {
                return RacketBoolean.FALSE;
            }
        }
    };

    public final static RacketPrimitiveFunction IS_NULL = new RacketPrimitiveFunction("null?") {
        @Override
        public RacketBoolean apply(List<RacketExpression> args) {

            Utilities.verifyArgumentCount("null?", args, 1);
            Utilities.verifyArgumentTypes("null?", args, RacketList.class);

            if (args.get(0).equals(RacketList.EMPTY_LIST)) {
                return RacketBoolean.TRUE;
            } else {
                return RacketBoolean.FALSE;
            }
        }
    };

    public static final List<RacketPrimitiveFunction> ALL_PRIMITIVE_FUNCTIONS
            = Arrays.asList(ADD, MULTIPLY, SUBTRACT, DIVIDE, LESS_THAN,
            CONS, CAR, CDR, INTEGER_EQUALS, IS_NULL, ANY_EQUALS);
}
