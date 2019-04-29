package com.simplifier.rules.write;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WriteSimplifier {

    private static String editCellRegex = ".*editCell(((?!,).)*,){3}(((?!,).)*),.*\\n" +
            "(((((?!,).)*,){3}((?!(getCell|copyCell)(((?!,).)*,){3}\\3).)*\\n)*" +
            ".*editCell(((?!,).)*,){3}\\3,.*\\n)";

    private static String chromeDoublePasteRegex = ".*paste,(((?!,).)*,)(((?!,).)*,)(((?!,).)*,){6}(((?!,).)*,)(((?!,).)*,).*\\n" +
            "(((((?!,).)*,){3}((?!copy,(((?!,).)*,){8}\\7).)*\\n)*" +
            ".*paste,(((?!,).)*,){8}\\7\\9.*\\n)";

    private static String chromePasteRegex = ".*paste,(((?!,).)*,)(((?!,).)*),(((?!,).)*,){6}(((?!,).)*,)(((?!,).)*),.*\\n" +
            "(((((?!,).)*,){3}((?!copy,(((?!,).)*,){8}\\7).)*\\n)*" +
            ".*editField,(((?!,).)*,){9}((?!(\\3\\9)).)*,(((?!,).)*,){3}\\n)";

    private static String chromeConnectedPasteToEditFieldRegex = "(.*editField.*\\n)" +
            "((.*\\n)*)" +
            "(.*paste.*\\n)" +
            "((.*\\n)*)" +
            "(.*editField,(((?!,).)*,){8}(((?!,).)*,).*\\n)" +
            "(((((?!,).)*,){3}((?!copy,(((?!,).)*,){8}\\10).)*\\n)*" +
            ".*editField,(((?!,).)*,){8}\\10.*\\n)";

    private static String chromeDoubleEditRegex = "(.*editField,(((?!,).)*,){8}(((?!,).)*,).*\\n)" +
            "(((((?!,).)*,){3}((?!copy,(((?!,).)*,){8}\\4).)*\\n)*" +
            ".*editField,(((?!,).)*,){8}\\4.*\\n)";

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

        return firstMatcher.find() || secondMatcher.find();
    }

    public static String deleteRedundantChromePaste(String logs) {
        Pattern pattern = Pattern.compile(chromePasteRegex);
        Matcher matcher = pattern.matcher(logs);

        String newLogs = logs.replaceAll(chromePasteRegex, "$11");
        newLogs = newLogs.replaceAll(chromeDoublePasteRegex, "$11");

        if (matcher.find()) {
            return deleteRedundantChromePaste(newLogs);
        }

        return newLogs;
    }

    public static boolean isRedundantChromeEditField(String logs) {
        Pattern pastePattern = Pattern.compile(chromeConnectedPasteToEditFieldRegex);
        Pattern editPattern = Pattern.compile(chromeDoubleEditRegex);

        return pastePattern.matcher(logs).find() || editPattern.matcher(logs).find();
    }

    public static String deleteRedundantChromeEditField(String logs) {
        Pattern pattern = Pattern.compile(chromeConnectedPasteToEditFieldRegex);
        Matcher matcher = pattern.matcher(logs);

        logs = logs.replaceAll(chromeConnectedPasteToEditFieldRegex, "$1$2$5$7$12");

        if (matcher.find()) {
            return deleteRedundantChromeEditField(logs);
        }

        pattern = Pattern.compile(chromeDoubleEditRegex);
        matcher = pattern.matcher(logs);

        logs = logs.replaceAll(chromeDoubleEditRegex, "$6");

        if (matcher.find()) {
            return deleteRedundantChromeEditField(logs);
        }

        return logs;
    }
}
