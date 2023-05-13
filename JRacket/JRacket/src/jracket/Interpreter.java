package jracket;

import jracket.types.RacketExpression;
import jracket.types.RacketPrimitiveFunction;
import jracket.types.RacketSymbol;

import java.util.List;
import java.util.Scanner;

/**
 * An jracket.Interpreter runs a read-eval-print loop for a subset of the Racket
 * language.
 */
public class Interpreter {

    private final Frame globalFrame;

    /**
     * This variable can be set to true or false to enable helpful
     * debugging messages by the interpreter.
     */
    public static boolean DEBUGGING = true;

    /**
     * Construct a new jracket.Interpreter.
     */
    public Interpreter() {
        globalFrame = new Frame();
        addPrimitivesToGlobalFrame();
        addDerivedFunctionsToGlobalFrame();
    }

    /**
     * Start the interpreter's read-eval-print loop.
     */
    public void run() {
        Scanner scan = new Scanner(System.in);
        String inputLine;

        System.out.println("Welcome to JRacket!  Type \"end\" to stop the interpreter.");

        while (true) {

            System.out.print(">>> ");
            if (scan.hasNextLine()) {
                inputLine = scan.nextLine();

                while (insideParentheses(inputLine)) {
                    System.out.print("  > ");
                    if (scan.hasNextLine()) {
                        inputLine += scan.nextLine();
                    } else {
                        break;
                    }
                }
            } else {
                break;  // end of file
            }

            if (inputLine.equals("end")) {
                break;
            }

            try {
                List<RacketExpression> expressions = Parser.parse(inputLine);

                for (RacketExpression expr : expressions) {
                    RacketExpression value = expr.eval(globalFrame);
                    System.out.println("==> " + value);
                }
            } catch (ParsingException e) {
                System.out.println(e);
            } catch (InterpreterException e) {
                System.out.println(e);
            }

        }
        System.out.println();
    }

    /**
     * Called by the constructor to add Racket's primitive functions to the
     * global frame.
     */
    private void addPrimitivesToGlobalFrame() {
        for (RacketPrimitiveFunction pf : RacketPrimitiveFunction.ALL_PRIMITIVE_FUNCTIONS) {
            globalFrame.defineVariable(new RacketSymbol(pf.getName()), pf);
        }
    }

    /**
     * Called by the constructor to add Racket's derived functions (functions
     * expressed in terms of primitives that you want available upon start-up)
     * to the interpreter.
     */
    private void addDerivedFunctionsToGlobalFrame() {
        String[] startup = new String[]{
                //"(define != (lambda (x y) (if (= x y) #f #t)))",
                //"(define > (lambda (x y) (if (< x y) #f #t)))",
                //"(define not (lambda (x) (if x #f #t)))",
                //"(define or (lambda (x y) (if x #t (if y #t #f))))",
                //"(define and (lambda (x y) (if x (if y #t #f) #f)))",
                //"(define (map f lst) (if (null? lst) '() (cons (f (car lst)) (map f (cdr lst)))))"
        };

        for (String s : startup) {
            for (RacketExpression expr : Parser.parse(s)) {
                expr.eval(globalFrame);
            }
        }
    }

    private static boolean insideParentheses(String s) {
        int countleft = 0, countright = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                countleft++;
            } else if (c == ')') {
                countright++;
            }
        }
        return countleft > countright;
    }
}
