package com.simplifier.pre_processing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class consists of methods that operate on the log:
 * sort log, delete, transform, identify and merge specific actions.
 */
public class PreProcessing {

    /**
     * This method is used to sort log actions by timestamp.
     * <p>
     * The method extracts actions from the {@code log} into the
     * {@code List<String>} and sort them. A newline symbol is added
     * to each element of the list and the list is transformed into
     * the string log with sorted actions.
     * </p>
     *
     * @param log   the log that contains input actions.
     * @return      sorted log
     */
    public static String sortLog(String log) {
        List<String> actions = Arrays.asList(log.split("\n"));
        Collections.sort(actions);

        return actions.stream().map(el -> el + "\n").collect(Collectors.joining());
    }

    /**
     * Finds patterns that contain "editCell" or "editRange" actions after any of "copy"
     * actions and changes "editCell" or "editRange" actions to "pasteIntoCell" and
     * "pasteIntoRange" actions respectively.
     * <p>
     * If the log contains a pattern that matches {@code cellRegex} regular expressions, the method will
     * replace "editCell" action from the pattern to "pasteIntoCell", and if the log contains {@code rangeRegex}
     * regular expression the method will replace "editRange" action from the pattern to "pasteIntoRange" action.
     * </p>
     *
     * @param   log the log that contains input actions.
     * @return  log with replaced "editCell" or "editRange" action
     *          with "pasteIntoCell" or "pasteIntoRange" actions.
     */
    public static String identifyPasteAction(String log) {
        log = transformCopyCellEditToPaste(log);
        log = transformCopyRangeEditToPaste(log);
        log = transformChromeCopyEditToPaste(log);

        return log;
    }

    /**
     * Finds a pattern {@code cellRegex} that corresponds to the case when there are "copyCell"
     * action, any number of actions and "editCell" action which target.value is equal to "copyCell"
     * action content (\\4).
     *
     * @param log the log that contains input actions.
     * @return log with replaced "editCell" action with "pasteIntoCell" action.
     */
    public static String transformCopyCellEditToPaste(String log) {
        String cellRegex = "(.*\"copyCell\",(\"([^\"]|\"\")*\",)(\"([^\"]|\"\")*\"),.*\\n)" +
                           "((.*\\n)*)" +
                           "((.*)\"editCell\",(\"([^\"]|\"\")*\",)(\"([^\"]|\"\")*\",)((\"([^\"]|\"\")*\",){7}\\4.*)\\n*)";

        if (log.contains("editCell") && Pattern.compile(cellRegex).matcher(log).find()) {
            log = log.replaceAll(cellRegex, "$1$6$9\"pasteIntoCell\",$10$4,$14\n");

            return transformCopyCellEditToPaste(log);
        }

        return log;
    }

    /**
     * Finds a pattern {@code rangeRegex} that corresponds to the case when there are "copyRange"
     * action, any number of actions and "editRange" action which target.value is equal to "copyRange"
     * action content (\\8).
     *
     * @param log the log that contains input actions.
     * @return log with replaced "editRange" action with "pasteIntoRange" action.
     */
    public static String transformCopyRangeEditToPaste(String log) {
        String rangeRegex = "(.*\"copyRange\",(\"([^\"]|\"\")*\",)(\"([^\"]|\"\")*\",)(\"([^\"]|\"\")*\",){7}(\"([^\"]|\"\")*\",).*\\n)" +
                            "((.*\\n)*)" +
                            "((.*)\"editRange\",(\"([^\"]|\"\")*\",)(\"([^\"]|\"\")*\",)((\"([^\"]|\"\")*\",){7}\\8.*)\\n*)";

        if (log.contains("editRange") && Pattern.compile(rangeRegex).matcher(log).find()) {
            log = log.replaceAll(rangeRegex, "$1$10$13\"pasteIntoRange\",$14$4$18\n");

            return transformCopyRangeEditToPaste(log);
        }

        return log;
    }

    /**
     * Finds a pattern {@code chromeRegex} that corresponds to the case when there is Chrome "copy"
     * action, any number of actions and "editCell" action which target.value is equal to "copy"
     * action content (\\4).
     *
     * @param log the log that contains input actions.
     * @return log with replaced "editCell" action with "pasteIntoCell" action.
     */
    public static String transformChromeCopyEditToPaste(String log) {
        String chromeRegex = "(.*\"Chrome\",\"copy\",(\"([^\"]|\"\")*\",)(\"([^\"]|\"\")*\",).*\\n)" +
                             "((.*\\n)*)" +
                             "((.*)\"editCell\",(\"([^\"]|\"\")*\",)(\"([^\"]|\"\")*\",)((\"([^\"]|\"\")*\",){7}\\4.*\\n*))";

        if (log.contains("editCell") && Pattern.compile(chromeRegex).matcher(log).find()) {
            log = log.replaceAll(chromeRegex, "$1$6$9\"pasteIntoCell\",$10$4$14\n");

            return transformChromeCopyEditToPaste(log);
        }

        return log;
    }

    /**
     * Merges Excel navigation cell actions and OS-Clipboard "copy" actions then deletes
     * all "getRange" and "getCell" actions.
     *
     * @param log the log that contains input actions.
     * @return log with merged "editCell", "getRange" and "getCell" actions and OS-Clipboard "copy" action.
     */
    public static String mergeNavigationCellCopy(String log) {
        log = mergeGetCellCopy(log);
        log = mergeGetRangeCopy(log);
        log = mergeEditCellCopy(log);

        log = log.replaceAll("((\"([^\"]|\"\")*\",){3}\"getCell\",.*\\n*)|" +
                                   "((\"([^\"]|\"\")*\",){3}\"getRange\",.*\\n*)", "");

        return log;
    }

    /**
     * Merges Excel "getCell" actions and OS-Clipboard "copy" actions then deletes
     * all "getCell" actions.
     * <p>
     * The method contains {@code getCellRegex} regular expression that correspond to the case
     * when there are any number of actions except "editCell", "getRange" and "getCell" between
     * "getCell" action and "copy" action. If the log contains pattern that matches this regular
     * expression, the method will replace the pattern with new action: "copyCell" action.
     * </p>
     *
     * @param log the log that contains input actions.
     * @return log with merged "getCell" actions and OS-Clipboard "copy" action.
     */
    public static String mergeGetCellCopy(String log) {
        String getCellRegex = "((\"([^\"]|\"\")*\",)((\"([^\"]|\"\")*\",){2})\"getCell\",(\"([^\"]|\"\")*\",){2}(.*)\\n" +
                              "(((?!(\"([^\"]|\"\")*\",){3}(\"editCell\"|\"getRange\"|\"getCell\"),(\"([^\"]|\"\")*\",){9}).)*\\n)*)" +
                              "(\"([^\"]|\"\")*\",)(\"([^\"]|\"\")*\",)\"OS-Clipboard\",\"copy\",((\"([^\"]|\"\")*\",){2}).*\\n*";

        if (log.contains("getCell") && Pattern.compile(getCellRegex).matcher(log).find()) {
            log = log.replaceAll(getCellRegex, "$1$17$4\"copyCell\",$21$9\n");

            return mergeGetCellCopy(log);
        }

        return log;
    }

    /**
     * Merges Excel "getRange" actions and OS-Clipboard "copy" actions then deletes
     * all "getRange" actions.
     * <p>
     * The method contains {@code getRangeRegex} regular expression that correspond to the case
     * when there are any number of actions except "editCell", "getRange" and "getCell" between
     * "getRange" action and "copy" action. If the log contains pattern that matches this regular
     * expression, the method will replace the pattern with new action: "copyRange" action.
     * </p>
     *
     * @param log the log that contains input actions.
     * @return log with merged "getRange" actions and OS-Clipboard "copy" action.
     */
    public static String mergeGetRangeCopy(String log) {
        String getRangeRegex = "((\"([^\"]|\"\")*\",)((\"([^\"]|\"\")*\",){2})\"getRange\",(\"([^\"]|\"\")*\",){2}(.*)\\n" +
                               "(((?!(\"([^\"]|\"\")*\",){3}(\"editCell\"|\"getRange\"|\"getCell\"),(\"([^\"]|\"\")*\",){9}).)*\\n)*)" +
                               "(\"([^\"]|\"\")*\",)(\"([^\"]|\"\")*\",)\"OS-Clipboard\",\"copy\",((((?!,).)*,){2}).*\\n*";

        if (log.contains("getRange") && Pattern.compile(getRangeRegex).matcher(log).find()) {
            log = log.replaceAll(getRangeRegex, "$1$17$4\"copyRange\",$21$9\n");

            return mergeGetRangeCopy(log);
        }

        return log;
    }

    /**
     * Merges Excel "editCell" actions and OS-Clipboard "copy" actions.
     * <p>
     * The method contains {@code editCellRegex} regular expression that correspond to the case
     * when there are any number of actions except "editCell", "getRange" and "getCell" between
     * "editCell" action and "copy" action. If the log contains pattern that matches this regular
     * expression, the method will replace the pattern with new action: "copyCell" action.
     * </p>
     *
     * @param log the log that contains input actions.
     * @return log with merged "editCell" actions and OS-Clipboard "copy" action.
     */
    public static String mergeEditCellCopy(String log) {
        String editCellRegex = "((\"([^\"]|\"\")*\",)((\"([^\"]|\"\")*\",){2})\"editCell\",(\"([^\"]|\"\")*\",){2}(.*)\\n" +
                               "(((?!(\"([^\"]|\"\")*\",){3}(\"editCell\"|\"getRange\"|\"getCell\"),(\"([^\"]|\"\")*\",){9}).)*\\n)*)" +
                               "(\"([^\"]|\"\")*\",)(\"([^\"]|\"\")*\",)\"OS-Clipboard\",\"copy\",((\"([^\"]|\"\")*\",){2}).*\\n*";

        if (log.contains("editCell") && Pattern.compile(editCellRegex).matcher(log).find()) {
            log = log.replaceAll(editCellRegex, "$1$17$4\"copyCell\",$21$9\n");

            return mergeEditCellCopy(log);
        }

        return log;
    }

    /**
     * Deletes OS-Clipboard "copy" action if it is preceded by
     * Chrome "copy" action.
     * <p>
     * $1 is a parameter of {@code String regex} that is responsible for Chrome
     * "copy" action. The method will be called again if the log contains other
     * patterns that match {@code String regex} until there are none of them.
     * </p>
     *
     * @param   log the log that contains input actions.
     * @return  the log without OS-Clipboard "copy" actions that are preceded by
     *          Chrome "copy" actions.
     */
    public static String deleteChromeClipboardCopy(String log) {
        String regex = "((\"([^\"]|\"\")*\",){2}\"Chrome\",\"copy\",.*\\n)" +
                       "((\"([^\"]|\"\")*\",){2}\"OS-Clipboard\",\"copy\",.*\\n*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(log);

        if (matcher.find()) {
            log = log.replaceAll(regex, "$1");

            return deleteChromeClipboardCopy(log);
        }

        return log;
    }
}
