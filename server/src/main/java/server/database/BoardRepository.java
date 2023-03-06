package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import commons.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {}

