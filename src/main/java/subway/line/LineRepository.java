package subway.line;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;

public interface LineRepository extends JpaRepository<Line, Long> {
    Line findOneById(Long id);
}
