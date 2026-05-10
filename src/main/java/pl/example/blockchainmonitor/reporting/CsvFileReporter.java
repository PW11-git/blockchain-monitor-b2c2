package pl.example.blockchainmonitor.reporting;

import pl.example.blockchainmonitor.model.BlockInfo;
import pl.example.blockchainmonitor.model.BlockchainStatistics;
import pl.example.blockchainmonitor.model.TransactionInfo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvFileReporter {

    private static final DateTimeFormatter FILENAME_FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final Path REPORTS_DIR = Paths.get("reports");

    public void writeSummaryCsv(BlockchainStatistics stats) {
        try {
            if (!Files.exists(REPORTS_DIR)) {
                Files.createDirectories(REPORTS_DIR);
            }
            String timestamp = LocalDateTime.now().format(FILENAME_FMT);
            Path file = REPORTS_DIR.resolve("summary-" + timestamp + ".csv");
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardOpenOption.CREATE_NEW)) {
                w.write("processedBlocks,processedTransactions,totalTransferredEth,averageTransferredEth,totalGasUsed");
                w.newLine();
                w.write(String.format("%d,%d,%s,%s,%s",
                        stats.getProcessedBlocks(),
                        stats.getProcessedTransactions(),
                        stats.getTotalTransferredEth().toPlainString(),
                        stats.getAverageTransferredEth().toPlainString(),
                        stats.getTotalGasUsed().toPlainString()));
                w.newLine();
            }
            System.out.println("Zapisano raport pod: " + file.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Błąd zapisu pliku CSV (summary): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void writeTransactionsCsv(List<BlockInfo> blocks) {
        try {
            if (!Files.exists(REPORTS_DIR)) {
                Files.createDirectories(REPORTS_DIR);
            }
            String timestamp = LocalDateTime.now().format(FILENAME_FMT);
            Path file = REPORTS_DIR.resolve("transactions-" + timestamp + ".csv");
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardOpenOption.CREATE_NEW)) {
                w.write("blockNumber,blockHash,txHash,from,to,valueEth,gasUsed,timestamp");
                w.newLine();
                for (BlockInfo block : blocks) {
                    String blockHash = block.getHash();
                    long blockNumber = block.getNumber();
                    String ts = block.getTimestamp().toString();
                    for (TransactionInfo tx : block.getTransactions()) {
                        w.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                                blockNumber,
                                escapeCsv(blockHash),
                                escapeCsv(tx.getHash()),
                                escapeCsv(tx.getFromAddress()),
                                escapeCsv(tx.getToAddress()),
                                tx.getValueInEth().toPlainString(),
                                tx.getGasUsed().toPlainString(),
                                ts));
                        w.newLine();
                    }
                }
            }
            System.out.println("Zapisano raport pod: " + file.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Błąd zapisu pliku CSV (transactions): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
