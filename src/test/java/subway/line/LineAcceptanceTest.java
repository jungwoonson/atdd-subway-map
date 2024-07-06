package subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 역 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LineAcceptanceTest {

    private static final String LINE_NAME_1 = "신분당선";
    private static final String LINE_NAME_2 = "분당선";
    private static final String COLOR_1 = "bg-red-600";
    private static final String COLOR_2 = "bg-green-600";
    private static final Long STATION_ID_1 = 1L;
    private static final Long STATION_ID_2 = 2L;
    private static final Long STATION_ID_3 = 3L;
    private static final Integer DEFAULT_DISTANCE = 10;

    private static final Map<String, Object> CREATE_PARAM_1 = Map.of(
            "name", LINE_NAME_1,
            "color", COLOR_1,
            "upStationId", STATION_ID_1,
            "downStationId", STATION_ID_2,
            "distance", DEFAULT_DISTANCE
    );

    private static final Map<String, Object> CREATE_PARAM_2 = Map.of(
            "name", LINE_NAME_2,
            "color", COLOR_2,
            "upStationId", STATION_ID_1,
            "downStationId", STATION_ID_3,
            "distance", DEFAULT_DISTANCE
    );

    /**
     * Given 새로운 지하철 노선 정보를 입력하고
     * When 관리자가 노선을 생성하면
     * Then 해당 노선이 생성되고 노선 목록에 포함된다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void createLineTest() {
        // when
        ExtractableResponse<Response> response = createLine(CREATE_PARAM_1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(findNames(lookUpLines())).containsExactlyInAnyOrder(LINE_NAME_1);
    }

    /**
     * Given 여러 개의 지하철 노선이 등록되어 있고,
     * When 관리자가 지하철 노선 목록을 조회하면,
     * Then 모든 지하철 노선 목록이 반환된다.
     */
    @DisplayName("노선 목록을 조회한다.")
    @Test
    void locoUpLineTest() {
        // given
        createLine(CREATE_PARAM_1);
        createLine(CREATE_PARAM_2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findNames(response)).containsExactlyInAnyOrder(LINE_NAME_1, LINE_NAME_2);
    }

    private ExtractableResponse<Response> createLine(Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> lookUpLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private List<String> findNames(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getList("name", String.class);
    }
}
