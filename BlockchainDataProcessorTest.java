package pl.example.blockchainmonitor.business;

import org.junit.jupiter.api.Test;
import pl.example.blockchainmonitor.model.BlockInfo;
import pl.example.blockchainmonitor.model.BlockchainStatistics;
import pl.example.blockchainmonitor.model.FilterCriteria;
import pl.example.blockchainmonitor.model.TransactionInfo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}