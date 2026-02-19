package shell.util;

public enum AnsiColor {
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    BOLD("\u001B[1m"),
    RESET("\u001B[0m");

    private final String code;

    AnsiColor(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static String colorize(AnsiColor color, String text) {
        return color.getCode() + text + RESET.getCode();
    }

    public static String bold(AnsiColor color, String text) {
        return BOLD.getCode() + color.getCode() + text + RESET.getCode();
    }
}

