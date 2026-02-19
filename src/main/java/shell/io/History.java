package shell.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class History {

    private static final String HISTORY_FILE = "hist.txt";
    private static final int MAX_LINES = 20;
    private static final int DISPLAY_LIMIT = 10;

    private final File file;

    public History() throws IOException {
        this.file = new File(HISTORY_FILE);

        if (!file.exists() && !file.createNewFile()) {
            Terminal.error("Failed to create history file");
        }
    }

    public void save(String command) {
        try {
            trimIfNeeded();

            try (FileOutputStream fos = new FileOutputStream(file, true)) {
                fos.write((command + "\n").getBytes());
            }
        } catch (Exception e) {
            Terminal.error("Failed to save command: " + e.getMessage());
        }
    }

    public void show() throws IOException {
        if (!file.exists() || !file.canRead()) {
            Terminal.error("Cannot read history file: " + file.getAbsolutePath());
            return;
        }

        List<String> lines = Files.readAllLines(file.toPath());
        int start = Math.max(0, lines.size() - DISPLAY_LIMIT);

        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.equals("clear") && !line.equals("history")) {
                System.out.printf("  %d  %s%n", i + 1, line);
            }
        }
    }

    private void trimIfNeeded() throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());

        if (lines.size() > MAX_LINES) {
            List<String> recent = lines.subList(lines.size() - 15, lines.size());
            Files.write(file.toPath(), recent, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}

