package com.simplifier.rules.write;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WriteSimplifier {

    private static String editCellRegex = ".*editCell,(((?!,).)*,){4}(((?!,).)*),.*\\n" +
            "(((((?!,).)*,){3}((?!(getCell|copyCell),(((?!,).)*,){4}\\3).)*\\n)*" +
            ".*editCell,(((?!,).)*,){4}\\3,.*\\n)";

    private static String chromeDoublePasteRegex =
            ".*paste,(((?!,).)*,)(((?!,).)*,)(((?!,).)*,){6}(((?!,).)*,)(((?!,).)*,).*\\n" +
                    "(((((?!,).)*,){3}((?!copy,(((?!,).)*,){8}\\7).)*\\n)*" +
                    ".*paste,(((?!,).)*,){8}\\7.*\\n)";

    private static String chromePasteRegex =
            ".*paste,(((?!,).)*,)(((?!,).)*),(((?!,).)*,){6}(((?!,).)*,)(((?!,).)*),.*\\n" +
                    "(((((?!,).)*,){3}((?!copy,(((?!,).)*,){8}\\7).)*\\n)*" +
                    ".*editField,(((?!,).)*,){9}((?!(\\3\\9)).)*,(((?!,).)*,){3}\\n)";

    private static String chromeCopyBetweenEditRegex =
            "((((?!paste).)*\\n)*)" +
                    "((.*paste,(((?!,).)*,){8}(((?!,).)*),.*\\n)*)" +
                    "(((.*\\n)*)" +
                    "(.*editField,(((?!,).)*,){8}(((?!,).)*),(((?!,).)*),.*\\n)" +
                    "(" +
                    "((((?!,).)*,){3}((?!copy,(((?!,).)*,){8}\\16).)*\\n)*" +
                    ".*editField,(((?!,).)*,){8}\\16(((?!(,|\\18)).)*),.*\\n))";

    private static String chromePasteBetweenEditRegex =
            "(.*editField,(((?!,).)*,){8}(((?!,).)*,)(((?!,).)*),.*\\n)" +
                    "(" +
                    "((((?!,).)*,){3}paste,(((?!,).)*,)(((?!,).)*),(((?!,).)*,){6}\\4.*\\n)*" +
                    ".*editField,(((?!,).)*,){8}\\4(((\\6\\14|,).)*),.*\\n)";

    private static String chromeDoubleEditRegex =
            "(.*editField,(((?!,).)*,){8}(((?!,).)*,)(((?!,).)*),.*\\n)" +
                    "(.*editField,(((?!,).)*,){8}\\4.*\\n)";

    public static boolean isRedundantEditCell(String logs) {
        Pattern pattern = Pattern.compile(editCellRegex);
        Matcher matcher = pattern.matcher(logs);

        return matcher.find();
    }

    public static String deleteRedundantEditCell(String logs) {
        Pattern pattern = Pattern.compile(editCellRegex);
        Matcher matcher = pattern.matcher(logs);

        String newLogs = logs.replaceAll(editCellRegex, "$5");

        if (matcher.find()) {
            return deleteRedundantEditCell(newLogs);
        }

        return newLogs;
    }

    public static boolean isRedundantChromePaste(String logs) {
        Pattern firstPattern = Pattern.compile(chromePasteRegex);
        Matcher firstMatcher = firstPattern.matcher(logs);

        Pattern secondPattern = Pattern.compile(chromeDoublePasteRegex);
        Matcher secondMatcher = secondPattern.matcher(logs);

        return secondMatcher.find();
    }

    public static String deleteRedundantChromePaste(String logs) {
        Pattern pattern = Pattern.compile(chromeDoublePasteRegex);
        Matcher matcher = pattern.matcher(logs);

//        String newLogs = logs.replaceAll(chromePasteRegex, "$11");
        logs = logs.replaceAll(chromeDoublePasteRegex, "$11");

        if (matcher.find()) {
            return deleteRedundantChromePaste(logs);
        }

        return logs;
    }

    public static boolean isRedundantChromeEditField(String logs) {
        Pattern editCopyPattern = Pattern.compile(chromeCopyBetweenEditRegex);
        Pattern editPastePattern = Pattern.compile(chromePasteBetweenEditRegex);

        return editCopyPattern.matcher(logs).find() &&
                !editPastePattern.matcher(logs).find();
    }

    public static String deleteRedundantChromeEditField(String logs) {
        Pattern patternCopy = Pattern.compile(chromeCopyBetweenEditRegex);
        Matcher matcherCopy = patternCopy.matcher(logs);

        Pattern patternPaste = Pattern.compile(chromePasteBetweenEditRegex);
        Matcher matcherPaste = patternPaste.matcher(logs);

        Pattern patternDouble = Pattern.compile(chromeDoubleEditRegex);
        Matcher matcherDouble = patternDouble.matcher(logs);

        if (matcherCopy.find() && !matcherPaste.find()) {

            logs = matcherCopy.replaceAll(mr -> {
                if (mr.group(8) != null &&
                        mr.group(16) != null &&
                        mr.group(8).equals(mr.group(16))) {
                    return mr.group(1) + mr.group(10);
                }
                return mr.group(1) + mr.group(11) + mr.group(20);
            });

            logs = logs.replaceAll(chromePasteBetweenEditRegex, "$8");
            return deleteRedundantChromeEditField(logs);
        }

        if (matcherDouble.find()) {
            logs = logs.replaceAll(chromeDoubleEditRegex, "$8");
            return deleteRedundantChromeEditField(logs);
        }

        return logs;
    }

    public static boolean isRedundantPasteIntoCell(String log) {
        String regex = ".*pasteIntoCell,(((?!,).)*,)((((?!,).)*,){3}(((?!,).)*,)).*\\n" +
                "(((((?!,).)*,){3}((?!copyCell,(((?!,).)*,){4}\\6).)*\\n)*" +
                ".*pasteIntoCell,(((?!,).)*,)\\3.*\\n)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(log);

        return matcher.find();
    }

    public static String deleteRedundantPasteIntoCell(String log) {
        String regex = ".*pasteIntoCell,(((?!,).)*,)((((?!,).)*,){3}(((?!,).)*,)).*\\n" +
                "(((((?!,).)*,){3}((?!copyCell,(((?!,).)*,){4}\\6).)*\\n)*" +
                ".*pasteIntoCell,(((?!,).)*,)\\3.*\\n)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(log);

        if (matcher.find()) {
            log = log.replaceAll(regex, "$8");
            return deleteRedundantPasteIntoCell(log);
        }

        return log;
    }

}
