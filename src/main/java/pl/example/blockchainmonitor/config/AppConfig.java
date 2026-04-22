package pl.example.blockchainmonitor.config;

public final class AppConfig {
    private AppConfig() {
    }

    public static final String SEPOLIA_RPC_URL = "https://eth-sepolia.g.alchemy.com/v2/jmCDROBDPm2u4etATc6sJ";

    public static final int BLOCKS_TO_FETCH = 5;
    public static final int BLOCKS_FOR_TRANSACTION_DETAILS = 2;
    public static final long RATE_LIMIT_DELAY_MS = 250L;
    public static final String MONITORED_ADDRESS = "";
    public static final double MIN_ETH_VALUE = 0.0;
}
