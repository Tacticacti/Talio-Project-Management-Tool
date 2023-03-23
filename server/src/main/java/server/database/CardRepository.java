package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import commons.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
}

