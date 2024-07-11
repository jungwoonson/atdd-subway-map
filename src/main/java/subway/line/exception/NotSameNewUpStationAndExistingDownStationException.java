package subway.line.exception;

public class NotSameNewUpStationAndExistingDownStationException extends IllegalArgumentException {

    private static final String message = "새로운 구간의 상행역과 기존 구간의 하행역이 일치해야합니다.";

    public NotSameNewUpStationAndExistingDownStationException() {
        super(message);
    }
}
