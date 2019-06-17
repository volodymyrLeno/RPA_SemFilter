package com.simplifier;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.*;

public class Utils {

    public String readLogsFromFile(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                stringBuilder.append(String.join(",", nextLine)).append("\n");
            }

            csvReader.close();
            filereader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public static void writeDataLineByLine(String filePath, String data) {
        File file = new File(filePath);
        try {
            FileWriter outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);

            String[] header = {"timeStamp", "userID	", "targetApp", "eventType", "url", "content", "target.workbookName	",
                    "target.sheetName",	"target.id", "target.class", "target.tagName", "target.type	", "target.name",
                    "target.value", "target.innerText",	"target.checked", "target.href", "target.option"
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
}
