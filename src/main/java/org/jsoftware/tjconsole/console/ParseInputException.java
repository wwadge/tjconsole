package org.jsoftware.tjconsole.console;

public class ParseInputException extends Exception {
    private final String input;

    public ParseInputException(String message, String input) {
        super(message);
        this.input = input;
    }

    public String getInput() {
        return input;
    }
}
