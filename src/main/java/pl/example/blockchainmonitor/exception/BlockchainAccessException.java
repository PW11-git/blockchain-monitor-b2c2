package pl.example.blockchainmonitor.exception;

public class BlockchainAccessException extends RuntimeException {
    public BlockchainAccessException(String message) {
        super(message);
    }

    public BlockchainAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
