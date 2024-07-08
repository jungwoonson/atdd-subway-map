package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.station.Station;
import subway.station.StationRepository;
import subway.station.StationResponse;

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
        return createLineResponse(lineRepository.findOneById(id));
    }

    @Transactional
    public LineResponse modifyLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findOneById(id);
        line.modify(lineRequest.getName(), lineRequest.getColor());
        return createLineResponse(lineRepository.save(line));
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse addSections(Long id, SectionRequest sectionRequest) {
        Line line = lineRepository.findOneById(id);
        line.addSection(createSection(line, sectionRequest));
        return createLineResponse(lineRepository.save(line));
    }

    private Line createLine(LineRequest lineRequest) {
        Line line = new Line.Builder()
                .name(lineRequest.getName())
                .color(lineRequest.getColor())
                .build();

        Section section = createSection(line, lineRequest);

        line.addSection(section);

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
                .orElseThrow(IllegalArgumentException::new);
        return new StationResponse(stationId, station.getName());
    }

    private Section createSection(Line line, LineRequest lineRequest) {
        return Section.builder()
                .line(line)
                .upStation(lineRequest.getUpStationId())
                .downStation(lineRequest.getDownStationId())
                .distance(lineRequest.getDistance())
                .isFirst(true)
                .build();
    }

    private Section createSection(Line line, SectionRequest sectionRequest) {
        return Section.builder()
                .line(line)
                .upStation(sectionRequest.getUpStationId())
                .downStation(sectionRequest.getDownStationId())
                .distance(sectionRequest.getDistance())
                .isFirst(false)
                .build();
    }
}
