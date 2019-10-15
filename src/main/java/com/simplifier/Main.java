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
        Map<String, StringBuilder> cases = utils.readLogsFromFile(filePath);
        Map<String, String> result = cases.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, logCase -> {
                    String logs = logCase.getValue().toString();
                    return applySimplifier(logs);
                }));
        String writableLog = result.entrySet().stream()
                .map(e -> Arrays.stream(e.getValue().split("\\r?\\n"))
                        .map(v -> e.getKey() + "," + v + "\n")
                        .collect(Collectors.joining()))
                .collect(Collectors.joining());

        String[] sortedLog = writableLog.split("\\r?\\n");
        Arrays.sort(sortedLog, Comparator.comparing(s -> s.substring(2)));

        System.out.println("Result\n");
        System.out.println(StringUtils.join(Arrays.asList(sortedLog), "\n"));

        String newFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + "_filtered.csv";
        Utils.writeDataLineByLine(newFilePath, StringUtils.join(Arrays.asList(sortedLog), "\n"));
    }

    private static String applySimplifier(String log) {
        try {
            Validator.validateForIdName(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sortedLog = PreProcessing.sortLog(log);
        System.out.println("SORTED LOG\n");
        System.out.println(sortedLog);

        sortedLog = PreProcessing.deleteChromeClipboardCopy(sortedLog);
        sortedLog = PreProcessing.mergeNavigationCellCopy(sortedLog);
        sortedLog = PreProcessing.identifyPasteAction(sortedLog);
        System.out.println("AFTER PRE PROCESSING\n");
        System.out.println(sortedLog);

        while (ReadSimplifier.containsRedundantCopy(sortedLog) ||
                ReadSimplifier.containsSingleCopy(sortedLog) ||
                NavigationSimplifier.containsRedundantClickTextField(sortedLog) ||
                WriteSimplifier.containsRedundantChromePaste(sortedLog) ||
                WriteSimplifier.containsRedundantEditCell(sortedLog) ||
                WriteSimplifier.isRedundantChromeEditField(sortedLog) ||
                WriteSimplifier.containsRedundantPasteIntoCell(sortedLog) ||
                WriteSimplifier.isRedundantPasteIntoRange(sortedLog)) {

            if (ReadSimplifier.containsRedundantCopy(sortedLog)) {
                sortedLog = ReadSimplifier.removeRedundantCopy(sortedLog);
                System.out.println("After ReadSimplifier.containsRedundantCopy\n");
                System.out.println(sortedLog);
            }

            if (ReadSimplifier.containsSingleCopy(sortedLog)) {
                sortedLog = ReadSimplifier.removeSingleCopy(sortedLog);
                System.out.println("After ReadSimplifier.removeSingleCopy\n");
                System.out.println(sortedLog);
            }

            if (NavigationSimplifier.containsRedundantClickTextField(sortedLog)) {
                sortedLog = NavigationSimplifier.removeRedundantClickTextField(sortedLog);
                System.out.println("After NavigationSimplifier.removeRedundantClickTextField\n");
                System.out.println(sortedLog);
            }

            if (WriteSimplifier.containsRedundantChromePaste(sortedLog)) {
                sortedLog = WriteSimplifier.deleteRedundantChromePaste(sortedLog);
                System.out.println("After WriteSimplifier.deleteRedundantChromePaste\n");
                System.out.println(sortedLog);
            }

            if (WriteSimplifier.containsRedundantEditCell(sortedLog)) {
                sortedLog = WriteSimplifier.removeRedundantEditCell(sortedLog);
                System.out.println("After WriteSimplifier.deleteRedundantEditCell\n");
                System.out.println(sortedLog);
            }

            if (WriteSimplifier.isRedundantChromeEditField(sortedLog)) {
                sortedLog = WriteSimplifier.deleteRedundantChromeEditField(sortedLog);
                System.out.println("After WriteSimplifier.deleteRedundantChromeEditField\n");
                System.out.println(sortedLog);
            }

            if (WriteSimplifier.containsRedundantPasteIntoCell(sortedLog)) {
                sortedLog = WriteSimplifier.removeRedundantPasteIntoCell(sortedLog);
                System.out.println("After WriteSimplifier.deleteRedundantPasteIntoCell\n");
                System.out.println(sortedLog);
            }

            if (WriteSimplifier.isRedundantPasteIntoRange(sortedLog)) {
                sortedLog = WriteSimplifier.deleteRedundantPasteIntoRange(sortedLog);
                System.out.println("After WriteSimplifier.deleteRedundantPasteIntoRange\n");
                System.out.println(sortedLog);
            }
        }

        return sortedLog;
    }
}
