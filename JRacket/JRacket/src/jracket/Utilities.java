package jracket;

import jracket.types.RacketExpression;
import jracket.types.RacketList;

import java.util.ArrayList;
import java.util.List;

public class Utilities {

    /**
     * Converts a List of RacketTypes into an equivalent cons-cell-based list.
     * The items in the list are not copied.
     */
    public static RacketList javaListToRacketList(List<RacketExpression> lst) {
        if (lst.isEmpty())
            return RacketList.EMPTY_LIST;

        List<RacketExpression> cdrlst = lst.subList(1, lst.size());
        RacketExpression carlst = lst.get(0);

        return new RacketList(carlst, javaListToRacketList(cdrlst));
    }

    /**
     * Converts a Racket list into an equivalent java list.  The items
     * are not copied.  The argument must be either a RacketPair (that is
     * a list), or the empty list.
     */
    public static List<RacketExpression> racketListToJavaList(RacketList lst) {
        List<RacketExpression> ans = new ArrayList<RacketExpression>();

        while (lst != RacketList.EMPTY_LIST) {
            ans.add(lst.getCar());
            lst = lst.getCdr();
        }
        return ans;
    }

    /**
     * Verifies that that the list argValues has count elements in it, and throws
     * an exception if it doesn't.
     */
    public static void verifyArgumentCount(String funcName, List<RacketExpression> argValues, int count) {
        if (argValues.size() != count) {
            throw new InterpreterException(funcName + " takes " + count
                    + " arguments; given " + argValues.size() + ": " + argValues);
        }
    }

    /**
     * Verifies that the list argValues has the specified jracket.types in it, and throws
     * an exception otherwise.
     */
    public static void verifyArgumentTypes(String funcName, List<RacketExpression> argValues, Class<?>... datatypes) {
        for (int x = 0; x < argValues.size(); x++) {
            RacketExpression t = argValues.get(x);
            Class<?> clazz = datatypes[x];

            if (!clazz.isInstance(t))
                throw new InterpreterException("Argument type mismatch in "
                        + funcName + ": " + t + " is not a " + clazz.getSimpleName());
        }
    }

}
