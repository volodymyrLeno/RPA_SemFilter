package com.simplifier.rules.read;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadSimplifier {

    public static boolean isRedundantCopy(String logs) {
        String regex = "(.*copy.*\\n)((((?!paste|editField|editCell).)*\\n)*)(.*copy.*\\n*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(logs);

        return matcher.find();
    }

    public static String deleteRedundantCopy(String logs) {
        String regex = "(.*copy.*\\n)((((?!paste|editField|editCell).)*\\n)*)(.*copy.*\\n*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(logs);

        String newLogs = logs.replaceAll(regex, "$2$5");

        if (matcher.find()) {
            return deleteRedundantCopy(newLogs);
        }

        return newLogs;
    }

    public static boolean isSingleCopy(String logs) {
        String regex = "((((?!copy).)*\\n)*)(.*copy.*\\n)((((?!paste|copy).)*\\n)*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(logs);

        return matcher.matches();
    }

    public static String deleteSingleCopy(String logs) {
        String regex = "((((?!copy).)*\\n)*)(.*copy.*\\n)((((?!paste|copy).)*\\n)*)";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(logs);

        if (matcher.matches()) {
            logs = logs.replaceAll(regex, "$1$5");
            return deleteSingleCopy(logs);
        }

        return logs;
    }
}
