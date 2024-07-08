package subway.line;

import subway.station.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(length = 20, nullable = false)
    private String color;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "line", orphanRemoval = true)
    private List<Section> sections;

    public Line() {
    }

    private Line(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.color = builder.color;
    }

    public void modify(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        sections.add(section);
    }

    public List<Long> getStationIds() {
        return mapId(extractStations());
    }

    private static List<Long> mapId(List<Station> stations) {
        return stations.stream()
                .map(Station::getId)
                .collect(Collectors.toList());
    }

    private List<Station> extractStations() {
        Section firstSection = findFirstSection();

        List<Station> stations = new ArrayList<>();
        stations.add(firstSection.getUpStation());
        stations.add(firstSection.getDownStation());
        for (Section section : sections) {
            if (stations.get(stations.size() - 1).equals(section.getUpStation())) {
                stations.add(section.getDownStation());
            }
        }
        return stations;
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(Section::isFirst)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public static class Builder {
        private Long id;
        private String name;
        private String color;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }
}
