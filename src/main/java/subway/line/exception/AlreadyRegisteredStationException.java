package subway.line.exception;

public class AlreadyRegisteredStationException extends IllegalArgumentException {

    private static final String message = "해당 노선에 등록되지 않은 하행역을 등록해 주세요.";

    public AlreadyRegisteredStationException() {
        super(message);
    }
}
