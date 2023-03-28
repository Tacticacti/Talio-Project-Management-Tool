package server.database;

import org.springframework.data.jpa.repository.JpaRepository;
import commons.Card;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
}

