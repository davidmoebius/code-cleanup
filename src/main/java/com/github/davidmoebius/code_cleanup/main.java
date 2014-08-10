package com.github.davidmoebius.code_cleanup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.yaml.snakeyaml.Yaml;

/**
 * class that will add missing header and removes some unwanted lines. Before pushing the changed code check any single
 * file to be sure that nothing was destroyed
 * 
 */
public class main {

    private static Configuration CONFIG;
    private static int COUNTER = 0;
    private static ArrayList<String> TODOS = new ArrayList<String>();
    private static ArrayList<String> FIXMES = new ArrayList<String>();
    private static ArrayList<String> DEPRECATEDS = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        readConfigFile();
        File file = new File(CONFIG.getEntryFolder());
        for (File currentFolder : file.listFiles()) {
            for (String currentProject : CONFIG.getProjectsToCheck()) {
                if (currentFolder.getAbsolutePath().endsWith(currentProject)) {
                    if (currentFolder.isDirectory()) {
                        replaceUnwantedLines(currentFolder);
                        removeUnwantedLines(currentFolder);
                        removeEmptyComments(currentFolder);
                        addHeaderToXMLFiles(currentFolder);
                        addHeaderToFiles(currentFolder);
                        removeDoubleLines(currentFolder);
                        ensureXmlHeaderLineExistsOnlyOneTime(currentFolder);
                        countTodoAndFixmes(currentFolder);
                    }
                }
            }
        }

        System.out.println("Checked Projects: " + CONFIG.getProjectsToCheck());
        System.out.println("Number of changes: " + COUNTER);
        System.out.println("Number of TODO's: " + TODOS.size());
        if (!TODOS.isEmpty()) {
            System.out.println("TODO's found in: " + TODOS);
        }
        System.out.println("Number of FIXME's: " + FIXMES.size());
        if (!FIXMES.isEmpty()) {
            System.out.println("FIXME's found in: " + FIXMES);
        }
        System.out.println("Number of @Deprecateds: " + DEPRECATEDS.size());
        if (!DEPRECATEDS.isEmpty()) {
            System.out.println("@Deprecateds found in: " + DEPRECATEDS);
        }

    }

    private static void readConfigFile() throws FileNotFoundException {
        File f = new File("config.yaml");
        InputStream in2 = new FileInputStream(f);

        Yaml yaml = new Yaml();
        CONFIG = yaml.loadAs(in2, Configuration.class);
    }

    private static void replaceUnwantedLines(File directory) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                replaceUnwantedLines(file);
            } else {
                for (String currentEnding : CONFIG.getFileEndingsToCheck()) {
                    if (file.getAbsolutePath().endsWith(currentEnding) && !ignoreFile(file)) {
                        FileReader a = new FileReader(file);
                        BufferedReader br = new BufferedReader(a);
                        String line;
                        line = br.readLine();

                        boolean foundInFile = false;
                        StringBuilder newFileContent = null;
                        while (line != null) {
                            boolean found = false;
                            for (ReplaceLine replaceLine : CONFIG.getReplaceLines()) {
                                if (line.equals(replaceLine.getOldLine())) {
                                    found = true;
                                    foundInFile = true;
                                    if (newFileContent == null) {
                                        newFileContent = new StringBuilder();
                                    } else {
                                        newFileContent.append((char) 10);
                                    }
                                    newFileContent.append(replaceLine.getNewLine());
                                    break;
                                }
                            }
                            if (!found) {
                                if (newFileContent == null) {
                                    newFileContent = new StringBuilder();
                                } else {
                                    newFileContent.append((char) 10);
                                }
                                newFileContent.append(line);
                            }
                            line = br.readLine();
                        }

                        br.close();

                        if (foundInFile) {
                            PrintWriter out = new PrintWriter(file);
                            out.print(newFileContent.toString());
                            out.close();

                            System.out.println("file: " + file.getAbsolutePath() + " modified by replacing lines");
                            COUNTER++;
                        }
                    }
                }
            }
        }
    }

    private static void ensureXmlHeaderLineExistsOnlyOneTime(File directory) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                ensureXmlHeaderLineExistsOnlyOneTime(file);
            } else {
                if (file.getAbsolutePath().endsWith(".xml") && !ignoreFile(file)) {
                    FileReader a = new FileReader(file);
                    BufferedReader br = new BufferedReader(a);
                    String line;
                    line = br.readLine();

                    int found = 0;
                    boolean doubleHeaderLine;
                    StringBuilder newFileContent = null;
                    while (line != null) {
                        doubleHeaderLine = false;
                        if (line.equals(CONFIG.getXmlHeader())) {
                            found++;
                            if (found > 1) {
                                doubleHeaderLine = true;
                            }
                        }

                        if (!doubleHeaderLine) {
                            if (newFileContent == null) {
                                newFileContent = new StringBuilder();
                            } else {
                                newFileContent.append((char) 10);
                            }
                            newFileContent.append(line);
                        }
                        line = br.readLine();
                    }
                    br.close();

                    if (found > 1) {
                        PrintWriter out = new PrintWriter(file);
                        out.print(newFileContent.toString());
                        out.close();

                        System.out.println("file: " + file.getAbsolutePath()
                                + " modified by removing double XML header lines");
                        COUNTER++;
                    }
                }
            }
        }
    }

    private static void removeUnwantedLines(File directory) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                removeUnwantedLines(file);
            } else {
                for (String currentEnding : CONFIG.getFileEndingsToCheck()) {
                    if (file.getAbsolutePath().endsWith(currentEnding) && !ignoreFile(file)) {
                        FileReader a = new FileReader(file);
                        BufferedReader br = new BufferedReader(a);
                        String line;
                        line = br.readLine();

                        boolean foundInFile = false;
                        StringBuilder newFileContent = null;
                        while (line != null) {
                            boolean found = false;
                            for (String nonWantedLine : CONFIG.getUnwantedLines()) {
                                if (line.startsWith(nonWantedLine)) {
                                    found = true;
                                    foundInFile = true;
                                    break;
                                }
                            }
                            if (!found) {
                                if (newFileContent == null) {
                                    newFileContent = new StringBuilder();
                                } else {
                                    newFileContent.append((char) 10);
                                }
                                newFileContent.append(line);
                            }
                            line = br.readLine();
                        }

                        br.close();

                        if (foundInFile) {
                            PrintWriter out = new PrintWriter(file);
                            out.print(newFileContent.toString());
                            out.close();

                            System.out.println("file: " + file.getAbsolutePath() + " modified by removing lines");
                            COUNTER++;
                        }
                    }
                }
            }
        }
    }

    private static void removeDoubleLines(File directory) throws IOException {
        String emptyLine = "" + (char) 10 + (char) 10;
        String doubleEmptyLine = "" + (char) 10 + (char) 10 + (char) 10;
        removeDoubleLines(directory, emptyLine, doubleEmptyLine);
    }

    private static void removeDoubleLines(File directory, String singleLine, String doubleLine) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                removeDoubleLines(file, singleLine, doubleLine);
            } else {
                for (String currentEnding : CONFIG.getFileEndingsToCheck()) {
                    if (file.getAbsolutePath().endsWith(currentEnding) && !ignoreFile(file)) {
                        FileInputStream fis = new FileInputStream(file);
                        byte[] data = new byte[(int) file.length()];
                        fis.read(data);
                        fis.close();

                        String fileContent = new String(data, "UTF-8");

                        boolean foundInFile = false;
                        while (fileContent.contains(doubleLine)) {
                            foundInFile = true;
                            fileContent = fileContent.replace(doubleLine, singleLine);
                        }

                        if (foundInFile) {
                            PrintWriter out = new PrintWriter(file);
                            out.print(fileContent);
                            out.close();

                            System.out.println("file: " + file.getAbsolutePath()
                                    + " modified by removing double empty lines");
                            COUNTER++;
                        }
                    }
                }
            }
        }
    }

    private static void removeEmptyComments(File directory) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                removeEmptyComments(file);
            } else {
                for (String currentEnding : CONFIG.getFileEndingsToCheck()) {
                    if (file.getAbsolutePath().endsWith(currentEnding) && !ignoreFile(file)) {

                        FileInputStream fis = new FileInputStream(file);
                        byte[] data = new byte[(int) file.length()];
                        fis.read(data);
                        fis.close();

                        String fileContent = new String(data, "UTF-8");

                        boolean foundInFile = false;
                        String emptyComment = "/**" + (char) 10 + " */";
                        while (fileContent.contains(emptyComment)) {
                            foundInFile = true;
                            fileContent = fileContent.replace(emptyComment, "");
                        }

                        if (foundInFile) {
                            PrintWriter out = new PrintWriter(file);
                            out.print(fileContent);
                            out.close();

                            System.out.println("file: " + file.getAbsolutePath()
                                    + " modified by removing double empty comment");
                            COUNTER++;
                        }
                    }
                }
            }
        }
    }

    private static void addHeaderToXMLFiles(File directory) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                addHeaderToXMLFiles(file);
            } else {
                if (file.getAbsolutePath().endsWith(".xml") && !ignoreFile(file)) {
                    String header = CONFIG.getXmlHeader();
                    FileInputStream fis = new FileInputStream(file);
                    byte[] data = new byte[(int) file.length()];
                    fis.read(data);
                    fis.close();

                    String fileContent = new String(data, "UTF-8");
                    if (!fileContent.startsWith(header)) {
                        PrintWriter out = new PrintWriter(file);
                        out.print(header + (char) 10 + fileContent);

                        out.close();

                        System.out.println("file: " + file.getAbsolutePath() + " modified by adding a xml header");
                        COUNTER++;
                    }
                }
            }
        }
    }

    private static void addHeaderToFiles(File directory) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                addHeaderToFiles(file);
            } else {
                for (String currentEnding : CONFIG.getFileEndingsToCheck()) {
                    if (file.getAbsolutePath().endsWith(currentEnding) && !ignoreFile(file)) {
                        String fileEnding = getFileEnding(file);
                        String header = CONFIG.getHeaderTextfor(fileEnding).getHeaderText();
                        ArrayList<String> headerLines = CONFIG.getHeaderTextfor(fileEnding).getHeaderArrayList();

                        FileInputStream fis = new FileInputStream(file);
                        byte[] data = new byte[(int) file.length()];
                        fis.read(data);
                        fis.close();

                        String fileContent = new String(data, "UTF-8");
                        if (!containsFileHeader(fileContent, headerLines)) {
                            PrintWriter out = new PrintWriter(file);
                            out.print(header + (char) 10 + fileContent);

                            out.close();

                            System.out.println("file: " + file.getAbsolutePath() + " modified by adding a header");
                            COUNTER++;
                        }
                    }
                }
            }
        }
    }

    private static String getFileEnding(File file) {
        String[] tokens = file.getAbsolutePath().split("\\.(?=[^\\.]+$)");
        return tokens[1];
    }

    private static void countTodoAndFixmes(File directory) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                countTodoAndFixmes(file);
            } else {
                for (String currentEnding : CONFIG.getFileEndingsToCheck()) {
                    if (file.getAbsolutePath().endsWith(currentEnding) && !ignoreFile(file)) {
                        FileInputStream fis = new FileInputStream(file);
                        byte[] data = new byte[(int) file.length()];
                        fis.read(data);
                        fis.close();

                        String fileContent = new String(data, "UTF-8");

                        int index = fileContent.indexOf("TODO");
                        while (index > 0) {
                            TODOS.add(file.getAbsolutePath());
                            index = fileContent.indexOf("TODO", index + 3);
                        }
                        index = fileContent.indexOf("FIXME");
                        while (index > 0) {
                            FIXMES.add(file.getAbsolutePath());
                            index = fileContent.indexOf("FIXME", index + 3);
                        }
                        index = fileContent.indexOf("@Deprecated");
                        while (index > 0) {
                            DEPRECATEDS.add(file.getAbsolutePath());
                            index = fileContent.indexOf("@Deprecated", index + 3);
                        }
                    }
                }
            }
        }
    }

    private static boolean containsFileHeader(String file, ArrayList<String> headerLines) {
        for (String currentHeaderLine : headerLines) {
            if (!file.contains(currentHeaderLine)) {
                return false;
            }
        }
        return true;
    }

    private static boolean ignoreFile(File file) {
        String completePath = file.getAbsolutePath();
        if (completePath.contains("/target/")
                || completePath.contains("/resources/")
                || completePath.contains(".settings/")) {
            return true;
        }
        return false;
    }

}
