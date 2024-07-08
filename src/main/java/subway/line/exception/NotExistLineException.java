package subway.line.exception;

public class NotExistLineException extends RuntimeException {

    private static final String message = "존재하지 않는 노선입니다.";

    public NotExistLineException() {
        super(message);
    }
}
