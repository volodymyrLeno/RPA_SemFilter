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

        System.out.println(StringUtils.join(Arrays.asList(sortedLog), "\n"));

        String newFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + "_filtered.csv";
        Utils.writeDataLineByLine(newFilePath, StringUtils.join(Arrays.asList(sortedLog), "\n"));
    }

    private static String applySimplifier(String logs) {
        try {
            Validator.validateForIdName(logs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sortedLogs = PreProcessing.sortLog(logs);
        System.out.println("SORTED LOG\n");
        System.out.println(sortedLogs);

        sortedLogs = PreProcessing.deleteChromeClipboardCopy(sortedLogs);
        sortedLogs = PreProcessing.mergeNavigationCellCopy(sortedLogs);
        sortedLogs = PreProcessing.deleteLink(sortedLogs);
        sortedLogs = PreProcessing.identifyPasteAction(sortedLogs);
        System.out.println("AFTER PRE PROCESSING\n");
        System.out.println(sortedLogs);

        while (ReadSimplifier.isRedundantCopy(sortedLogs) ||
                ReadSimplifier.isSingleCopy(sortedLogs) ||
                NavigationSimplifier.isRedundantClickTextField(sortedLogs) ||
                WriteSimplifier.isRedundantChromePaste(sortedLogs) ||
                WriteSimplifier.isRedundantEditCell(sortedLogs) ||
                WriteSimplifier.isRedundantChromeEditField(sortedLogs) ||
                WriteSimplifier.isRedundantPasteIntoCell(sortedLogs) ||
                WriteSimplifier.isRedundantPasteIntoRange(sortedLogs)) {

            if (ReadSimplifier.isRedundantCopy(sortedLogs)) {
                sortedLogs = ReadSimplifier.deleteRedundantCopy(sortedLogs);
                System.out.println("\n");
                System.out.println(sortedLogs);
            }

            if (ReadSimplifier.isSingleCopy(sortedLogs)) {
                sortedLogs = ReadSimplifier.deleteSingleCopy(sortedLogs);
                System.out.println("\n");
                System.out.println(sortedLogs);
            }

            if (NavigationSimplifier.isRedundantClickTextField(sortedLogs)) {
                sortedLogs = NavigationSimplifier.deleteRedundantClickTextField(sortedLogs);
                System.out.println("\n");
                System.out.println(sortedLogs);
            }

            if (WriteSimplifier.isRedundantChromePaste(sortedLogs)) {
                sortedLogs = WriteSimplifier.deleteRedundantChromePaste(sortedLogs);
                System.out.println("\n");
                System.out.println(sortedLogs);
            }

            if (WriteSimplifier.isRedundantEditCell(sortedLogs)) {
                sortedLogs = WriteSimplifier.deleteRedundantEditCell(sortedLogs);
                System.out.println("\n");
                System.out.println(sortedLogs);
            }

            if (WriteSimplifier.isRedundantChromeEditField(sortedLogs)) {
                sortedLogs = WriteSimplifier.deleteRedundantChromeEditField(sortedLogs);
                System.out.println("\n");
                System.out.println(sortedLogs);
            }

            if (WriteSimplifier.isRedundantPasteIntoCell(sortedLogs)) {
                sortedLogs = WriteSimplifier.deleteRedundantPasteIntoCell(sortedLogs);
                System.out.println("\n");
                System.out.println(sortedLogs);
            }

            if (WriteSimplifier.isRedundantPasteIntoRange(sortedLogs)) {
                sortedLogs = WriteSimplifier.deleteRedundantPasteIntoRange(sortedLogs);
                System.out.println("\n");
                System.out.println(sortedLogs);
            }
        }

        return sortedLogs;
    }
}
