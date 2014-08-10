package com.github.davidmoebius.code_cleanup;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private String entryFolder;
    private String xmlHeader;
    private List<String> projectsToCheck;
    private List<String> fileEndingsToCheck;
    private String headerText;
    private List<HeaderText> headerTexts = new ArrayList<HeaderText>();
    private List<ReplaceLine> replaceLines = new ArrayList<ReplaceLine>();
    private List<String> unwantedLines = new ArrayList<String>();

    public String getEntryFolder() {
        return entryFolder;
    }

    public void setEntryFolder(String entryFolder) {
        this.entryFolder = entryFolder;
    }

    public List<String> getProjectsToCheck() {
        return projectsToCheck;
    }

    public void setProjectsToCheck(List<String> projectsToCheck) {
        this.projectsToCheck = projectsToCheck;
    }

    @Override
    public String toString() {
        return "Configuration [entryFolder=" + entryFolder + ", xmlHeader=" + xmlHeader + ", projectsToCheck="
                + projectsToCheck + ", fileEndingsToCheck=" + fileEndingsToCheck + ", headerText=" + headerText
                + ", headerTexts=" + headerTexts + ", replaceLines=" + replaceLines + ", unwantedLines="
                + unwantedLines + "]";
    }

    public List<String> getFileEndingsToCheck() {
        return fileEndingsToCheck;
    }

    public void setFileEndingsToCheck(List<String> fileEndingsToCheck) {
        this.fileEndingsToCheck = fileEndingsToCheck;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
        initializeHeaderTexts();
    }

    private void initializeHeaderTexts() {
        String[] headerLines = this.headerText.split("\n");

        ArrayList<String> javaLines = initializeHeaderTextForJava(headerLines);
        String javaText = getHeaderText(javaLines);
        HeaderText javaHeader = new HeaderText("java", javaText, javaLines);
        headerTexts.add(javaHeader);

        HeaderText groovyHeader = new HeaderText("groovy", javaText, javaLines);
        headerTexts.add(groovyHeader);

        ArrayList<String> xmlLines = initializeHeaderTextForXML(headerLines);
        String xmlText = getHeaderText(xmlLines);
        HeaderText xmlHeader = new HeaderText("xml", xmlText, xmlLines);
        headerTexts.add(xmlHeader);

        ArrayList<String> yamlLines = initializeHeaderTextForYaml(headerLines);
        String yamlText = getHeaderText(yamlLines);
        HeaderText yamlHeader = new HeaderText("yaml", yamlText, yamlLines);
        headerTexts.add(yamlHeader);

        HeaderText yamlHeader2 = new HeaderText("yml", yamlText, yamlLines);
        headerTexts.add(yamlHeader2);
    }

    private ArrayList<String> initializeHeaderTextForJava(String[] headerLines) {

        ArrayList<String> javalHeaderLines = new ArrayList<String>();
        javalHeaderLines.add("/*");
        for (String currentHeaderLine : headerLines) {
            if (currentHeaderLine.length() > 0) {
                javalHeaderLines.add(" * " + currentHeaderLine);
            } else {
                javalHeaderLines.add(" *");
            }
        }
        javalHeaderLines.add(" */");
        return javalHeaderLines;
    }

    private static ArrayList<String> initializeHeaderTextForXML(String[] headerLines) {

        ArrayList<String> xmlHeaderLines = new ArrayList<String>();
        xmlHeaderLines.add("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        xmlHeaderLines.add("<!--");
        for (String currentHeaderLine : headerLines) {
            if (currentHeaderLine.length() > 0) {
                xmlHeaderLines.add("  ~ " + currentHeaderLine);
            } else {
                xmlHeaderLines.add("  ~");
            }
        }
        xmlHeaderLines.add("-->");
        return xmlHeaderLines;
    }

    private static ArrayList<String> initializeHeaderTextForYaml(String[] headerLines) {

        ArrayList<String> yamlHeaderLines = new ArrayList<String>();
        yamlHeaderLines.add("#");
        for (String currentHeaderLine : headerLines) {
            if (currentHeaderLine.length() > 0) {
                yamlHeaderLines.add(" # " + currentHeaderLine);
            } else {
                yamlHeaderLines.add(" #");
            }
        }
        yamlHeaderLines.add("#");
        return yamlHeaderLines;
    }

    private String getHeaderText(ArrayList<String> headers) {
        StringBuilder header = null;
        for (String currentHeaderLine : headers) {
            if (header == null) {
                header = new StringBuilder();
            } else {
                header.append((char) 10);
            }
            header.append(currentHeaderLine);
        }
        header.append((char) 10);

        return header.toString();
    }

    public HeaderText getHeaderTextfor(String fileEnding) {
        for (HeaderText header : headerTexts) {
            if (header.getFileType().equals(fileEnding)) {
                return header;
            }
        }
        throw new RuntimeException("Could not find any header for fileending " + fileEnding);
    }

    public List<ReplaceLine> getReplaceLines() {
        return replaceLines;
    }

    public void setReplaceLines(List<ReplaceLine> replaceLines) {
        this.replaceLines = replaceLines;
    }

    public List<String> getUnwantedLines() {
        return unwantedLines;
    }

    public void setUnwantedLines(List<String> unwantedLines) {
        this.unwantedLines = unwantedLines;
    }

    public String getXmlHeader() {
        return xmlHeader;
    }

    public void setXmlHeader(String xmlHeader) {
        this.xmlHeader = xmlHeader;
    }

}
