package subway.line;

import subway.station.Station;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lineId", referencedColumnName = "id")
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStationId", referencedColumnName = "id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStationId", referencedColumnName = "id")
    private Station downStation;
    @Column(nullable = false)
    private Integer distance;
    @Column(nullable = false)
    private boolean isFirst;

    public Section() {
    }

    private Section(Builder builder) {
        this.line = builder.line;
        this.upStation = builder.upStation;
        this.downStation = builder.downStation;
        this.distance = builder.distance;
        this.isFirst = builder.isFirst;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private Line line;
        private Station upStation;
        private Station downStation;
        private Integer distance;
        private boolean isFirst;

        public Builder line(Line line) {
            this.line = line;
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

        public Builder isFirst(boolean isFirst) {
            this.isFirst = isFirst;
            return this;
        }

        public Section build() {
            return new Section(this);
        }
    }
}
