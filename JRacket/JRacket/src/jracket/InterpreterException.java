package jracket;

/**
 * An InterpreterException is thrown for any kind of dynamic error generated
 * by the interpreter -- wrong # of args to a function, wrong type given
 * to a function, bad syntax in a define/lambda/if, etc.
 */
public class InterpreterException extends RuntimeException {

    public InterpreterException() {
    }

    public InterpreterException(String message) {
        super(message);
    }


}
