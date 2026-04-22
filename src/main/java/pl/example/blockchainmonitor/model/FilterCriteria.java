package pl.example.blockchainmonitor.model;

import java.math.BigDecimal;

public class FilterCriteria {
    private final String address;
    private final BigDecimal minValueInEth;

    public FilterCriteria(String address, BigDecimal minValueInEth) {
        this.address = address;
        this.minValueInEth = minValueInEth;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getMinValueInEth() {
        return minValueInEth;
    }
}
