package com.mourtzounis.cards.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mourtzounis.cards.enums.CardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.mourtzounis.cards.enums.CardStatus.TO_DO;

@Entity
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserInfo user;

    @Column(nullable = false)
    @NotBlank(message = "Card name cannot be blank")
    private String name;

    @Column
    private String description;

    @Column
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Color must be 6 alphanumeric characters prefixed with a #")
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus cardStatus = TO_DO;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy - HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id) && Objects.equals(user, card.user) && Objects.equals(name, card.name)
                && Objects.equals(description, card.description) && Objects.equals(color, card.color)
                && cardStatus == card.cardStatus && Objects.equals(createdAt, card.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, name, description, color, cardStatus, createdAt);
    }
}
