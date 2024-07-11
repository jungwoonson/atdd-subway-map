package subway.station.exception;

public class NotExistStationException extends IllegalStateException {

    private static final String message = "존재하지 않는 지하철 역입니다.";

    public NotExistStationException() {
        super(message);
    }
}