package shell.util;

import shell.io.Terminal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileFormatter {

    private static final int TERMINAL_WIDTH = 80;

    public static void list(File directory, List<String> arguments) {
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

        File dir = targetPath != null ? new File(directory, targetPath) : directory;

        if (!dir.exists()) {
            Terminal.error("ls: " + targetPath + ": Not found");
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            Terminal.error("ls: Cannot list directory");
            return;
        }

        Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        List<File> fileList = new ArrayList<>();
        for (File file : files) {
            if (!showHidden && file.getName().startsWith(".")) continue;
            fileList.add(file);
        }

        if (longFormat) {
            printLongFormat(fileList);
        } else {
            printColumns(fileList);
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
            String perms = buildPermissions(file);
            String owner = System.getProperty("user.name");
            String size = String.format("%8d", file.length());
            String date = sdf.format(new Date(file.lastModified()));
            String name = colorize(file);

            System.out.printf("%s %3d %-8s %-8s %s %s %s%n",
                    perms, 1, owner, owner, size, date, name);
        }
    }

    private static String buildPermissions(File file) {
        StringBuilder perms = new StringBuilder();

        if (file.isDirectory()) {
            perms.append("d");
        } else if (Files.isSymbolicLink(file.toPath())) {
            perms.append("l");
        } else {
            perms.append("-");
        }

        try {
            Set<PosixFilePermission> posix = Files.getPosixFilePermissions(file.toPath());

            perms.append(posix.contains(PosixFilePermission.OWNER_READ) ? "r" : "-");
            perms.append(posix.contains(PosixFilePermission.OWNER_WRITE) ? "w" : "-");
            perms.append(posix.contains(PosixFilePermission.OWNER_EXECUTE) ? "x" : "-");
            perms.append(posix.contains(PosixFilePermission.GROUP_READ) ? "r" : "-");
            perms.append(posix.contains(PosixFilePermission.GROUP_WRITE) ? "w" : "-");
            perms.append(posix.contains(PosixFilePermission.GROUP_EXECUTE) ? "x" : "-");
            perms.append(posix.contains(PosixFilePermission.OTHERS_READ) ? "r" : "-");
            perms.append(posix.contains(PosixFilePermission.OTHERS_WRITE) ? "w" : "-");
            perms.append(posix.contains(PosixFilePermission.OTHERS_EXECUTE) ? "x" : "-");
        } catch (IOException e) {
            perms.append(file.canRead() ? "r" : "-");
            perms.append(file.canWrite() ? "w" : "-");
            perms.append(file.canExecute() ? "x" : "-");
            perms.append("------");
        }

        return perms.toString();
    }

    private static void printColumns(List<File> files) {
        if (files.isEmpty()) return;

        int maxLen = 0;
        for (File file : files) {
            int len = file.getName().length() + (file.isDirectory() ? 1 : 0);
            maxLen = Math.max(maxLen, len);
        }
        maxLen += 2;

        int columns = Math.max(1, TERMINAL_WIDTH / maxLen);
        int count = 0;

        for (File file : files) {
            String colored = colorize(file);
            int nameLen = file.getName().length() + (file.isDirectory() ? 1 : 0);
            int padding = maxLen - nameLen;

            System.out.print(colored);
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

    private static String colorize(File file) {
        String name = file.getName();
        String reset = AnsiColor.RESET.getCode();

        if (file.isDirectory()) {
            return AnsiColor.bold(AnsiColor.BLUE, name + "/");
        }

        if (Files.isSymbolicLink(file.toPath())) {
            return AnsiColor.colorize(AnsiColor.CYAN, name);
        }

        if (file.canExecute() && file.isFile()) {
            return AnsiColor.bold(AnsiColor.GREEN, name + "*");
        }

        String lower = name.toLowerCase();

        if (lower.endsWith(".tar") || lower.endsWith(".gz") || lower.endsWith(".zip") ||
                lower.endsWith(".rar") || lower.endsWith(".7z") || lower.endsWith(".bz2")) {
            return AnsiColor.colorize(AnsiColor.RED, name);
        }

        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") ||
                lower.endsWith(".gif") || lower.endsWith(".bmp") || lower.endsWith(".svg")) {
            return AnsiColor.colorize(AnsiColor.PURPLE, name);
        }

        if (lower.endsWith(".java") || lower.endsWith(".py") || lower.endsWith(".js") ||
                lower.endsWith(".c") || lower.endsWith(".cpp") || lower.endsWith(".rs") ||
                lower.endsWith(".go") || lower.endsWith(".ts")) {
            return AnsiColor.colorize(AnsiColor.YELLOW, name);
        }

        return name;
    }
}

