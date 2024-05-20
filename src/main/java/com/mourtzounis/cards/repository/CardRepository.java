package com.mourtzounis.cards.repository;

import com.mourtzounis.cards.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {
    Optional<Card> findCardByIdAndUserId(Long cardId, Long userId);
    List<Card> findByUserId(Long userId);
}
