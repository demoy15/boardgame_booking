package com.demoy.boardgamebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDto {
    private UUID id;
    private String title;
    private int minPlayers;
    private int maxPlayers;
    private int playtimeMin;
}
