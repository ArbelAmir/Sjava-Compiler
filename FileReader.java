package oop.ex6.main;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileReader {

    private static final String SJAVA = ".sjava";
    private static final String READING_ERROR = "Problem happened while reading the file.";
    private static final String WRONG_TYPE = "The file given is not a 'sjava' file";

    /**
     * Converts a sjava file to a List of Strings divided by its lines.
     * @param path a path to the sjava file.
     * @return A list of strings divided by its lines.
     * @throws Exception
     */
    public static List<String> readFile(String path) throws Exception {

        if (!path.trim().endsWith(SJAVA)) {
            throw new Exception(WRONG_TYPE);
        }

        // read file to list
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (Exception c) {
            throw new Exception(READING_ERROR);
        }
    }
}
