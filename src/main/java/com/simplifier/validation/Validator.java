package com.simplifier.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static void validateForIdName(String log) throws Exception {
        for (String action : log.split("\n")) {

            String regexId = "(((?!,).)*,){2}((?!OS-Clipboard|,).)*,(((?!,).)*,){5},.*";
            Pattern patternId = Pattern.compile(regexId);
            Matcher matcherId = patternId.matcher(action);

            String regexName = "(((?!,).)*,){2}((?!OS-Clipboard|,).)*,(((?!,).)*,){9},.*";
            Pattern patternName = Pattern.compile(regexName);
            Matcher matcherName = patternName.matcher(action);

            /*
            if (matcherId.matches() && matcherName.matches()) {
                throw new Exception("Target id or name was missed");
            }
            */
        }
    }
}
