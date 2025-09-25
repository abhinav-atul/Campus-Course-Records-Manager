package edu.ccrm.io;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Handles creating backups and performing recursive file operations.
 */
public class BackupService {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path BACKUP_DIR = Paths.get("backups");
    
    /**
     * Creates a timestamped backup of the entire data directory.
     */
    public void performBackup() {
        if (!Files.exists(DATA_DIR)) {
            System.out.println("Data directory does not exist. Nothing to back up.");
            return;
        }
        
        // 1. Create a timestamped folder name (e.g., "2025-09-12_16-10-20")
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path targetDir = BACKUP_DIR.resolve(timestamp);

        try {
            Files.createDirectories(targetDir);
            
            // 2. Walk the file tree of the source directory and copy files.
            try (var paths = Files.walk(DATA_DIR)) {
                paths.forEach(sourcePath -> {
                    try {
                        Path destinationPath = targetDir.resolve(DATA_DIR.relativize(sourcePath));
                        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.err.println("Could not copy file: " + sourcePath);
                    }
                });
            }
            System.out.println("Backup successful. Created at: " + targetDir.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Backup failed: " + e.getMessage());
        }
    }
    
    /**
     * Recursively calculates the total size of a directory.
     * The Files.walk() method handles the directory traversal, making the
     * recursive logic clean and functional.
     *
     * @param path The directory to calculate the size of.
     * @return The total size in bytes.
     */
    public long calculateDirectorySize(Path path) {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return 0;
        }
        
        AtomicLong size = new AtomicLong(0);

        try (Stream<Path> walk = Files.walk(path)) {
            walk.filter(Files::isRegularFile)
                .forEach(p -> {
                    try {
                        size.addAndGet(Files.size(p));
                    } catch (IOException e) {
                        System.err.println("Cannot read size of file: " + p);
                    }
                });
        } catch (IOException e) {
            System.err.println("Error walking directory: " + path);
        }

        return size.get();
    }
}