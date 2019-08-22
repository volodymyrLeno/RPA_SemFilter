package com.simplifier;

import com.simplifier.pre_processing.PreProcessing;
import com.simplifier.rules.navigation.NavigationSimplifier;
import com.simplifier.rules.read.ReadSimplifier;
import com.simplifier.rules.write.WriteSimplifier;
import com.simplifier.validation.Validator;

public class Main {

    public static void main(String[] args) throws Exception {
        String filePath = args[0];
        Utils utils = new Utils();
        String logs = utils.readLogsFromFile(filePath);

        /*
         *  Validate log if there is action without id or name
         */
        Validator.validateForIdName(logs);

        /*
         *  Sort log before filtering
         */
        String sortedLogs = PreProcessing.sortLog(logs);
        System.out.println("SORTED LOG\n");
        System.out.println(sortedLogs);

        sortedLogs = PreProcessing.deleteChromeClipboardCopy(sortedLogs);
        sortedLogs = PreProcessing.mergeNavigationCellCopy(sortedLogs);
        sortedLogs = PreProcessing.deleteLink(sortedLogs);
        sortedLogs = PreProcessing.identifyPasteAction(sortedLogs);
        System.out.println("AFTER PRE PROCESSING\n");
        System.out.println(sortedLogs);

        // Add delete single copy

        while (ReadSimplifier.isRedundantCopy(sortedLogs) ||
                ReadSimplifier.isSingleCopy(sortedLogs) ||
                NavigationSimplifier.isRedundantClickTextField(sortedLogs) ||
                WriteSimplifier.isRedundantChromePaste(sortedLogs) ||
                WriteSimplifier.isRedundantEditCell(sortedLogs) ||
                WriteSimplifier.isRedundantChromeEditField(sortedLogs) ||
                WriteSimplifier.isRedundantPasteIntoCell(sortedLogs)) {

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
        }

        String newFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + "_filtered.csv";
        Utils.writeDataLineByLine(newFilePath, sortedLogs);
    }
}
