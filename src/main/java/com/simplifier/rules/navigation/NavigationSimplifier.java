package com.simplifier.rules.navigation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NavigationSimplifier {

    public static boolean isRedundantClickTextField(String logs) {
        String regex = "(.*clickTextField.*\\n)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(logs);

        return matcher.find();
    }

    public static String deleteRedundantClickTextField(String logs) {
        String regex = "(.*clickTextField.*\\n)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(logs);

        String newLogs = logs.replaceAll(regex, "");

        if (matcher.find()) {
            return deleteRedundantClickTextField(newLogs);
        }

        return newLogs;
    }
}
