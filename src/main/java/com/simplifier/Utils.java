package com.simplifier;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
                System.out.println(cell[0].codePointAt(0));
                writer.writeNext(cell);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLogsFromFile(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader filereader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                System.out.println(nextLine[0].codePointAt(0));
                stringBuilder.append(String.join(",", nextLine)).append("\n");
            }

            csvReader.close();
            filereader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
