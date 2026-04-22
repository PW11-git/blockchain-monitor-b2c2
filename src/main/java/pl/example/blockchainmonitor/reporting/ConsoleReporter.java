package pl.example.blockchainmonitor.reporting;

import pl.example.blockchainmonitor.model.BlockInfo;
import pl.example.blockchainmonitor.model.BlockchainStatistics;
import pl.example.blockchainmonitor.model.TransactionInfo;

import java.util.List;

public class ConsoleReporter {

    public void printProcessedBlocks(List<BlockInfo> blocks) {
        for (BlockInfo block : blocks) {
            System.out.println("[BLOCK] number=" + block.getNumber()
                    + ", hash=" + block.getHash()
                    + ", txCount=" + block.getTransactionCount()
                    + ", timestamp=" + block.getTimestamp());

            for (TransactionInfo tx : block.getTransactions()) {
                System.out.println("  [TX] hash=" + tx.getHash()
                        + ", from=" + tx.getFromAddress()
                        + ", to=" + tx.getToAddress()
                        + ", valueEth=" + tx.getValueInEth()
                        + ", gasUsed=" + tx.getGasUsed());
            }
        }
    }

    public String buildSummaryReport(BlockchainStatistics statistics) {
        return """
                ==============================
                RAPORT KOŃCOWY MONITORA
                ==============================
                Liczba przetworzonych bloków: %d
                Liczba przetworzonych transakcji: %d
                Łączna wartość przesłanego ETH: %s
                Średnia wartość transakcji ETH: %s
                Łączne zużycie gasu: %s
                ==============================
                """.formatted(
                statistics.getProcessedBlocks(),
                statistics.getProcessedTransactions(),
                statistics.getTotalTransferredEth().toPlainString(),
                statistics.getAverageTransferredEth().toPlainString(),
                statistics.getTotalGasUsed().toPlainString()
        );
    }

    public void printSummary(BlockchainStatistics statistics) {
        System.out.println(buildSummaryReport(statistics));
    }
}
