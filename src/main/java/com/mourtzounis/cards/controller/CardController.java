package com.mourtzounis.cards.controller;

import com.mourtzounis.cards.model.Card;
import com.mourtzounis.cards.service.AuthenticationService;
import com.mourtzounis.cards.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;
    private final AuthenticationService authenticationService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Card>> getAllCards() {
        return ResponseEntity.ok(cardService.getAllCards());
    }

    @GetMapping("/{userId}/all")
    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_ADMIN') and #userId == authentication.principal.id")
    public ResponseEntity<List<Card>> getAllUserCards(@Valid @NotNull(message = "userId cannot be null") @PathVariable Long userId) {
        return ResponseEntity.ok(cardService.getAllUserCards(userId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Card> createCard(@RequestBody @Valid Card card) {
        var createdCard = cardService.createCard(card);

        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_ADMIN')")
    @GetMapping("/{cardId}")
    public ResponseEntity<Card> getUserCard(@Valid @NotNull(message = "cardId cannot be null") @PathVariable Long cardId) {
        var userId = authenticationService.getUserIdFromPrincipal();
        var userCard = cardService.getUserCard(cardId, userId);

        return ResponseEntity.ok(userCard);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_ADMIN') and #userId == authentication.principal.id")
    @GetMapping(value = "/{userId}/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Card>> filterUserCards(
            @Valid @NotNull(message = "userId cannot be null") @PathVariable Long userId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String cardStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy - HH:mm:ss") LocalDateTime createdAt,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        log.info("Filtering cards with params - userId: {}, name: {}, color: {}, status: {}, createdAt: {}, page: {}, size: {}, " +
                        "offset: {}, limit: {}, sortBy: {}, sortDirection: {}",
                userId, name, color, cardStatus, createdAt, page, size, offset, limit, sortBy, sortDirection);

        var pageable = createPageable(page, size, offset, limit, sortBy, sortDirection);
        var filteredCards = cardService.filterUserCards(userId, name, color, cardStatus, createdAt, pageable);

        return ResponseEntity.ok(filteredCards.getContent());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_ADMIN')")
    @PutMapping("/{cardId}")
    public ResponseEntity<Card> updateCard(@NotNull(message = "cardId cannot be null") @PathVariable Long cardId,
                                           @RequestBody Card updatedCard) {
        var userId = authenticationService.getUserIdFromPrincipal();

        var updatedCardOpt = cardService.updateCard(userId, cardId, updatedCard);

        return updatedCardOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_ADMIN')")
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        var userId = authenticationService.getUserIdFromPrincipal();
        cardService.deleteCard(cardId, userId);

        return ResponseEntity.accepted().build();
    }

    private Pageable createPageable(Integer page, Integer size, Integer offset, Integer limit, String sortBy, String sortDirection) {
        var direction = Sort.Direction.fromString(sortDirection);
        var sort = Sort.by(direction, sortBy);

        if (page != null && size != null) {
            return PageRequest.of(page, size, sort);
        } else if (offset != null && limit != null) {
            return PageRequest.of(offset / limit, limit, sort);
        } else {
            return Pageable.unpaged();
        }
    }

}
