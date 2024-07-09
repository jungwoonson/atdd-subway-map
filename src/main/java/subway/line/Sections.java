package subway.line;

import subway.line.exception.AlreadyRegisteredStationException;
import subway.line.exception.NotSameNewUpStationAndExistingDownStationException;
import subway.station.Station;
import subway.station.Stations;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "line", orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    private Sections(Section section) {
        sections.add(section);
    }

    public static Sections from(Section section) {
        return new Sections(section);
    }

    public void add(Section section) {
        if (section.notSameUpStationAndDownStationOf(findLastSection())) {
            throw new NotSameNewUpStationAndExistingDownStationException();
        }
        if (existStation(section.getDownStation())) {
            throw new AlreadyRegisteredStationException();
        }
        sections.add(section);
    }

    private Section findLastSection() {
        return sections.get(sections.size() - 1);
    }

    private boolean existStation(Station downStation) {
        Stations stations = extractStations();
        return stations.existStation(downStation);
    }

    public List<Long> getStationIds() {
        return extractStations().getStationIds();
    }

    private Stations extractStations() {
        Section firstSection = findFirstSection();

        Stations stations = Stations.of(firstSection.getUpStation(), firstSection.getDownStation());

        for (Section section : sections) {
            appendStations(stations, section);
        }

        return stations;
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(Section::isFirst)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private void appendStations(Stations stations, Section section) {
        Station lastStation = stations.lastStation();
        if (lastStation.equals(section.getUpStation())) {
            stations.add(section.getDownStation());
        }
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}
