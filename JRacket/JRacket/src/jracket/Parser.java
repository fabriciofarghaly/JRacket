package jracket;

import jracket.types.RacketBoolean;
import jracket.types.RacketExpression;
import jracket.types.RacketInteger;
import jracket.types.RacketSymbol;

import java.util.*;

/**
 * A jracket.Parser turns a String of Racket code into an equivalent RacketExpression.
 */
public class Parser {

    public static List<RacketExpression> parse(String expression) throws ParsingException {
        // pass 1: convert everything to tokens
        List<String> tokens = new ArrayList<String>();
        String currtoken = "";

        for (char c : expression.toCharArray()) {
            //char c = expression.charAt(currpos);

            // whitespace ends current token
            if (Character.isWhitespace(c)) {

                if (!currtoken.equals("")) {
                    tokens.add(currtoken);
                    currtoken = "";
                }
            }

            // parens or quote becomes a token
            else if (c == '(' || c == ')' || c == '\'') {

                // end current token & add
                if (!currtoken.equals("")) {
                    tokens.add(currtoken);
                    currtoken = "";
                }

                tokens.add(String.valueOf(c));
            } else {
                currtoken += c;
            }
        }
        if (!currtoken.equals("")) {
            tokens.add(currtoken);
            //currtoken = "";
        }

        //System.out.println("After pass 1: " + tokens);

        // pass 2: make lists (handle parentheses)
        List<Object> tokensWithLists = new ArrayList<Object>();
        Deque<List<Object>> stackOfLists = new ArrayDeque<List<Object>>();
        List<Object> currentList = tokensWithLists;

        for (String token : tokens) {
            //System.out.println("Have " + token);

            if (token.equals("(")) {
                stackOfLists.push(currentList);
                currentList = new ArrayList<Object>();
            } else if (token.equals(")")) {
                List<Object> expr = currentList;
                try {
                    currentList = stackOfLists.pop();
                } catch (NoSuchElementException e) {
                    throw new ParsingException("Too many closing parens in " + expression + ".  Already parsed "
                            + currentList);
                }
                currentList.add(expr);
            } else // regular token
            {
                currentList.add(token);
            }
        }
        if (!stackOfLists.isEmpty())
            throw new ParsingException("Missing close paren in " + expression + ". Already parsed "
                    + currentList);

        //System.out.println("After pass 2: " + tokensWithLists);

        // pass 3: replace quote (') with actual call to (quote ... )

        quotify(tokensWithLists);

        //System.out.println("After pass 3: " + tokensWithLists);

        // pass 4: turn strings into racket jracket.types

        List<RacketExpression> typedList = typify(tokensWithLists);

        //System.out.println("After pass 4: " + typedList);

        if (typedList.size() == 1) {
            //System.out.println("single item display:  " + typedList.get(0).toDisplayString());
        } else {
            for (RacketExpression t : typedList) {
                //System.out.println("multi item display:   " + t.toDisplayString());
            }
        }

        return typedList;
    }

    private static List<RacketExpression> typify(List<Object> lst) throws ParsingException
    {
        List<RacketExpression> returnLst = new ArrayList<RacketExpression>(lst.size());

        for (Object obj : lst)
        {
            // obj will either be a String or List<Object>
            if (obj instanceof String)
            {
                // if it's a string, this can be an integer, boolean, or symbol
                String objStr = (String)obj;

                // parse boolean
                if (objStr.startsWith("#"))
                {
                    if (objStr.equals("#t"))
                        returnLst.add(RacketBoolean.TRUE);
                    else if (objStr.equals("#f"))
                        returnLst.add(RacketBoolean.FALSE);
                    else throw new ParsingException("Cannot parse boolean value: " + objStr);

                    continue;
                }

                // parse integer
                try {
                    int i = Integer.parseInt(objStr);
                    returnLst.add(new RacketInteger(i));
                    continue;
                } catch (NumberFormatException e) { }

                // must be a symbol!
                returnLst.add(new RacketSymbol(objStr));
            }
            else {
                // obj wasn't a string, must be a list of objects.
                // so convert to a racket list of corresponding types
                List<RacketExpression> typedLst = typify((List<Object>)obj);
                returnLst.add(Utilities.javaListToRacketList(typedLst));
            }
        }
        return returnLst;
    }

    private static void quotify(List<Object> lst) throws ParsingException {
        for (int x = 0; x < lst.size(); x++) {
            Object obj = lst.get(x);

            // obj will either be a String or a List<Object>
            if ((obj instanceof String) && obj.equals("'")) {
                // count nested quote level
                int nestedQuoteLevel = 1;
                int locOfObjToQuote = x + 1;
                while (locOfObjToQuote < lst.size() && lst.get(locOfObjToQuote).equals("'")) {
                    locOfObjToQuote++;
                    nestedQuoteLevel++;
                }

                if (locOfObjToQuote == lst.size()) // nothing to quote!
                    throw new ParsingException("Nothing following quote in " + lst);

                Object objToQuote = lst.get(locOfObjToQuote);

                //System.out.println("object to quote is " + objToQuote);
                //System.out.println("nesting level is " + nestedQuoteLevel);

                if (objToQuote instanceof List) {
                    quotify((List<Object>) objToQuote);
                }

                // remove all the actual quotes from the lst
                for (int y = 0; y < nestedQuoteLevel; y++)
                    lst.remove(x);

                // now remove the element that we quoted
                lst.remove(x);

                // construct the replacement element
                List<Object> newlist = Arrays.asList("quote", objToQuote);
                for (int y = 0; y < nestedQuoteLevel - 1; y++)
                    newlist = Arrays.asList("quote", newlist);

                lst.add(x, newlist);
                //System.out.println("fixed = " + lst);

            } else if (obj instanceof List) {
                quotify((List<Object>) obj);
            }
        }
    }

}
