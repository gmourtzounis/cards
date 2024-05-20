package com.mourtzounis.cards.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Getter
@ToString
public enum CardStatus {
    @JsonProperty("To Do") TO_DO("To Do"),
    @JsonProperty("In Progress") IN_PROGRESS("In Progress"),
    @JsonProperty("Done") DONE("Done");

    private final String name;

    CardStatus(String name) {
        this.name = name;
    }

    public static CardStatus find(String name) {
        return Arrays.stream(values())
                .filter(cardStatus -> cardStatus.name.equalsIgnoreCase(name))
                .findFirst().orElseThrow(() -> new NoSuchElementException("Card Status Not Found"));
    }
}
