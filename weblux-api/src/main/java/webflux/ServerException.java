package webflux;

public class ServerException extends RuntimeException {

    public ServerException(String message) {
        super(message);
    }
}
