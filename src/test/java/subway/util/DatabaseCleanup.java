package subway.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class DatabaseCleanup {

    public static final String DELETE_SECTION = "DELETE FROM section";
    public static final String DELETE_LINE = "DELETE FROM line";
    public static final String DELETE_STATION = "DELETE FROM station";
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void cleanUpTablesForLineTest() {
        entityManager.createNativeQuery(DELETE_SECTION).executeUpdate();
        entityManager.createNativeQuery(DELETE_LINE).executeUpdate();
    }

    @Transactional
    public void cleanUpAllTables() {
        entityManager.createNativeQuery(DELETE_SECTION).executeUpdate();
        entityManager.createNativeQuery(DELETE_LINE).executeUpdate();
        entityManager.createNativeQuery(DELETE_STATION).executeUpdate();
    }
}