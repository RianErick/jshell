package shell.command;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

    private final String name;
    private final List<String> arguments;
    private final List<String> parts;

    public CommandParser(String input) {
        this.parts = parse(input);
        this.name = parts.get(0);
        this.arguments = parts.size() > 1 ? parts.subList(1, parts.size()) : List.of();
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public List<String> getParts() {
        return parts;
    }

    private List<String> parse(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            } else if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            } else if (c == ' ' && !inSingleQuote && !inDoubleQuote) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }
}

