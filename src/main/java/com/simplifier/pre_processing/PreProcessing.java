package com.simplifier.pre_processing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PreProcessing {

    public static String sortLog(String logs) {
        List<String> actions = Arrays.asList(logs.split("\n"));
        Collections.sort(actions);
        return actions.stream().map(el -> el + "\n").collect(Collectors.joining());
    }

    public static String deleteChromeClipboardCopy(String logs) {
        String regex = "(.*Chrome,copy.*\\n)(.*OS-Clipboard,copy.*\\n)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(logs);

        String newLogs = logs.replaceAll(regex, "$1");

        if (matcher.find()) {
            return deleteChromeClipboardCopy(newLogs);
        }

        return newLogs;
    }

    public static String mergeNavigationCellCopy(String logs) {
        String getCellRegex = "((((?!,).)*,)(.*)getCell,,(.*)\\n(((?!editCell|getRange|getCell).)*\\n)*)(((?!,).)*,).*OS-Clipboard,copy,(,((?!,).)*),+\\n";
        String getRangeRegex = "((((?!,).)*,)(.*)getRange,,(.*)\\n(((?!editCell|getRange|getCell).)*\\n)*)(((?!,).)*,).*OS-Clipboard,copy,(,((?!,).)*),+\\n";
        String editCellRegex = "((((?!,).)*,)(.*)editCell,,(.*)\\n(((?!editCell|getRange|getCell).)*\\n)*)(((?!,).)*,).*OS-Clipboard,copy,(,((?!,).)*),+\\n";
//        String getRangeRegex = "((.*)getRange,,(.*)\\n(((?!editCell|getCell).)*\\n)*).*OS-Clipboard,copy,(,((?!,).)*),+\\n";
//        String editCellRegex = "((.*)editCell,,(.*)\\n(((?!getCell|getRange).)*\\n)*).*OS-Clipboard,copy,(,((?!,).)*),+\\n";

        if (Pattern.compile(getCellRegex).matcher(logs).find()) {
            logs = logs.replaceAll(getCellRegex, "$1$8$4copyCell,$10$5\n");
            return mergeNavigationCellCopy(logs);
        }

        if (Pattern.compile(getRangeRegex).matcher(logs).find()) {
//            logs = logs.replaceAll(getRangeRegex, "$1$2copyCell,$6$3\n");
            logs = logs.replaceAll(getRangeRegex, "$1$8$4copyRange,$10$5\n");
            return mergeNavigationCellCopy(logs);
        }

        if (Pattern.compile(editCellRegex).matcher(logs).find()) {
//            logs = logs.replaceAll(editCellRegex, "$1$2copyCell,$6$3\n");
            logs = logs.replaceAll(editCellRegex, "$1$8$4copyCell,$10$5\n");
            return mergeNavigationCellCopy(logs);
        }

        logs = logs.replaceAll("(.*getCell.*(\\n|$)|.*getRange.*(\\n|$))", "");
        return logs;
    }
}
