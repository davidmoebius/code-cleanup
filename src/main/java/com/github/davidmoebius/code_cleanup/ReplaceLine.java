package com.github.davidmoebius.code_cleanup;

public class ReplaceLine {

    private String oldLine;
    private String newLine;

    public String getOldLine() {
        return oldLine;
    }

    public void setOldLine(String oldLine) {
        this.oldLine = oldLine;
    }

    public String getNewLine() {
        return newLine;
    }

    public void setNewLine(String newLine) {
        this.newLine = newLine;
    }

    @Override
    public String toString() {
        return "ReplaceLine [oldLine=" + oldLine + ", newLine=" + newLine + "]";
    }

}
