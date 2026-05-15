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
}
