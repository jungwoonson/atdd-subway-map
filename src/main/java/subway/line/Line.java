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
        this.sections = new Sections();
    }

    public void modify(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void registerSection(Section section) {
        if (sections.isEmpty()) {
            sections = Sections.from(section);
            return;
        }
        sections.add(section);
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

    public void deleteSection(Station station) {
        sections.delete(station);
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
