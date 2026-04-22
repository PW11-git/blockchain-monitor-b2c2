package pl.example.blockchainmonitor;

import pl.example.blockchainmonitor.access.BlockchainClient;
import pl.example.blockchainmonitor.access.Web3jSepoliaClient;
import pl.example.blockchainmonitor.business.BlockchainDataProcessor;
import pl.example.blockchainmonitor.config.AppConfig;
import pl.example.blockchainmonitor.model.BlockInfo;
import pl.example.blockchainmonitor.model.BlockchainStatistics;
import pl.example.blockchainmonitor.model.FilterCriteria;
import pl.example.blockchainmonitor.model.TransactionInfo;
import pl.example.blockchainmonitor.reporting.ConsoleReporter;

import java.math.BigDecimal;
import java.util.List;

public class BlockchainMonitorApplication {

    public static void main(String[] args) {
        BlockchainClient client = new Web3jSepoliaClient(
                AppConfig.SEPOLIA_RPC_URL,
                AppConfig.RATE_LIMIT_DELAY_MS
        );

        BlockchainDataProcessor processor = new BlockchainDataProcessor();
        ConsoleReporter reporter = new ConsoleReporter();

        List<BlockInfo> blocks = client.fetchLatestBlocks(
                AppConfig.BLOCKS_TO_FETCH,
                AppConfig.BLOCKS_FOR_TRANSACTION_DETAILS
        );

        reporter.printProcessedBlocks(blocks);

        FilterCriteria criteria = new FilterCriteria(
                AppConfig.MONITORED_ADDRESS,
                BigDecimal.valueOf(AppConfig.MIN_ETH_VALUE)
        );

        List<TransactionInfo> filteredTransactions = processor.filterTransactions(blocks, criteria);
        System.out.println("Liczba transakcji po filtrowaniu: " + filteredTransactions.size());

        BlockchainStatistics statistics = processor.calculateStatistics(blocks);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> reporter.printSummary(statistics)));
    }
}
