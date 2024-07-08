package subway.station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Stations {

    private List<Station> stations;

    private Stations(List<Station> stations) {
        this.stations = stations;
    }

    public static Stations of(Station upStation, Station downStation) {
        return new Stations(new ArrayList<>(List.of(upStation, downStation)));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stations stations1 = (Stations) o;
        return Objects.equals(stations, stations1.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations);
    }
}
