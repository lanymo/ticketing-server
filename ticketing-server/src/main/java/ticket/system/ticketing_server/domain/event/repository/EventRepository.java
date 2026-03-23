package ticket.system.ticketing_server.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.system.ticketing_server.domain.event.entity.Event;
import ticket.system.ticketing_server.domain.event.entity.EventStatus;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    // 상태별 조회
    List<Event> findByStatus(EventStatus status);

    // 상태별 + 시작일 정렬
    List<Event> findByStatusOrderByStartDateAsc(EventStatus status);

    // 공연 이름 검색
    List<Event> findByNameContaining(String keyword);
}
