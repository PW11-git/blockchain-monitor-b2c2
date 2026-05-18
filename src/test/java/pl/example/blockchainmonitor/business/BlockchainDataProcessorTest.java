package pl.example.blockchainmonitor.business;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.example.blockchainmonitor.model.BlockInfo;
import pl.example.blockchainmonitor.model.BlockchainStatistics;
import pl.example.blockchainmonitor.model.FilterCriteria;
import pl.example.blockchainmonitor.model.TransactionInfo;
import pl.example.blockchainmonitor.reporting.ConsoleReporter;
import pl.example.blockchainmonitor.reporting.CsvFileReporter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BlockchainDataProcessorTest {

    private final BlockchainDataProcessor processor = new BlockchainDataProcessor();

    @Test
    //Czy filtrowanie działa poprawnie dla: konkretnego adresu minimalnej wartości ETH
    void shouldFilterTransactionsByAddressAndMinimumValue() {
        List<BlockInfo> blocks = createSampleBlocks();

        FilterCriteria criteria =
                new FilterCriteria("0xA1", new BigDecimal("1.50"));

        List<TransactionInfo> result =
                processor.filterTransactions(blocks, criteria);

        assertEquals(1, result.size());
        assertEquals("0xTX2", result.getFirst().getHash());
    }

    @Test
    //Czy brak filtrów powoduje zwrócenie wszystkich transakcji
    void shouldReturnAllTransactionsWhenFilterIsEmpty() {
        List<BlockInfo> blocks = createSampleBlocks();

        FilterCriteria criteria =
                new FilterCriteria("", null);

        List<TransactionInfo> result =
                processor.filterTransactions(blocks, criteria);

        assertEquals(3, result.size());
    }

    @Test
    //Czy poprawnie liczone są statystyki blockchaina
    void shouldCalculateStatisticsCorrectly() {
        List<BlockInfo> blocks = createSampleBlocks();

        BlockchainStatistics statistics =
                processor.calculateStatistics(blocks);

        assertEquals(2, statistics.getProcessedBlocks());
        assertEquals(3, statistics.getProcessedTransactions());
        assertEquals(new BigDecimal("6.50"),
                statistics.getTotalTransferredEth());
        assertEquals(new BigDecimal("2.16666667"),
                statistics.getAverageTransferredEth());
        assertEquals(new BigDecimal("84000"),
                statistics.getTotalGasUsed());
    }

    @Test
    //Czy aplikacja poprawnie obsługuje brak transakcji
    void shouldReturnZeroAverageForNoTransactions() {
        BlockInfo block =
                new BlockInfo(200L, "0xEMPTY", 0,
                        Instant.now(), List.of());

        BlockchainStatistics statistics =
                processor.calculateStatistics(List.of(block));

        assertEquals(BigDecimal.ZERO,
                statistics.getAverageTransferredEth());
    }

    @Test
    //Czy filtrowanie działa także dla adresu odbiorcy (toAddress)
    void shouldFilterTransactionsByReceiverAddress() {
        List<BlockInfo> blocks = createSampleBlocks();

        FilterCriteria criteria =
                new FilterCriteria("0xD1", null);

        List<TransactionInfo> result =
                processor.filterTransactions(blocks, criteria);

        assertEquals(1, result.size());
        assertEquals("0xTX3", result.getFirst().getHash());
    }

    @Test
    //Czy system poprawnie zwraca pustą listę, gdy żadna transakcja nie spełnia warunków
    void shouldReturnEmptyListWhenNoTransactionsMatch() {
        List<BlockInfo> blocks = createSampleBlocks();

        FilterCriteria criteria =
                new FilterCriteria("0xNOTFOUND",
                        new BigDecimal("100"));

        List<TransactionInfo> result =
                processor.filterTransactions(blocks, criteria);

        assertEquals(0, result.size());
    }

    @Test
    //Czy aplikacja poprawnie działa dla pustej listy bloków
    void shouldHandleEmptyBlockList() {
        BlockchainStatistics statistics =
                processor.calculateStatistics(List.of());

        assertEquals(0, statistics.getProcessedBlocks());
        assertEquals(0, statistics.getProcessedTransactions());
        assertEquals(BigDecimal.ZERO,
                statistics.getTotalTransferredEth());
        assertEquals(BigDecimal.ZERO,
                statistics.getAverageTransferredEth());
        assertEquals(BigDecimal.ZERO,
                statistics.getTotalGasUsed());
    }

    @Test
    //Czy transakcja równa minimalnej wartości także przechodzi filtr
    void shouldIncludeTransactionEqualToMinimumValue() {
        List<BlockInfo> blocks = createSampleBlocks();

        FilterCriteria criteria =
                new FilterCriteria("",
                        new BigDecimal("2.50"));

        List<TransactionInfo> result =
                processor.filterTransactions(blocks, criteria);

        assertEquals(2, result.size());
    }

    private List<BlockInfo> createSampleBlocks() {

        TransactionInfo tx1 =
                new TransactionInfo(
                        "0xTX1",
                        "0xA1",
                        "0xB1",
                        new BigDecimal("1.00"),
                        new BigDecimal("21000"),
                        100L
                );

        TransactionInfo tx2 =
                new TransactionInfo(
                        "0xTX2",
                        "0xA1",
                        "0xB2",
                        new BigDecimal("2.50"),
                        new BigDecimal("31000"),
                        100L
                );

        TransactionInfo tx3 =
                new TransactionInfo(
                        "0xTX3",
                        "0xC1",
                        "0xD1",
                        new BigDecimal("3.00"),
                        new BigDecimal("32000"),
                        99L
                );

        BlockInfo block1 =
                new BlockInfo(
                        100L,
                        "0xBLOCK100",
                        2,
                        Instant.now(),
                        List.of(tx1, tx2)
                );

        BlockInfo block2 =
                new BlockInfo(
                        99L,
                        "0xBLOCK99",
                        1,
                        Instant.now(),
                        List.of(tx3)
                );

        return List.of(block1, block2);
    }

    // Testuje, czy raport tekstowy zawiera kompletne i poprawnie sformatowane statystyki sieci.
    @Test
    void buildSummaryReport_containsAllFields() {
        BlockchainStatistics stats = new BlockchainStatistics(
                5,
                10,
                new BigDecimal("12.34"),
                new BigDecimal("1.234"),
                new BigDecimal("42000")
        );

        ConsoleReporter reporter = new ConsoleReporter();

        String report = reporter.buildSummaryReport(stats);

        assertTrue(report.contains("Liczba przetworzonych bloków: 5"));
        assertTrue(report.contains("Liczba przetworzonych transakcji: 10"));
        assertTrue(report.contains("Łączna wartość przesłanego ETH: 12.34"));
        assertTrue(report.contains("Średnia wartość transakcji ETH: 1.234"));
        assertTrue(report.contains("Łączne zużycie gasu: 42000"));
    }

    @Test
    void printProcessedBlocks_printsBlocksAndTransactions() {
        // GIVEN: Przygotowanie danych testowych (blok z jedną transakcją)
        TransactionInfo tx = new TransactionInfo("0xTX1", "0xA", "0xB", new BigDecimal("1"), new BigDecimal("21000"), 1L);
        BlockInfo block = new BlockInfo(1L, "0xBLOCK1", 1, Instant.parse("2020-01-01T00:00:00Z"), List.of(tx));
        ConsoleReporter reporter = new ConsoleReporter();

        // Przechwycenie strumienia System.out, aby móc przetestować to, co trafi na konsolę
        var out = new ByteArrayOutputStream();
        var ps = new PrintStream(out);
        var originalOut = System.out;

        try {
            System.setOut(ps);
            // WHEN: Wywołanie metody drukującej raport
            reporter.printProcessedBlocks(List.of(block));
        } finally {
            // Przywrócenie oryginalnego strumienia konsoli (kluczowe, by nie zepsuć innych testów)
            System.setOut(originalOut);
        }

        // THEN: Weryfikacja, czy w przechwyconym tekście znajdują się oczekiwane dane bloku i transakcji
        String printed = out.toString();
        assertTrue(printed.contains("[BLOCK] number=1"));
        assertTrue(printed.contains("hash=0xBLOCK1"));
        assertTrue(printed.contains("[TX] hash=0xTX1"));
        assertTrue(printed.contains("from=0xA"));
        assertTrue(printed.contains("to=0xB"));
    }

    private static final Path REPORTS_DIR = Paths.get("reports");

    @AfterEach
    void cleanup() throws Exception {
        // Sprawdzenie, czy katalog z raportami w ogóle istnieje
        if (Files.exists(REPORTS_DIR)) {
            // Otwarcie strumienia plików wewnątrz try-with-resources, aby bezpiecznie go zamknąć
            try (var s = Files.list(REPORTS_DIR)) {
                // Usunięcie każdego pliku znajdującego się wewnątrz katalogu
                s.forEach(p -> p.toFile().delete());
            }
            // Usunięcie głównego katalogu po tym, jak został już opróżniony
            Files.deleteIfExists(REPORTS_DIR);
        }
    }

    @Test
    void writeSummaryCsv_createsFileWithExpectedContent() throws Exception {
        // GIVEN: Przygotowanie obiektów raportu oraz przykładowych statystyk blockchaina
        CsvFileReporter reporter = new CsvFileReporter();
        BlockchainStatistics stats = new BlockchainStatistics(
                2,
                3,
                new BigDecimal("6.50"),
                new BigDecimal("2.16666667"),
                new BigDecimal("84000")
        );

        // WHEN: Wywołanie metody generującej plik podsumowania CSV
        reporter.writeSummaryCsv(stats);

        // THEN: Weryfikacja, czy katalog raportów powstał...
        assertTrue(Files.exists(REPORTS_DIR), "reports dir should exist");

        // ...oraz czy w środku znajduje się plik 'summary-*.csv' z poprawną zawartością
        try (var s = Files.list(REPORTS_DIR)) {
            Path file = s.filter(p -> p.getFileName().toString().startsWith("summary-") && p.getFileName().toString().endsWith(".csv"))
                    .findFirst().orElseThrow(() -> new AssertionError("summary csv not created"));

            List<String> lines = Files.readAllLines(file);
            assertFalse(lines.isEmpty());

            // Sprawdzenie poprawności nagłówków kolumn
            assertEquals("processedBlocks,processedTransactions,totalTransferredEth,averageTransferredEth,totalGasUsed", lines.get(0));
            // Sprawdzenie poprawności wpisanych danych liczbowych
            assertEquals("2,3,6.50,2.16666667,84000", lines.get(1));
        }
    }

    @Test
    void writeTransactionsCsv_escapesAndWritesTransactions() throws Exception {
        CsvFileReporter reporter = new CsvFileReporter();

        // GIVEN: Dane zawierające znaki specjalne (przecinki i cudzysłowy), które mogą zepsuć strukturę CSV
        TransactionInfo tx = new TransactionInfo(
                "0xTX,QUOTE\"",
                "from,with,comma",
                "to\"quote",
                new BigDecimal("1.23"),
                new BigDecimal("21000"),
                100L
        );

        BlockInfo block = new BlockInfo(100L, "0xBLOCK,COMMA", 1, Instant.parse("2020-01-01T00:00:00Z"), List.of(tx));

        // WHEN: Wywołanie zapisu danych do pliku CSV
        reporter.writeTransactionsCsv(List.of(block));

        // THEN: Odfiltrowanie i wczytanie nowo utworzonego pliku z raportem
        try (var s = Files.list(REPORTS_DIR)) {
            Path file = s.filter(p -> p.getFileName().toString().startsWith("transactions-") && p.getFileName().toString().endsWith(".csv"))
                    .findFirst().orElseThrow(() -> new AssertionError("transactions csv not created"));

            List<String> lines = Files.readAllLines(file);
            assertFalse(lines.isEmpty());

            // Weryfikacja struktury nagłówków kolumn
            assertEquals("blockNumber,blockHash,txHash,from,to,valueEth,gasUsed,timestamp", lines.get(0));

            String txLine = lines.get(1);

            // Weryfikacja mechanizmu uciekania znaków (escaping) w wygenerowanym wierszu:
            // 1. blockHash zawiera przecinek -> pole musi być w cudzysłowie
            assertTrue(txLine.contains("\"0xBLOCK,COMMA\""));
            // 2. txHash zawiera przecinek i cudzysłów -> pole w cudzysłowie, a wewnętrzny cudzysłów podwojony
            assertTrue(txLine.contains("\"0xTX,QUOTE\"\"\""));
            // 3. adres 'from' zawiera przecinki -> pole musi być w cudzysłowie
            assertTrue(txLine.contains("\"from,with,comma\""));
            // 4. adres 'to' zawiera cudzysłów -> pole w cudzysłowie, a wewnętrzny cudzysłów podwojony
            assertTrue(txLine.contains("\"to\"\"quote\""));
        }
    }
}