package com.simplifier.validation;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static void validateForIdOrName(String log) {
        String actionRegex = "(\"([^\"]|\"\")*\",){2}(\"Chrome\"|\"Excel\"),(\"editField\"|\"copy\"|\"copyCell\"|" +
                             "\"copyRange\"|\"paste\"|\"pasteIntoCell\"|\"pasteIntoRange\"|\"editCell\"|" +
                             "\"editRange\"|\"getCell\").*";

        String[] actions = log.split("\n");
        Arrays.stream(actions)
                .filter(action -> Pattern.compile(actionRegex).matcher(action).matches())
                .forEach(Validator::checkActionForValidIdOrName);
    }

    private static void checkActionForValidIdOrName(String action) {
        String regexId = "(\"([^\"]|\"\")*\",){8}(\"([^\"]|\"\")+\",).*";
        String regexName = "(\"([^\"]|\"\")*\",){12}(\"([^\"]|\"\")+\",).*";

        Pattern patternId = Pattern.compile(regexId);
        Matcher matcherId = patternId.matcher(action);

        Pattern patternName = Pattern.compile(regexName);
        Matcher matcherName = patternName.matcher(action);

        if (!matcherId.matches() && !matcherName.matches()) {
            throw new IllegalArgumentException("Target id or name was missed");
        }
    }
}
