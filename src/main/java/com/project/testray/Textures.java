package com.project.testray;

import javafx.scene.image.Image;

import java.util.Objects;

public class Textures {
    private final Image brickImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("textures/wall.png")));
    public Image getBrickImage() {
        return brickImage;
    }

    private final Image stoneImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("textures/stone.png")));
    public Image getStoneImage() {
        return stoneImage;
    }

    private final Image floorImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("textures/floor.png")));
    public Image getFloorImage() {
        return floorImage;
    }

    private final Image ceilImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("textures/ceil.png")));
    public Image getCeilImage() {
        return ceilImage;
    }

    private final Image[][] enemySprites = loadEnemySprites();

    private Image[][] loadEnemySprites() {
        String[][] paths = {
                {"textures/enemy/idle0.png"},
                {"textures/enemy/walk0.png", "textures/enemy/walk1.png", "textures/enemy/walk2.png", "textures/enemy/walk3.png"},
                {"textures/enemy/attack0.png", "textures/enemy/attack1.png"},
                {"textures/enemy/death0.png", "textures/enemy/death1.png", "textures/enemy/death2.png",
                        "textures/enemy/death3.png", "textures/enemy/death4.png"},
        };
        Image[][] result = new Image[paths.length][];
        for (int i = 0; i < paths.length; i++) {
            result[i] = new Image[paths[i].length];
            for (int j = 0; j < paths[i].length; j++) {
                result[i][j] = new Image(
                        Objects.requireNonNull(getClass().getResourceAsStream(paths[i][j]))
                );
            }
        }
        return result;
    }

    public Image getEnemySprite(Enemy.EnemyAnimationState state, int frame) {
        return enemySprites[state.ordinal()][frame];
    }
}
