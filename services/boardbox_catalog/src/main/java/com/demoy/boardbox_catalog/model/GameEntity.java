package com.demoy.boardbox_catalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("game")
public class GameEntity {

    @Id
    private UUID id;
    private String title;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Integer playtimeMin;
}