package com.demoy.boardbox_catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDto {
    @JsonIgnore
    private UUID id;
    private String title;
    private int minPlayers;
    private int maxPlayers;
    private int playtimeMin;
}
