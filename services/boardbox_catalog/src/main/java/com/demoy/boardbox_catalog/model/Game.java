package com.demoy.boardbox_catalog.model;

import java.util.UUID;

public record Game(UUID id, String title, int minPlayers, int maxPlayers, int playtimeMin) {
}
