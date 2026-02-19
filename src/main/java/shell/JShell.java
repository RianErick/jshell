package shell;

import shell.command.BuiltinCommands;
import shell.command.CommandExecutor;
import shell.command.CommandParser;
import shell.io.History;
import shell.io.Terminal;

import java.io.IOException;
import java.util.Scanner;

public class JShell {

    public static void main(String[] args) throws IOException {
        History history = new History();
        BuiltinCommands builtins = new BuiltinCommands(history);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            Terminal.printPrompt(builtins.getCurrentDirectory());
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            history.save(input);

            CommandParser cmd = new CommandParser(input);

            if (builtins.handle(cmd.getName(), cmd.getArguments())) {
                continue;
            }

            CommandExecutor.execute(cmd.getParts(), builtins.getCurrentDirectory());
        }
    }
}

