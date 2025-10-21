package com.demoy.boardgamebooking.model;

import java.util.UUID;

public record Game(UUID id, String title, int minPlayers, int maxPlayers, int playtimeMin) {
}
