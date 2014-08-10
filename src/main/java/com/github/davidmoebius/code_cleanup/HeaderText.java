package com.github.davidmoebius.code_cleanup;

import java.util.ArrayList;

public class HeaderText {

    private String fileType;
    private String headerText;
    private ArrayList<String> headerArrayList;

    public HeaderText(String fileType, String headerText, ArrayList<String> headerArrayList) {
        this.fileType = fileType;
        this.headerText = headerText;
        this.headerArrayList = headerArrayList;
    }

    public String getFileType() {
        return fileType;
    }

    public String getHeaderText() {
        return headerText;
    }

    public ArrayList<String> getHeaderArrayList() {
        return headerArrayList;
    }

}
