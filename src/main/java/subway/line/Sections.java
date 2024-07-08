package subway.line;

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
        sections.add(section);
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
        if (lastStation.equals(section.getUpStation())) {
            stations.add(section.getDownStation());
        }
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(Section::isFirst)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
