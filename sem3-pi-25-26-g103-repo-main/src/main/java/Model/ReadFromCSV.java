package Model;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Utility to read CSV files from the train_station_dataset folder and map rows into domain objects.
 */
public class ReadFromCSV {
    private static final String parsingErrorPath = "outputFiles/parsingErrors.log";

    private static volatile boolean firstErrorPrinted = false;

    public static boolean isFirstErrorPrinted() {
        return firstErrorPrinted;
    }

    /**
     * Reads a CSV file from the train_station_dataset directory and maps each non-header line to an object.
     * Lines are split by ';' or ',' and empty lines are ignored. Mapping errors are logged to stderr.
     *
     * @param fileName file name without extension (e.g., "stations")
     * @param mapper function that converts a String[] of columns into an object of type T
     * @param <T> target type to produce from each CSV row
     * @return list of mapped objects
     * @throws RuntimeException if the file cannot be read
     */
    public static <T> List<T> readFile(String fileName, Function<String[], T> mapper) {
        List<T> result = new ArrayList<>();

        Path csvPath = resolveDatasetPath(fileName + ".csv");

        try (BufferedReader br = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                if (!line.trim().isEmpty()) {
                    try {
                        String[] values;

                        if (line.contains("\"")) {
                            values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                        } else {
                            values = line.split("[;,]", -1);
                        }

                        for (int i = 0; i < values.length; i++) {
                            values[i] = values[i].trim();
                            if (values[i].startsWith("\"") && values[i].endsWith("\"")) {
                                values[i] = values[i].substring(1, values[i].length() - 1);
                            }
                        }

                        T obj = mapper.apply(values);
                        if (obj != null) {
                            result.add(obj);
                        }
                    } catch (Exception ex) {
                        String message = "Warning: error processing line " + lineNumber + " in file '" + csvPath.getFileName() + "' (" + ex.getClass().getSimpleName() + "): " + ex.getMessage();

                        if (!firstErrorPrinted) {
                            System.err.println("Error in parsing check file " + parsingErrorPath + " to see the list of errors.");
                            firstErrorPrinted = true;
                        }
                            errorsLog(message);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + csvPath.toAbsolutePath(), e);
        }

        return result;
    }

    private static Path resolveDatasetPath(String fileNameWithExtension) {
        // Try current working directory first
        Path base = Paths.get("").toAbsolutePath();
        Path candidate = base.resolve("train_station_dataset").resolve(fileNameWithExtension);
        if (Files.exists(candidate)) return candidate;

        // Try walking up to 5 parent directories
        Path current = base;
        for (int i = 0; i < 5 && current != null; i++) {
            current = current.getParent();
            if (current == null) break;
            candidate = current.resolve("train_station_dataset").resolve(fileNameWithExtension);
            if (Files.exists(candidate)) return candidate;
        }

        // As a last attempt, try relative (in case caller uses a different CWD semantics)
        candidate = Paths.get("train_station_dataset").resolve(fileNameWithExtension);
        if (Files.exists(candidate)) return candidate.toAbsolutePath();

        throw new RuntimeException("Could not locate dataset file '" + fileNameWithExtension + "' starting from '" + base + "'. Expected under a 'train_station_dataset' folder.");
    }

    private static void errorsLog(String message) {
        try (FileWriter fw = new FileWriter(parsingErrorPath, true)) {
            fw.write(message + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Failed to write to parsing error log: " + e.getMessage());
        }
    }
}