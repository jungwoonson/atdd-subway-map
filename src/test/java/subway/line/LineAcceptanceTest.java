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

    private static final Map<String, Object> MODIFY_PARAM = Map.of(
            "name", LINE_NAME_2,
            "color", COLOR_2
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
    void lookUpLinesTest() {
        // given
        createLine(CREATE_PARAM_1);
        createLine(CREATE_PARAM_2);

        // when
        ExtractableResponse<Response> response = lookUpLines();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findNames(response)).containsExactlyInAnyOrder(LINE_NAME_1, LINE_NAME_2);
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 조회하면,
     * Then: 해당 노선의 정보가 반환된다.
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void lookUpLineTest() {
        // given
        ExtractableResponse<Response> createdLineResponse = createLine(CREATE_PARAM_1);
        Long id = createdLineResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = lookUpLine(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo(LINE_NAME_1);
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 수정하면,
     * Then: 해당 노선의 정보가 수정된다.
     */
    @DisplayName("노선을 수정한다.")
    @Test
    void modifyLineTest() {
        // given
        ExtractableResponse<Response> createdLineResponse = createLine(CREATE_PARAM_1);
        Long id = createdLineResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = modifyLine(id, MODIFY_PARAM);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> lookedUpLine = lookUpLine(id);
        assertThat(lookedUpLine.jsonPath().getString("name")).isEqualTo(LINE_NAME_2);
        assertThat(lookedUpLine.jsonPath().getString("color")).isEqualTo(COLOR_2);
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 삭제하면,
     * Then: 해당 노선이 삭제되고 노선 목록에서 제외된다.
     */
    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLineTest() {
        // given
        ExtractableResponse<Response> createdLineResponse = createLine(CREATE_PARAM_1);
        Long id = createdLineResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = deleteLine(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(findNames(lookUpLines())).doesNotContain(LINE_NAME_1);
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

    private ExtractableResponse<Response> lookUpLine(Long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> modifyLine(Long id, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();
    }
}
