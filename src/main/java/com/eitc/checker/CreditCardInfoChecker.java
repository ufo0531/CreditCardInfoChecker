package com.eitc.checker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class CreditCardInfoChecker {

    private static final Logger LOGGER = Logger.getLogger(CreditCardInfoChecker.class.getName());
    
    private static final String INPUT_DIR = "input";
    private static final String OUTPUT_DIR = "output";
    
    public static void main(String[] args) {
        watchFiles(INPUT_DIR, OUTPUT_DIR);
    }

    private static void watchFiles(String inputDir, String outputDir) {
        try {
            Path watchDir = Paths.get(inputDir);
            if (watchDir == null) {
                if(watchDir == null) {
                    throw new UnsupportedOperationException("Directory not found:" + inputDir);
                }
            }
            WatchService watcher = watchDir.getFileSystem().newWatchService();
            watchDir.register(watcher, ENTRY_CREATE);
            WatchKey key = null;
            while (true) {
                key = watcher.take();
                
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    Kind<?> kind = watchEvent.kind();
                    if (kind == ENTRY_CREATE) {
                        LOGGER.log(Level.INFO, "新增檔案：{0}", watchEvent.context().toString());
                        Path cardNumListFile = ((WatchEvent<Path>) watchEvent).context();
                        checkInfo(Paths.get(inputDir, cardNumListFile.toString()));
                    }
                }
                
                if (!key.reset()) {
                    break;
                }
            }
        } catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private static void checkInfo(Path input) throws IOException {
        try(Stream<String> lines = Files.lines(input)){
            lines.forEach((line) -> {
                LOGGER.info(line);
            });
        }
    }

}
