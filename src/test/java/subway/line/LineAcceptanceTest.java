package subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.line.LineAcceptanceTestFixture.*;

@DisplayName("지하철 역 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LineAcceptanceTest {

    @BeforeEach
    void setUp() {
        createStation(STATION_ID_1, STATION_NAME_1);
        createStation(STATION_ID_2, STATION_NAME_2);
        createStation(STATION_ID_3, STATION_NAME_3);
        createStation(STATION_ID_4, STATION_NAME_4);
    }

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

        // when
        ExtractableResponse<Response> response = lookUpLine(findId(createdLineResponse));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findName(response)).isEqualTo(LINE_NAME_1);
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

        // when
        ExtractableResponse<Response> response = modifyLine(findId(createdLineResponse), MODIFY_PARAM);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> lookedUpLine = lookUpLine(findId(createdLineResponse));
        assertThat(findName(lookedUpLine)).isEqualTo(LINE_NAME_2);
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

        // when
        ExtractableResponse<Response> response = deleteLine(findId(createdLineResponse));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(findNames(lookUpLines())).doesNotContain(LINE_NAME_1);
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 해당 노선에 구간을 등록하면,
     * Then: 해당 노선에 구간에 추가된다.
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void registerSectionTest() {
        // given
        ExtractableResponse<Response> createdLineResponse = createLine(CREATE_PARAM_1);

        // when
        ExtractableResponse<Response> response = registerSection(findId(createdLineResponse), SECTION_PARAM_1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<Long> stationsIds = lookUpStationIds(findId(createdLineResponse));
        assertThat(stationsIds).containsExactly(STATION_ID_1, STATION_ID_2, STATION_ID_3);
    }

    /**
     * When: 존재하지 않는 노선에 구간을 등록하면,
     * Then: 오류를 응답한다.
     */
    @DisplayName("존재하지 않는 노선에 구간을 등록하면 오류가 발생한다.")
    @Test
    void notExistLineExceptionTest() {
        // when
        ExtractableResponse<Response> response = registerSection(1L, SECTION_PARAM_1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 존재하지 않는 역이 포함된 구간을 등록하면,
     * Then: 오류를 응답한다.
     */
    @DisplayName("존재하지 않는 역이 포함된 구간을 노선에 등록하면 오류를 응답한다.")
    @Test
    void notExistStationExceptionTest() {
        // given
        ExtractableResponse<Response> createdLineResponse = createLine(CREATE_PARAM_1);

        // when
        ExtractableResponse<Response> response = registerSection(findId(createdLineResponse), SECTION_PARAM_2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given: 특정 노선이 등록되어 있고,
     * When: 노선의 하행역과 다른 역이 상행역인 구간을 등록하면,
     * Then: 오류를 응답한다.
     */
    @DisplayName("새로운 구간의 상행역이 해당 노선의 하행역이 아니면 오류를 응답한다.")
    @Test
    void notSameUpStationAndDownStationExceptionTest() {
        // given
        ExtractableResponse<Response> createdLineResponse = createLine(CREATE_PARAM_1);

        // when
        ExtractableResponse<Response> response = registerSection(findId(createdLineResponse), SECTION_PARAM_3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given: 특정 노선이 등록되어 있고,
     * When: 노선에 등록되어 있는 역이 하행역인 구간을 등록하면,
     * Then: 오류를 응답한다.
     */
    @DisplayName("새로운 구간의 상행역이 해당 노선의 하행역이 아니면 오류를 응답한다.")
    @Test
    void alreadyRegisteredStationExceptionTest() {
        // given
        ExtractableResponse<Response> createdLineResponse = createLine(CREATE_PARAM_1);

        // when
        ExtractableResponse<Response> response = registerSection(findId(createdLineResponse), SECTION_PARAM_4);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given: 특정 노선에 구간이 2개 이상 등록되어 있고,
     * When: 노선의 하행역을 제거하면,
     * Then: 노선을 조회했을 때 하행역이 제거된다.
     */
    @DisplayName("지하철 노선에 구간을 제거한다.")
    @Test
    void deleteSectionTest() {
        // given
        ExtractableResponse<Response> createdLineResponse = createLine(CREATE_PARAM_1);
        registerSection(findId(createdLineResponse), SECTION_PARAM_1);

        // when
        ExtractableResponse<Response> response = deleteSection(findId(createdLineResponse), STATION_ID_3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> stationIds = lookUpStationIds(findId(createdLineResponse));
        assertThat(stationIds).doesNotContain(STATION_ID_3);
    }

    /**
     * Given: 특정 노선에 구간이 2개 이상 등록되어 있고,
     * When: 노선의 하행역이 아닌 역을 제거하면,
     * Then: 오류를 응답한다.
     */
    @DisplayName("지하철 노선에 등록된 역(하행 종점역)만 제거할 수 있다. 즉, 마지막 구간만 제거할 수 있다.")
    @Test
    void deleteNotDownStationExceptionTest() {
        // given
        ExtractableResponse<Response> createdLineResponse = createLine(CREATE_PARAM_1);
        registerSection(findId(createdLineResponse), SECTION_PARAM_1);

        // when
        ExtractableResponse<Response> response = deleteSection(findId(createdLineResponse), STATION_ID_2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given: 특정 노선에 구간이 1개 등록되어 있고,
     * When: 노선의 하행역 구간을 제거하면,
     * Then: 오류를 응답한다.
     */
    @DisplayName("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.")
    @Test
    void deleteSectionOfOnlyOneSectionLineExceptionTest() {
        // given
        ExtractableResponse<Response> createdLineResponse = createLine(CREATE_PARAM_1);

        // when
        ExtractableResponse<Response> response = deleteSection(findId(createdLineResponse), STATION_ID_2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

    private ExtractableResponse<Response> registerSection(Long id, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(String.format("/lines/%d/sections", id))
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteSection(Long id, Long stationId) {
        return RestAssured.given().log().all()
                .when().delete(String.format("/lines/%s/sections?stationId=%s", id, stationId))
                .then().log().all()
                .extract();
    }

    private List<Long> lookUpStationIds(Long lindId) {
        return lookUpLine(lindId).jsonPath()
                .getList("stations.id", Long.class);
    }

    private List<String> findNames(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getList("name", String.class);
    }

    private static String findName(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("name");
    }

    private static long findId(ExtractableResponse<Response> createdLineResponse) {
        return createdLineResponse.jsonPath()
                .getLong("id");
    }
}
