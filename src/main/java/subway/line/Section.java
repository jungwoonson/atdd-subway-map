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

    public Section(Line line, Station upStation, Station downStation, Integer distance, boolean isFirst) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.isFirst = isFirst;
    }

    public static Section of(Line line, Long upStationId, Long downStationId, Integer distance) {
        return new Section(line, new Station(upStationId), new Station(downStationId), distance, true);
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
}
