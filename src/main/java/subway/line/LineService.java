package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.line.exception.NotExistLineException;
import subway.station.Station;
import subway.station.StationRepository;
import subway.station.StationResponse;
import subway.station.exception.NotExistStationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private LineRepository lineRepository;

    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
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

    public LineResponse findLine(Long id) {
        return createLineResponse(findLineBy(id));
    }

    @Transactional
    public LineResponse modifyLine(Long id, LineRequest lineRequest) {
        Line line = findLineBy(id);
        line.modify(lineRequest.getName(), lineRequest.getColor());
        return createLineResponse(lineRepository.save(line));
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse registerSections(Long id, SectionRequest sectionRequest) {
        Station upStation = findStationBy(sectionRequest.getUpStationId());
        Station downStation = findStationBy(sectionRequest.getDownStationId());
        Line line = findLineBy(id);
        line.registerSection(upStation, downStation, sectionRequest.getDistance());
        return createLineResponse(lineRepository.save(line));
    }

    private Line findLineBy(Long id) {
        return lineRepository.findOneById(id)
                .orElseThrow(NotExistLineException::new);
    }

    @Transactional
    public LineResponse deleteSection(Long lineId, Long stationId) {
        Line line = findLineBy(lineId);
        line.deleteSection(stationId);
        return createLineResponse(lineRepository.save(line));
    }

    private Line createLine(LineRequest lineRequest) {
        return Line.builder()
                .name(lineRequest.getName())
                .color(lineRequest.getColor())
                .upStation(findStationBy(lineRequest.getUpStationId()))
                .downStation(findStationBy(lineRequest.getDownStationId()))
                .distance(lineRequest.getDistance())
                .build();
    }

    private LineResponse createLineResponse(Line line) {
        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .stations(createStationResponses(line.getStationIds()))
                .build();
    }

    private List<StationResponse> createStationResponses(List<Long> stationIds) {
        return stationIds.stream()
                .map(this::createStation)
                .collect(Collectors.toList());
    }

    private StationResponse createStation(Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(NotExistStationException::new);
        return new StationResponse(stationId, station.getName());
    }

    private Station findStationBy(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(NotExistStationException::new);
    }
}
