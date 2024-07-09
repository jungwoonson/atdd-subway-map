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
        Line line = findLineBy(id);
        line.registerSection(createSection(line, sectionRequest));
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
        Line line = new Line.Builder()
                .name(lineRequest.getName())
                .color(lineRequest.getColor())
                .build();

        Section section = createFirstSection(line, lineRequest);

        line.registerSection(section);

        return line;
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse.Builder()
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

    private Section createFirstSection(Line line, LineRequest lineRequest) {
        return Section.builder()
                .line(line)
                .upStation(findStationBy(lineRequest.getUpStationId()))
                .downStation(findStationBy(lineRequest.getDownStationId()))
                .distance(lineRequest.getDistance())
                .isFirst(true)
                .build();
    }

    private Section createSection(Line line, SectionRequest sectionRequest) {
        return Section.builder()
                .line(line)
                .upStation(findStationBy(sectionRequest.getUpStationId()))
                .downStation(findStationBy(sectionRequest.getDownStationId()))
                .distance(sectionRequest.getDistance())
                .isFirst(false)
                .build();
    }

    private Station findStationBy(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(NotExistStationException::new);
    }
}
