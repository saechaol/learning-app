package com.saechaol.learningapp.model;

/**
 * An Object which encapsulates a String representing the textual representation of a user list
 */
public class ListModel {

    private String textView;

    public ListModel(String textView) {
        this.textView = textView;
    }

    public String getTextView() {
        return textView;
    }

}