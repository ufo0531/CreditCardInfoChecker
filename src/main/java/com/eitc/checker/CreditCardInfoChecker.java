package com.eitc.checker;

import com.eitc.utils.PropertiesReader;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public class CreditCardInfoChecker {

    private static final Logger LOGGER = Logger.getLogger(CreditCardInfoChecker.class.getName());
    
    private static final String INPUT_DIR = "input";
    private static final String BACKUP_DIR = "input/backup";
    private static final String OUTPUT_DIR = "output";
    
    public static void main(String[] args) {
        watchFiles(INPUT_DIR, OUTPUT_DIR, BACKUP_DIR);
    }

    private static void watchFiles(String inputDir, String outputDir, String backupDir) {
        try {
            Path watchDirPath = getDirectoryPath(inputDir);
            Path exportDirPath = getDirectoryPath(outputDir);
            Path backupDirPath = getDirectoryPath(backupDir);
            WatchService watcher = watchDirPath.getFileSystem().newWatchService();
            watchDirPath.register(watcher, ENTRY_CREATE);
            WatchKey key;
            LOGGER.log(Level.INFO, "請將檔案放至：{0}資料夾", inputDir);
            while (true) {
                key = watcher.take();
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    Kind<?> kind = watchEvent.kind();
                    if (kind == ENTRY_CREATE) {
                        LOGGER.log(Level.INFO, "處理檔案：{0}", watchEvent.context().toString());
                        Path cardNumListFile = ((WatchEvent<Path>) watchEvent).context();
                        checkBinInfo(Paths.get(inputDir, cardNumListFile.toString()), exportDirPath, backupDirPath);
                    }
                }
                
                if (!key.reset()) {
                    break;
                }
            }
        } catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "處理檔案錯誤", ex);
        }
    }
    
    private static void checkBinInfo(Path inputFile, Path outputDir, Path backupDir) throws IOException {
        LocalDateTime today = LocalDateTime.now();
        String apiUrl = PropertiesReader.getInstance().getProperty("binlist.api");
        String exportFileName = inputFile.getFileName().toString() + ".export_" + today.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String backupFileName = inputFile.getFileName().toString() + ".backup_" + today.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Path outputFile = Paths.get(outputDir.toString(), exportFileName);
        Path backupFile = Paths.get(backupDir.toString(), backupFileName);
        Files.createFile(outputFile);
        try(Stream<String> lines = Files.lines(inputFile)){
            lines.forEach((line) -> {
                String data[] = StringUtils.split(line, ",");
                if (data.length == 2) {
                    String id = data[0];
                    String bin = data[1];
                    LOGGER.log(Level.INFO, "查詢資料→ID：{0}", id);
                    try {
                        HttpResponse<String> response = Unirest.get(apiUrl + bin).asString();
                        String result = response.getBody();
                        result = id + "," + StringUtils.join(result.split(","), ",", 1, 7) + ",\r\n";
                        Files.write(outputFile, result.replace("\"", "").getBytes(), APPEND);
                    } catch (UnirestException ex) {
                        LOGGER.log(Level.SEVERE, "呼叫api錯誤", ex);
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, "輸出錯誤", ex);
                    }
                } else {
                    LOGGER.log(Level.SEVERE, "檔案內容格式錯誤：{0}", line);
                }
            });
        }
        LOGGER.log(Level.INFO, "匯出檔案完成：{0}", outputFile.toString());
        Files.move(inputFile, backupFile);
    }
    
    private static Path getDirectoryPath(String dirName) throws IOException {
        Path path = Paths.get(dirName);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return path;
    }

}
