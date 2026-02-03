import enums.Colors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import static util.InputOutputSystem.*;

public class Main {
    private static File currentDirectory = new File(System.getProperty("user.dir"));

    public static void main(String[] args) throws IOException {
        File histFile = new File("hist.txt");

        if (!histFile.exists()) {
            boolean newFile = histFile.createNewFile();
            if (!newFile) {
                log(Colors.RED, "Erro ao criar o arquivo de histórico");
            }
        }

        java.util.Scanner scanner = new java.util.Scanner(System.in);

        while (true) {
            printPrompt();
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            saveCommand(input, histFile);

            List<String> commandParts = Arrays.asList(input.split("\\s+"));
            String command = commandParts.get(0);
            List<String> arguments = commandParts.subList(1, commandParts.size());

            if (handleBuiltinCommand(command, arguments)) {
                continue;
            }

            executeExternalCommand(commandParts);
        }
    }

    private static void printPrompt() {
        String dir = currentDirectory.getAbsolutePath();
        String home = System.getProperty("user.home");
        
        if (dir.startsWith(home)) {
            dir = "~" + dir.substring(home.length());
        }
        
        System.out.print(Colors.CYAN.getCodigo() + dir + Colors.RESET.getCodigo());
        System.out.print(Colors.GREEN.getCodigo() + " rshell> " + Colors.RESET.getCodigo());
    }

    private static boolean handleBuiltinCommand(String command, List<String> arguments) throws IOException {
        switch (command) {
            case "exit":
                log(Colors.YELLOW, "Saindo do rshell... Até mais!");
                System.exit(0);
                return true;

            case "cd":
                changeDirectory(arguments);
                return true;

            case "pwd":
                log(Colors.BLUE, currentDirectory.getAbsolutePath());
                return true;

            case "history":
                findHistory();
                return true;

            case "clear":
                System.out.print("\033[H\033[2J");
                System.out.flush();
                return true;

            case "help":
                printHelp();
                return true;

            default:
                return false;
        }
    }

    private static void changeDirectory(List<String> arguments) {
        String targetPath;

        if (arguments.isEmpty() || arguments.get(0).equals("~")) {
            targetPath = System.getProperty("user.home");
        } else if (arguments.get(0).equals("-")) {
            targetPath = System.getProperty("user.home");
        } else if (arguments.get(0).equals("..")) {
            File parent = currentDirectory.getParentFile();
            if (parent != null) {
                currentDirectory = parent;
            } else {
                log(Colors.RED, "Já está no diretório raiz");
            }
            return;
        } else {
            targetPath = arguments.get(0);
        }

        File newDir;
        if (targetPath.startsWith("/")) {
            newDir = new File(targetPath);
        } else if (targetPath.startsWith("~")) {
            newDir = new File(System.getProperty("user.home") + targetPath.substring(1));
        } else {
            newDir = new File(currentDirectory, targetPath);
        }

        if (newDir.exists() && newDir.isDirectory()) {
            try {
                currentDirectory = newDir.getCanonicalFile();
            } catch (IOException e) {
                log(Colors.RED, "Erro ao acessar diretório: " + e.getMessage());
            }
        } else if (!newDir.exists()) {
            log(Colors.RED, "cd: " + targetPath + ": Diretório não encontrado");
        } else {
            log(Colors.RED, "cd: " + targetPath + ": Não é um diretório");
        }
    }

    private static void executeExternalCommand(List<String> commandParts) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(commandParts);
            processBuilder.directory(currentDirectory);
            processBuilder.environment().putAll(System.getenv());

            Process process = processBuilder.start();

            BufferedReader stdInput = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            stdInput.lines().forEach(line -> log(Colors.WHITE, line));

            BufferedReader stdError = new BufferedReader(
                new InputStreamReader(process.getErrorStream())
            );
            stdError.lines().forEach(err -> log(Colors.RED, err));

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log(Colors.YELLOW, "[Processo terminou com código: " + exitCode + "]");
            }

        } catch (IOException e) {
            log(Colors.RED, "Comando não encontrado: " + commandParts.get(0));
        } catch (InterruptedException e) {
            log(Colors.RED, "Processo interrompido: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private static void printHelp() {
        log(Colors.CYAN, "╔══════════════════════════════════════════╗");
        log(Colors.CYAN, "║         rshell - Comandos Builtin        ║");
        log(Colors.CYAN, "╠══════════════════════════════════════════╣");
        log(Colors.WHITE, "║  cd [dir]   - Muda de diretório          ║");
        log(Colors.WHITE, "║  pwd        - Mostra diretório atual     ║");
        log(Colors.WHITE, "║  history    - Mostra histórico           ║");
        log(Colors.WHITE, "║  clear      - Limpa a tela               ║");
        log(Colors.WHITE, "║  help       - Mostra esta ajuda          ║");
        log(Colors.WHITE, "║  exit       - Sai do shell               ║");
        log(Colors.CYAN, "╚══════════════════════════════════════════╝");
    }
}
