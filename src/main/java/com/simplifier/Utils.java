package com.simplifier;

import com.opencsv.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static void writeDataLineByLine(String filePath, String data) {
        File file = new File(filePath);
        try {
            FileOutputStream os = new FileOutputStream(file);
            os.write(0xef);
            os.write(0xbb);
            os.write(0xbf);
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(os));

            String[] header = {"timeStamp", "userID	", "targetApp", "eventType", "url", "content", "target.workbookName	",
                    "target.sheetName", "target.id", "target.class", "target.tagName", "target.type	", "target.name",
                    "target.value", "target.innerText", "target.checked", "target.href", "target.option"
            };
            writer.writeNext(header);

            String[] dataArray = data.split("\n");
            for (String row : dataArray) {
                String[] cell = row.split(",");
                writer.writeNext(cell);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, StringBuilder> readLogsFromFile(String filePath) {
        Map<String, StringBuilder> cases = new HashMap<>();

        try {
            RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().build();
            BufferedReader filereader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .withCSVParser(rfc4180Parser)
                    .build();

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                if (cases.get(nextLine[0]) == null) {
                    cases.put(nextLine[0], new StringBuilder());
                }
                cases.get(nextLine[0]).append(String.join(",", nextLine)).append("\n");
            }

            csvReader.close();
            filereader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cases;
    }
}
