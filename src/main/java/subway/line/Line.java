package subway.line;

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
    @Convert(converter = StringListConverter.class)
    private List<Long> stations;
    @Column(nullable = false)
    private Integer distance;

    public Line() {
    }

    private Line(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.color = builder.color;
        this.stations = builder.stations;
        this.distance = builder.distance;
    }

    public void modify(String name, String color) {
        this.name = name;
        this.color = color;
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

    public List<Long> getStations() {
        return List.copyOf(stations);
    }

    public Integer getDistance() {
        return distance;
    }

    public static class Builder {
        private Long id;
        private String name;
        private String color;
        private List<Long> stations;
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

        public Builder stations(Long upStationId, Long downStationId) {
            this.stations = List.of(upStationId, downStationId);
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
