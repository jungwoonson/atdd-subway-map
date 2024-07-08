package subway.station;

import java.util.List;
import java.util.stream.Collectors;

public class Stations {

    List<Station> stations;

    private Stations(List<Station> stations) {
        this.stations = stations;
    }

    public static Stations of(Station upStation, Station downStation) {
        return new Stations(List.of(upStation, downStation));
    }

    public void add(Station station) {
        stations.add(station);
    }

    public Station lastStation() {
        return stations.get(stations.size() - 1);
    }

    public List<Long> getStationIds() {
        return stations.stream()
                .map(Station::getId)
                .collect(Collectors.toList());
    }
}
