package com.simplifier.rules.read;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class consists of methods that operate on log:
 * check if log contains redundant read action, and if so,
 * remove them and return new log.
 * List of "Read" actions:
 * <ul>
 *     <li>
 *         <h6>copy</h1>
 *         <p>
 *         If there are any number of actions between two "copy" actions
 *         except "paste" action, the first "copy" action is redundant.
 *         </p>
 *         <p>
 *         If the log contains single "copy" action and there is no "paste"
 *         action after it, the "copy" action is redundant.
 *         </p>
 *     </li>
 * </ul>
 */
public class ReadSimplifier {

    /**
     * This is regular expression that corresponds to the case when
     * there are any number of actions except "paste" action
     * between two "copy" actions.
     */
    private static String redundantFirstCopyRegex = "((((?!,).)*,){3}copy.*\\n)" +
                                                    "((((?!(((?!,).)*,){3}paste,(((?!,).)*,){15}).)*\\n)*" +
                                                    "(((?!,).)*,){3}copy.*\\n*)";

    /**
     * This is regular expression that corresponds to the case when
     * the log contains single "copy" action and there is no paste
     * action after it.
     */
    private static String singleCopyRegex = "((((?!(((?!,).)*,){3}copy,(((?!,).)*,){15}).)*\\n)*)" +
                                            "((((?!,).)*,){3}(copy,).*\\n*)" +
                                            "((((?!(((?!,).)*,){3}(paste|copy),(((?!,).)*,){15}).)*\\n*)*)";

    /**
     * This method is used to check if the log contains a
     * pattern that matches {@link ReadSimplifier#redundantFirstCopyRegex},
            * i.e the log contains two "copy" actions, ant there are any number of action
     * between them except "paste" action.
     * <p>
     * The method checks if the log contains a pattern which contains
     * any number of actions except "paste" action between two "copy"
            * actions.
     * </p>
            *
            * @param   log the log that contains input actions.
     * @return  <code>true</code> if the log contains pattern that
     *          matches {@link ReadSimplifier#redundantFirstCopyRegex};
     *          <code>false</code> otherwise.
     */
    public static boolean containsRedundantCopy(String log) {
        Pattern p = Pattern.compile(redundantFirstCopyRegex);
        Matcher matcher = p.matcher(log);

        return matcher.find();
    }

    /**
     * This method is used to check if the log contains a
     * pattern that matches {@link ReadSimplifier#singleCopyRegex},
     * i.e the log contains single "copy" action and there is no "paste"
     * action after it.
     * <p>
     * The method checks if the log contains a pattern which contains
     * single "copy" action and there is no "paste" action after it.
     * </p>
     *
     * @param   log the log that contains input actions.
     * @return  <code>true</code> if the log matches {@link ReadSimplifier#singleCopyRegex}.
     *          <code>false</code> otherwise.
     */
    public static boolean containsSingleCopy(String log) {
        Pattern p = Pattern.compile(singleCopyRegex);
        Matcher matcher = p.matcher(log);

        return matcher.matches();
    }

    /**
     * This method is used to remove all redundant copy actions
     * from the log.
     * <p>
     * If the log contains pattern that matches {@link ReadSimplifier#redundantFirstCopyRegex},
     * the method will remove first "copy" action in the pattern. $4 is a parameter
     * of {@link ReadSimplifier#redundantFirstCopyRegex} that is responsible for
     * every action after first "copy" action and the second "copy" action in the pattern.
     * The method will be called again if the log contains redundant "copy" action after replacing the
     * pattern that matches {@link ReadSimplifier#redundantFirstCopyRegex} until there are none of them.
     * </p>
     *
     * @param log   the log that contains input actions.
     * @return      the log without redundant "copy" actions.
     */
    public static String deleteRedundantCopy(String log) {
        log = log.replaceAll(redundantFirstCopyRegex, "$4");

        if (containsRedundantCopy(log)) {
            return deleteRedundantCopy(log);
        }

        return log;
    }

    /**
     * This method is used to remove all single "copy" actions
     * from the log.
     * <p>
     * If the log contains pattern that matches {@link ReadSimplifier#singleCopyRegex},
     * the method will remove single "copy" action from the log. $1 is a parameter
     * of {@link ReadSimplifier#singleCopyRegex} that is responsible for
     * every action before single "copy" action. $13 is a parameter
     * of {@link ReadSimplifier#singleCopyRegex} that is responsible for
     * every action after single "copy" action.
     * </p>
     *
     * @param   log the log that contains input actions.
     * @return  the log without single "copy" action.
     */
    public static String deleteSingleCopy(String log) {
        log = log.replaceAll(singleCopyRegex, "$1$13");

        if (containsSingleCopy(log)) {
            return deleteSingleCopy(log);
        }

        return log;
    }
}
