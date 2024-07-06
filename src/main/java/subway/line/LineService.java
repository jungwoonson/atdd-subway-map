package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse lineSave(LineRequest lineRequest) {
        Line line = lineRepository.save(createLine(lineRequest));
        return createLineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    private Line createLine(LineRequest lineRequest) {
        return new Line.Builder()
                .name(lineRequest.getName())
                .color(lineRequest.getColor())
                .upStationId(lineRequest.getUpStationId())
                .downStationId(lineRequest.getDownStationId())
                .distance(lineRequest.getDistance())
                .build();
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse.Builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .stations(createStations(line.getUpStationId(), line.getDownStationId()))
                .build();
    }

    private List<StationResponse> createStations(Long upStationId, Long downStationId) {
        return List.of(
                createStation(upStationId),
                createStation(downStationId)
        );
    }

    private StationResponse createStation(Long stationId) {
        return new StationResponse(stationId, findStationNameById(stationId));
    }

    private String findStationNameById(Long stationId) {
        switch (stationId.intValue()) {
            case 1:
                return "지하철역";
            case 2:
                return "새로운지하철역";
            default:
                return "또다른지하철역";
        }
    }
}
