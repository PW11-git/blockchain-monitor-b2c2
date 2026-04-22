package pl.example.blockchainmonitor.model;

import java.time.Instant;
import java.util.List;

public class BlockInfo {
    private final long number;
    private final String hash;
    private final int transactionCount;
    private final Instant timestamp;
    private final List<TransactionInfo> transactions;

    public BlockInfo(long number, String hash, int transactionCount, Instant timestamp, List<TransactionInfo> transactions) {
        this.number = number;
        this.hash = hash;
        this.transactionCount = transactionCount;
        this.timestamp = timestamp;
        this.transactions = transactions;
    }

    public long getNumber() {
        return number;
    }

    public String getHash() {
        return hash;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public List<TransactionInfo> getTransactions() {
        return transactions;
    }

    @Override
    public String toString() {
        return "BlockInfo{" +
                "number=" + number +
                ", hash='" + hash + '\'' +
                ", transactionCount=" + transactionCount +
                ", timestamp=" + timestamp +
                '}';
    }
}
