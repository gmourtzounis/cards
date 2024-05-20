package com.mourtzounis.cards.service;

import com.mourtzounis.cards.enums.CardStatus;
import com.mourtzounis.cards.exception.CardNotFoundException;
import com.mourtzounis.cards.model.Card;
import com.mourtzounis.cards.model.UserInfo;
import com.mourtzounis.cards.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Override
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @Override
    public List<Card> getAllUserCards(Long userId) {
        return cardRepository.findByUserId(userId);
    }

    @Override
    public Card createCard(Card card) {
        Long userId = authenticationService.getUserIdFromPrincipal();
        UserInfo userInfo = userService.loadUser(userId);
        card.setUser(userInfo);

        return cardRepository.save(card);
    }

    @Override
    public Card getUserCard(Long cardId, Long userId) {
        return cardRepository.findCardByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
    }

    @Override
    public Page<Card> filterUserCards(Long userId, String name, String color, String cardStatus,
                                      LocalDateTime createdAt, Pageable pageable) {

        Specification<Card> spec = Specification.where(withUserId(userId))
                .and(Optional.ofNullable(name).map(this::withName).orElse(null))
                .and(Optional.ofNullable(color).map(this::withColor).orElse(null))
                .and(Optional.ofNullable(cardStatus).map(CardStatus::find).map(this::withCardStatus).orElse(null))
                .and(Optional.ofNullable(createdAt).map(this::withCreatedAt).orElse(null));

        return cardRepository.findAll(spec, pageable);
    }

    private Specification<Card> withName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name);
    }

    private Specification<Card> withColor(String color) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("color"), color);
    }

    private Specification<Card> withCardStatus(CardStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("cardStatus"), status);
    }

    private Specification<Card> withCreatedAt(LocalDateTime createdAt) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdAt"), createdAt);
    }

    private Specification<Card> withUserId(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
    }
    @Transactional
    public Optional<Card> updateCard(Long userId, Long cardId, Card updatedCard) {
        Optional<Card> cardOpt = cardRepository.findCardByIdAndUserId(cardId, userId);

        if (cardOpt.isPresent()) {
            Card card = cardOpt.get();

            if (updatedCard.getName() != null) {
                card.setName(updatedCard.getName());
            }
            if (updatedCard.getDescription() != null) {
                card.setDescription(updatedCard.getDescription());
            } else {
                card.setDescription(null);
            }
            if (updatedCard.getColor() != null) {
                card.setColor(updatedCard.getColor());
            } else {
                card.setColor(null);
            }
            if (updatedCard.getCardStatus() != null) {
                card.setCardStatus(updatedCard.getCardStatus());
            }
            cardRepository.save(card);
            return Optional.of(card);
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId, Long userId) {
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));

        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User does not have access to this card");
        }

        cardRepository.delete(card);
    }
}

