package shell.command;

import shell.io.History;
import shell.io.Terminal;
import shell.util.AnsiColor;
import shell.util.FileFormatter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BuiltinCommands {

    private File currentDirectory;
    private File previousDirectory;
    private final History history;

    public BuiltinCommands(History history) {
        this.currentDirectory = new File(System.getProperty("user.dir"));
        this.previousDirectory = currentDirectory;
        this.history = history;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public boolean handle(String command, List<String> arguments) throws IOException {
        switch (command) {
            case "exit":
                return doExit();
            case "cd":
                return doCd(arguments);
            case "pwd":
                return doPwd();
            case "ls":
                return doLs(arguments);
            case "history":
                return doHistory();
            case "clear":
                return doClear();
            case "help":
                return doHelp();
            default:
                return false;
        }
    }

    private boolean doExit() {
        Terminal.log(AnsiColor.YELLOW, "Leaving jshell... Goodbye!");
        System.exit(0);
        return true;
    }

    private boolean doCd(List<String> arguments) {
        String target;

        if (arguments.isEmpty() || arguments.get(0).equals("~")) {
            target = System.getProperty("user.home");
        } else if (arguments.get(0).equals("-")) {
            File temp = currentDirectory;
            currentDirectory = previousDirectory;
            previousDirectory = temp;
            Terminal.info(currentDirectory.getAbsolutePath());
            return true;
        } else if (arguments.get(0).equals("..")) {
            File parent = currentDirectory.getParentFile();
            if (parent != null) {
                previousDirectory = currentDirectory;
                currentDirectory = parent;
            } else {
                Terminal.error("cd: Already at root directory");
            }
            return true;
        } else {
            target = arguments.get(0);
        }

        File newDir;
        if (target.startsWith("/")) {
            newDir = new File(target);
        } else if (target.startsWith("~")) {
            newDir = new File(System.getProperty("user.home") + target.substring(1));
        } else {
            newDir = new File(currentDirectory, target);
        }

        if (newDir.exists() && newDir.isDirectory()) {
            try {
                previousDirectory = currentDirectory;
                currentDirectory = newDir.getCanonicalFile();
            } catch (IOException e) {
                Terminal.error("cd: Failed to access directory: " + e.getMessage());
            }
        } else if (!newDir.exists()) {
            Terminal.error("cd: " + target + ": No such directory");
        } else {
            Terminal.error("cd: " + target + ": Not a directory");
        }

        return true;
    }

    private boolean doPwd() {
        Terminal.log(AnsiColor.BLUE, currentDirectory.getAbsolutePath());
        return true;
    }

    private boolean doLs(List<String> arguments) {
        FileFormatter.list(currentDirectory, arguments);
        return true;
    }

    private boolean doHistory() throws IOException {
        history.show();
        return true;
    }

    private boolean doClear() {
        Terminal.clear();
        return true;
    }

    private boolean doHelp() {
        Terminal.log(AnsiColor.CYAN, "╔══════════════════════════════════════════╗");
        Terminal.log(AnsiColor.CYAN, "║         jshell - Builtin Commands        ║");
        Terminal.log(AnsiColor.CYAN, "╠══════════════════════════════════════════╣");
        Terminal.info("║  ls [-la]   - List files                 ║");
        Terminal.info("║  cd [dir]   - Change directory            ║");
        Terminal.info("║  cd -       - Go to previous directory    ║");
        Terminal.info("║  pwd        - Print working directory     ║");
        Terminal.info("║  history    - Show command history        ║");
        Terminal.info("║  clear      - Clear the screen            ║");
        Terminal.info("║  help       - Show this help              ║");
        Terminal.info("║  exit       - Exit the shell              ║");
        Terminal.log(AnsiColor.CYAN, "╚══════════════════════════════════════════╝");
        return true;
    }
}

