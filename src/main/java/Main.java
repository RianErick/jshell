import enums.Colors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.*;

import static util.InputOutputSystem.*;

public class Main {
    private static File currentDirectory = new File(System.getProperty("user.dir"));
    private static final int TERMINAL_WIDTH = 80;

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

            case "ls":
                listFiles(arguments);
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

    private static void listFiles(List<String> arguments) {
        boolean showHidden = false;
        boolean longFormat = false;
        String targetPath = null;

        for (String arg : arguments) {
            if (arg.startsWith("-")) {
                if (arg.contains("a")) showHidden = true;
                if (arg.contains("l")) longFormat = true;
            } else {
                targetPath = arg;
            }
        }

        File dir = targetPath != null ? new File(currentDirectory, targetPath) : currentDirectory;
        
        if (!dir.exists()) {
            log(Colors.RED, "ls: " + targetPath + ": Não encontrado");
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            log(Colors.RED, "ls: Não foi possível listar o diretório");
            return;
        }

        Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        List<File> fileList = new ArrayList<>();
        for (File file : files) {
            if (!showHidden && file.getName().startsWith(".")) {
                continue;
            }
            fileList.add(file);
        }

        if (longFormat) {
            printLongFormat(fileList);
        } else {
            printColumnsFormat(fileList);
        }
    }

    private static void printLongFormat(List<File> files) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
        long totalSize = 0;

        for (File file : files) {
            totalSize += file.length() / 1024;
        }
        System.out.println("total " + totalSize);

        for (File file : files) {
            StringBuilder line = new StringBuilder();
            
            String perms = getPermissions(file);
            line.append(perms).append(" ");
            
            line.append(String.format("%3d ", 1));
            
            String owner = System.getProperty("user.name");
            line.append(String.format("%-8s %-8s ", owner, owner));
            
            line.append(String.format("%8d ", file.length()));
            
            line.append(sdf.format(new Date(file.lastModified()))).append(" ");
            
            line.append(getColoredFileName(file));
            
            System.out.println(line);
        }
    }

    private static String getPermissions(File file) {
        StringBuilder perms = new StringBuilder();
        
        if (file.isDirectory()) {
            perms.append("d");
        } else if (isSymlink(file)) {
            perms.append("l");
        } else {
            perms.append("-");
        }

        try {
            Set<PosixFilePermission> posixPerms = Files.getPosixFilePermissions(file.toPath());
            
            perms.append(posixPerms.contains(PosixFilePermission.OWNER_READ) ? "r" : "-");
            perms.append(posixPerms.contains(PosixFilePermission.OWNER_WRITE) ? "w" : "-");
            perms.append(posixPerms.contains(PosixFilePermission.OWNER_EXECUTE) ? "x" : "-");
            perms.append(posixPerms.contains(PosixFilePermission.GROUP_READ) ? "r" : "-");
            perms.append(posixPerms.contains(PosixFilePermission.GROUP_WRITE) ? "w" : "-");
            perms.append(posixPerms.contains(PosixFilePermission.GROUP_EXECUTE) ? "x" : "-");
            perms.append(posixPerms.contains(PosixFilePermission.OTHERS_READ) ? "r" : "-");
            perms.append(posixPerms.contains(PosixFilePermission.OTHERS_WRITE) ? "w" : "-");
            perms.append(posixPerms.contains(PosixFilePermission.OTHERS_EXECUTE) ? "x" : "-");
        } catch (IOException e) {
            perms.append(file.canRead() ? "r" : "-");
            perms.append(file.canWrite() ? "w" : "-");
            perms.append(file.canExecute() ? "x" : "-");
            perms.append("------");
        }
        
        return perms.toString();
    }

    private static void printColumnsFormat(List<File> files) {
        if (files.isEmpty()) return;

        int maxLen = 0;
        for (File file : files) {
            maxLen = Math.max(maxLen, file.getName().length());
        }
        maxLen += 2;

        int columns = Math.max(1, TERMINAL_WIDTH / maxLen);
        int count = 0;

        for (File file : files) {
            String coloredName = getColoredFileName(file);
            int padding = maxLen - file.getName().length();
            
            System.out.print(coloredName);
            
            count++;
            if (count % columns == 0) {
                System.out.println();
            } else {
                System.out.print(" ".repeat(padding));
            }
        }
        
        if (count % columns != 0) {
            System.out.println();
        }
    }

    private static String getColoredFileName(File file) {
        String name = file.getName();
        String reset = Colors.RESET.getCodigo();
        
        if (file.isDirectory()) {
            return Colors.BLUE.getCodigo() + "\033[1m" + name + "/" + reset;
        }
        
        if (isSymlink(file)) {
            return Colors.CYAN.getCodigo() + name + reset;
        }
        
        if (file.canExecute() && file.isFile()) {
            return Colors.GREEN.getCodigo() + "\033[1m" + name + "*" + reset;
        }
        
        if (name.endsWith(".tar") || name.endsWith(".gz") || name.endsWith(".zip") || 
            name.endsWith(".rar") || name.endsWith(".7z") || name.endsWith(".bz2")) {
            return Colors.RED.getCodigo() + name + reset;
        }
        
        if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || 
            name.endsWith(".gif") || name.endsWith(".bmp") || name.endsWith(".svg")) {
            return Colors.PURPLE.getCodigo() + name + reset;
        }
        
        if (name.endsWith(".java") || name.endsWith(".py") || name.endsWith(".js") ||
            name.endsWith(".c") || name.endsWith(".cpp") || name.endsWith(".rs")) {
            return Colors.YELLOW.getCodigo() + name + reset;
        }
        
        return name;
    }

    private static boolean isSymlink(File file) {
        try {
            return Files.isSymbolicLink(file.toPath());
        } catch (Exception e) {
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
        log(Colors.WHITE, "║  ls [-la]   - Lista arquivos             ║");
        log(Colors.WHITE, "║  cd [dir]   - Muda de diretório          ║");
        log(Colors.WHITE, "║  pwd        - Mostra diretório atual     ║");
        log(Colors.WHITE, "║  history    - Mostra histórico           ║");
        log(Colors.WHITE, "║  clear      - Limpa a tela               ║");
        log(Colors.WHITE, "║  help       - Mostra esta ajuda          ║");
        log(Colors.WHITE, "║  exit       - Sai do shell               ║");
        log(Colors.CYAN, "╚══════════════════════════════════════════╝");
    }
}
