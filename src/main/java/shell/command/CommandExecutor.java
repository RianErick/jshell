package shell.command;

import shell.io.Terminal;
import shell.util.AnsiColor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CommandExecutor {

    public static void execute(List<String> commandParts, File workingDirectory) {
        try {
            ProcessBuilder pb = new ProcessBuilder(commandParts);
            pb.directory(workingDirectory);
            pb.environment().putAll(System.getenv());

            Process process = pb.start();

            new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines()
                    .forEach(Terminal::info);

            new BufferedReader(new InputStreamReader(process.getErrorStream()))
                    .lines()
                    .forEach(Terminal::error);

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                Terminal.warning("[Process exited with code: " + exitCode + "]");
            }

        } catch (IOException e) {
            Terminal.error("Command not found: " + commandParts.get(0));
        } catch (InterruptedException e) {
            Terminal.error("Process interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}

