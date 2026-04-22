package pl.example.blockchainmonitor.model;

import java.math.BigDecimal;

public class BlockchainStatistics {
    private final int processedBlocks;
    private final int processedTransactions;
    private final BigDecimal totalTransferredEth;
    private final BigDecimal averageTransferredEth;
    private final BigDecimal totalGasUsed;

    public BlockchainStatistics(int processedBlocks,
                                int processedTransactions,
                                BigDecimal totalTransferredEth,
                                BigDecimal averageTransferredEth,
                                BigDecimal totalGasUsed) {
        this.processedBlocks = processedBlocks;
        this.processedTransactions = processedTransactions;
        this.totalTransferredEth = totalTransferredEth;
        this.averageTransferredEth = averageTransferredEth;
        this.totalGasUsed = totalGasUsed;
    }

    public int getProcessedBlocks() {
        return processedBlocks;
    }

    public int getProcessedTransactions() {
        return processedTransactions;
    }

    public BigDecimal getTotalTransferredEth() {
        return totalTransferredEth;
    }

    public BigDecimal getAverageTransferredEth() {
        return averageTransferredEth;
    }

    public BigDecimal getTotalGasUsed() {
        return totalGasUsed;
    }
}
