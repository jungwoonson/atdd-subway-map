package subway.line.exception;

public class NotDownStationException extends IllegalArgumentException {

    private static final String message = "해당 노선의 하행역 구간만 제거할 수 있습니다.";

    public NotDownStationException() {
        super(message);
    }
}
