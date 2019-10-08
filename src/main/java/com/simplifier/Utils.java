package com.simplifier;

import com.opencsv.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

    public static void writeDataLineByLine(String filePath, String data) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filePath),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.NO_ESCAPE_CHARACTER,
                    CSVWriter.RFC4180_LINE_END);

            String[] header = {"caseID", "timeStamp", "userID", "targetApp", "eventType", "url", "content", "target.workbookName",
                    "target.sheetName", "target.id", "target.class", "target.tagName", "target.type	", "target.name",
                    "target.value", "target.innerText", "target.checked", "target.href", "target.option", "target.title", "target.innerHTML"
            };
            writer.writeNext(header);

            String[] dataArray = data.split("\n");
            for (String row : dataArray) {
                String[] cell = row.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                writer.writeNext(Arrays.stream(cell)
                        .map(e -> e.replaceAll("\"{2}(((?!,).)*)\"{2}", "\"\"\"$1\"\"\""))
                        .toArray(String[]::new)
                );
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, StringBuilder> readLogsFromFile(String filePath) {
        Map<String, StringBuilder> cases = new HashMap<>();

        try {
            RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().withQuoteChar('\"').build();
            BufferedReader filereader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .withCSVParser(rfc4180Parser)
                    .build();

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String caseId = "\"" + nextLine[0] + "\"";

                if (cases.get(caseId) == null) {
                    cases.put(caseId, new StringBuilder());
                }
                cases.get(caseId).append(Arrays
                        .stream(Arrays.copyOfRange(nextLine, 1, nextLine.length))
                        .map(e -> "\"" + e + "\"")
                        .collect(Collectors.joining(","))
                ).append("\n");
            }

            csvReader.close();
            filereader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cases;
    }
}
