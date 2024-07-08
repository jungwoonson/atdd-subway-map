package subway.line;

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

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        if (section.isUpStationAndDownStationOf(findLastSection())) {
            sections.add(section);
            return;
        }
        throw new NotSameNewUpStationAndExistingDownStationException();
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

    private static void appendStations(Stations stations, Section section) {
        Station lastStation = stations.lastStation();
        if (lastStation.getId().equals(section.getUpStation().getId())) {
            stations.add(section.getDownStation());
        }
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(Section::isFirst)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Section findLastSection() {
        return sections.get(sections.size() - 1);
    }
}
