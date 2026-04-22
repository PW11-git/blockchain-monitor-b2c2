package pl.example.blockchainmonitor.access;

import pl.example.blockchainmonitor.model.BlockInfo;

import java.util.List;

public interface BlockchainClient {
    List<BlockInfo> fetchLatestBlocks(int blockCount, int detailBlockCount);
}
