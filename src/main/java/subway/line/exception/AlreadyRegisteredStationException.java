package subway.line.exception;

public class AlreadyRegisteredStationException extends IllegalArgumentException {

    private static final String message = "해당 노선에 등록되지 않은 지하철 역을 하행역으로 등록해 주세요.";

    public AlreadyRegisteredStationException() {
        super(message);
    }
}
