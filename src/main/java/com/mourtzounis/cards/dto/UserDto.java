package com.mourtzounis.cards.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mourtzounis.cards.model.Card;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class UserDto {

    private Long id;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "email is not valid")
    private String email;

    @JsonIgnore
    @NotBlank(message = "password cannot be blank")
    private String password;

    private String roles;

    private List<Card> cards;

}
