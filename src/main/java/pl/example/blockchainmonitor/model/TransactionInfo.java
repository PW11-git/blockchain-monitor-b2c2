package pl.example.blockchainmonitor.model;

import java.math.BigDecimal;

public class TransactionInfo {
    private final String hash;
    private final String fromAddress;
    private final String toAddress;
    private final BigDecimal valueInEth;
    private final BigDecimal gasUsed;
    private final long blockNumber;

    public TransactionInfo(String hash,
                           String fromAddress,
                           String toAddress,
                           BigDecimal valueInEth,
                           BigDecimal gasUsed,
                           long blockNumber) {
        this.hash = hash;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.valueInEth = valueInEth;
        this.gasUsed = gasUsed;
        this.blockNumber = blockNumber;
    }

    public String getHash() {
        return hash;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public BigDecimal getValueInEth() {
        return valueInEth;
    }

    public BigDecimal getGasUsed() {
        return gasUsed;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    @Override
    public String toString() {
        return "TransactionInfo{" +
                "hash='" + hash + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", valueInEth=" + valueInEth +
                ", gasUsed=" + gasUsed +
                ", blockNumber=" + blockNumber +
                '}';
    }
}
