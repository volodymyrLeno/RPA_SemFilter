package com.simplifier;

import com.simplifier.pre_processing.PreProcessing;
import com.simplifier.rules.navigation.NavigationSimplifier;
import com.simplifier.rules.read.ReadSimplifier;
import com.simplifier.rules.write.WriteSimplifier;
import com.simplifier.validation.Validator;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        String filePath = args[0];
        Utils utils = new Utils();

        Map<String, StringBuilder> cases = utils.readLogFromFile(filePath);
        Map<String, String> result = getSimplifiedLog(cases);
        String writableLog = getWritableLog(result);
        String[] sortedLog = writableLog.split("\\r?\\n");
        Arrays.sort(sortedLog, Comparator.comparing(s -> s.substring(2)));
        writeLogToFile(filePath, sortedLog);

//        System.out.println("Result\n");
//        System.out.println(StringUtils.join(Arrays.asList(sortedLog), "\n"));
    }

    private static Map<String, String> getSimplifiedLog(Map<String, StringBuilder> cases) {
        return cases.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, logCase -> {
                    String logs = logCase.getValue().toString();
                    return applySimplifier(logs);
                }));
    }

    private static String getWritableLog(Map<String, String> result) {
        return result.entrySet().stream()
                .map(e -> Arrays.stream(e.getValue().split("\\r?\\n"))
                        .map(v -> e.getKey() + "," + v + "\n")
                        .collect(Collectors.joining()))
                .collect(Collectors.joining());
    }

    private static void writeLogToFile(String filePath, String[] sortedLog) {
        String newFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + "_filtered.csv";
        Utils.writeDataLineByLine(newFilePath, StringUtils.join(Arrays.asList(sortedLog), "\n"));
    }

    private static boolean containsRedundantActions (String log){
        return ReadSimplifier.containsRedundantCopy(log) ||
                ReadSimplifier.containsSingleCopy(log) ||
                NavigationSimplifier.containsRedundantClickTextField(log) ||
                WriteSimplifier.containsRedundantDoublePaste(log) ||
                WriteSimplifier.containsRedundantEditCell(log) ||
                WriteSimplifier.containsRedundantEditField(log) ||
                WriteSimplifier.containsRedundantPasteIntoCell(log) ||
                WriteSimplifier.containsRedundantPasteIntoRange(log) ||
                WriteSimplifier.containsRedundantDoubleEditField(log);
    }

    private static String applySimplifier(String log) {
        try {
            Validator.validateForIdOrName(log);

            String sortedLog = PreProcessing.sortLog(log);
            sortedLog = PreProcessing.deleteChromeClipboardCopy(sortedLog);
            sortedLog = PreProcessing.mergeNavigationCellCopy(sortedLog);
            sortedLog = PreProcessing.identifyPasteAction(sortedLog);

            while (containsRedundantActions(sortedLog)) {
                sortedLog = ReadSimplifier.removeRedundantCopy(sortedLog);
                sortedLog = ReadSimplifier.removeSingleCopy(sortedLog);
                sortedLog = NavigationSimplifier.removeRedundantClickTextField(sortedLog);
                sortedLog = WriteSimplifier.removeRedundantDoublePaste(sortedLog);
                sortedLog = WriteSimplifier.removeRedundantEditCell(sortedLog);
                sortedLog = WriteSimplifier.removeRedundantEditField(sortedLog);
                sortedLog = WriteSimplifier.removeRedundantPasteIntoCell(sortedLog);
                sortedLog = WriteSimplifier.removeRedundantPasteIntoRange(sortedLog);
                sortedLog = WriteSimplifier.removeRedundantDoubleEditField(sortedLog);
            }

            return sortedLog;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return log;
    }
}
