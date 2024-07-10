package subway.line;

import subway.station.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(length = 20, nullable = false)
    private String color;

    @Embedded
    private Sections sections;

    public Line() {
    }

    private Line(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.color = builder.color;
        this.sections = Sections.of(this, builder.upStation, builder.downStation, builder.distance);
    }

    public void modify(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void registerSection(Station upStation, Station downStation, Integer distance) {
        sections.add(createNotFirstStation(this, upStation, downStation, distance));
    }

    private static Section createNotFirstStation(Line line, Station upStation, Station downStation, Integer distance) {
        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .isFirst(false)
                .build();
    }

    public List<Long> getStationIds() {
        return sections.getStationIds();
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

    public void deleteSection(Long stationId) {
        sections.delete(stationId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String color;
        private Station upStation;
        private Station downStation;
        private Integer distance;

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

        public Builder upStation(Station upStation) {
            this.upStation = upStation;
            return this;
        }

        public Builder downStation(Station downStation) {
            this.downStation = downStation;
            return this;
        }

        public Builder distance(Integer distance) {
            this.distance = distance;
            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }
}
