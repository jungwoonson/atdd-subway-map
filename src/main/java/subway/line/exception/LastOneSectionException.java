package subway.line.exception;

public class LastOneSectionException extends IllegalStateException {
    private static final String message = "노선에는 구간이 하나 이상 존재해야 합니다.";

    public LastOneSectionException() {
        super(message);
    }
}
