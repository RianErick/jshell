package shell.io;

import shell.util.AnsiColor;

import java.io.File;

public class Terminal {

    public static void printPrompt(File currentDirectory) {
        String dir = currentDirectory.getAbsolutePath();
        String home = System.getProperty("user.home");

        if (dir.startsWith(home)) {
            dir = "~" + dir.substring(home.length());
        }

        System.out.print(AnsiColor.colorize(AnsiColor.CYAN, dir));
        System.out.print(AnsiColor.colorize(AnsiColor.GREEN, " jshell> "));
    }

    public static void log(AnsiColor color, String message) {
        System.out.println(AnsiColor.colorize(color, message));
    }

    public static void info(String message) {
        System.out.println(message);
    }

    public static void error(String message) {
        log(AnsiColor.RED, message);
    }

    public static void warning(String message) {
        log(AnsiColor.YELLOW, message);
    }

    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

