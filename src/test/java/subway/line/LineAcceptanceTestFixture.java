package subway.line;

import io.restassured.RestAssured;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineAcceptanceTestFixture {
    static final String LINE_NAME_1 = "신분당선";
    static final String LINE_NAME_2 = "분당선";
    static final String COLOR_1 = "bg-red-600";
    static final String COLOR_2 = "bg-green-600";
    static final Long STATION_ID_1 = 1L;
    static final Long STATION_ID_2 = 2L;
    static final Long STATION_ID_3 = 3L;
    static final String STATION_NAME_1 = "분당역";
    static final String STATION_NAME_2 = "홍대역";
    static final String STATION_NAME_3 = "강남역";
    static final Integer DEFAULT_DISTANCE = 10;

    static final Map<String, Object> CREATE_PARAM_1 = Map.of(
            "name", LINE_NAME_1,
            "color", COLOR_1,
            "upStationId", STATION_ID_1,
            "downStationId", STATION_ID_2,
            "distance", DEFAULT_DISTANCE
    );

    static final Map<String, Object> CREATE_PARAM_2 = Map.of(
            "name", LINE_NAME_2,
            "color", COLOR_2,
            "upStationId", STATION_ID_1,
            "downStationId", STATION_ID_3,
            "distance", DEFAULT_DISTANCE
    );

    static final Map<String, Object> MODIFY_PARAM = Map.of(
            "name", LINE_NAME_2,
            "color", COLOR_2
    );

    static final Map<String, Object> SECTION_PARAM_1 = Map.of(
            "upStationId", STATION_ID_2,
            "downStationId", STATION_ID_3,
            "distance", DEFAULT_DISTANCE
    );

    static void createStation(Long id, String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("name", name);

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all();
    }
}
