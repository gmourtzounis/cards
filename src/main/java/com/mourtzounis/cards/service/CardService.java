package com.mourtzounis.cards.service;

import com.mourtzounis.cards.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CardService {
    List<Card> getAllCards();

    List<Card> getAllUserCards(Long userId);

    Page<Card> filterUserCards(Long userId, String name, String color, String status, LocalDateTime createdAt, Pageable pageable);

    Card createCard(Card card);

    Card getUserCard(Long cardId, Long userId);

    Optional<Card> updateCard(Long userId, Long cardId, Card updatedCard);

    void deleteCard(Long cardId, Long userId);
}
