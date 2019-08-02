package com.simplifier.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static void validateForIdName(String log) throws Exception {
        for (String action : log.split("\n")) {
            String actionRegex = "(((?!,).)*,){2}(Chrome|Excel),(editField|copy|paste|editCell|getCell).*";
            Pattern actionPattern = Pattern.compile(actionRegex);
            Matcher actionMatcher = actionPattern.matcher(action);

            if (actionMatcher.matches()) {
                String regexId = "(((?!,).)*,){8}(((?!,).)+,).*";
                Pattern patternId = Pattern.compile(regexId);
                Matcher matcherId = patternId.matcher(action);

                String regexName = "(((?!,).)*,){12}(((?!,).)+,).*";
                Pattern patternName = Pattern.compile(regexName);
                Matcher matcherName = patternName.matcher(action);

                if (!matcherId.matches() && !matcherName.matches()) {
                    throw new Exception("Target id or name was missed");
                }
            }
        }
    }
}
