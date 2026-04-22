package pl.example.blockchainmonitor.access;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import pl.example.blockchainmonitor.exception.BlockchainAccessException;
import pl.example.blockchainmonitor.model.BlockInfo;
import pl.example.blockchainmonitor.model.TransactionInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Web3jSepoliaClient implements BlockchainClient {

    private final Web3j web3j;
    private final long delayMs;

    public Web3jSepoliaClient(String rpcUrl, long delayMs) {
        this.web3j = Web3j.build(new HttpService(rpcUrl));
        this.delayMs = delayMs;
    }

    @Override
    public List<BlockInfo> fetchLatestBlocks(int blockCount, int detailBlockCount) {
        try {
            BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            List<BlockInfo> result = new ArrayList<>();

            for (int i = 0; i < blockCount; i++) {
                BigInteger blockNumber = latestBlockNumber.subtract(BigInteger.valueOf(i));
                boolean includeTransactions = i < detailBlockCount;

                EthBlock ethBlock = web3j.ethGetBlockByNumber(
                        new DefaultBlockParameterNumber(blockNumber),
                        includeTransactions
                ).send();

                result.add(mapBlock(ethBlock, includeTransactions));
                applyDelay();
            }

            return result;
        } catch (IOException e) {
            throw new BlockchainAccessException("Błąd podczas pobierania danych z Sepolii.", e);
        }
    }

    private BlockInfo mapBlock(EthBlock ethBlockResponse, boolean includeTransactions) throws IOException {
        EthBlock.Block block = ethBlockResponse.getBlock();
        List<TransactionInfo> transactions = includeTransactions
                ? mapTransactions(block)
                : Collections.emptyList();

        return new BlockInfo(
                block.getNumber().longValue(),
                block.getHash(),
                block.getTransactions().size(),
                Instant.ofEpochSecond(block.getTimestamp().longValue()),
                transactions
        );
    }

    private List<TransactionInfo> mapTransactions(EthBlock.Block block) throws IOException {
        List<TransactionInfo> items = new ArrayList<>();

        for (EthBlock.TransactionResult<?> txResult : block.getTransactions()) {
            EthBlock.TransactionObject tx = (EthBlock.TransactionObject) txResult.get();

            BigDecimal valueEth = Convert.fromWei(new BigDecimal(tx.getValue()), Convert.Unit.ETHER);
            BigDecimal gasUsed = fetchGasUsed(tx.getHash());

            items.add(new TransactionInfo(
                    tx.getHash(),
                    tx.getFrom(),
                    tx.getTo(),
                    valueEth,
                    gasUsed,
                    tx.getBlockNumber().longValue()
            ));
        }

        return items;
    }

    private BigDecimal fetchGasUsed(String transactionHash) throws IOException {
        EthGetTransactionReceipt receiptResponse = web3j.ethGetTransactionReceipt(transactionHash).send();
        return receiptResponse.getTransactionReceipt()
                .map(receipt -> new BigDecimal(receipt.getGasUsed()))
                .orElse(BigDecimal.ZERO);
    }

    private void applyDelay() {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BlockchainAccessException("Przerwano opóźnienie związane z rate limiting.", e);
        }
    }
}
