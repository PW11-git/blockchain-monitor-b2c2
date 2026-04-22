package pl.example.blockchainmonitor.business;

import pl.example.blockchainmonitor.model.BlockInfo;
import pl.example.blockchainmonitor.model.BlockchainStatistics;
import pl.example.blockchainmonitor.model.FilterCriteria;
import pl.example.blockchainmonitor.model.TransactionInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockchainDataProcessor {

    public List<TransactionInfo> filterTransactions(List<BlockInfo> blocks, FilterCriteria criteria) {
        List<TransactionInfo> result = new ArrayList<>();

        for (BlockInfo block : blocks) {
            for (TransactionInfo tx : block.getTransactions()) {
                if (matchesAddress(tx, criteria.getAddress()) && matchesMinValue(tx, criteria.getMinValueInEth())) {
                    result.add(tx);
                }
            }
        }

        return result;
    }

    public BlockchainStatistics calculateStatistics(List<BlockInfo> blocks) {
        int totalBlocks = blocks.size();
        int totalTransactions = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalGas = BigDecimal.ZERO;

        for (BlockInfo block : blocks) {
            totalTransactions += block.getTransactionCount();

            for (TransactionInfo tx : block.getTransactions()) {
                totalValue = totalValue.add(tx.getValueInEth());
                totalGas = totalGas.add(tx.getGasUsed());
            }
        }

        BigDecimal averageValue = totalTransactions == 0
                ? BigDecimal.ZERO
                : totalValue.divide(BigDecimal.valueOf(totalTransactions), 8, RoundingMode.HALF_UP);

        return new BlockchainStatistics(
                totalBlocks,
                totalTransactions,
                totalValue,
                averageValue,
                totalGas
        );
    }

    private boolean matchesAddress(TransactionInfo transaction, String address) {
        if (address == null || address.isBlank()) {
            return true;
        }

        return Objects.equals(address, transaction.getFromAddress())
                || Objects.equals(address, transaction.getToAddress());
    }

    private boolean matchesMinValue(TransactionInfo transaction, BigDecimal minValue) {
        if (minValue == null) {
            return true;
        }

        return transaction.getValueInEth().compareTo(minValue) >= 0;
    }
}
