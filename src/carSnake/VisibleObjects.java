/*
 * Copyright (C) 2015 johanwendt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package carSnake;

import javafx.scene.paint.ImagePattern;

/**
 *
 * @author johanwendt
 */
public enum VisibleObjects {
    PLAYER_ONE ("PlayerOne.png", BuildingBlock.playerOneImage(), 1),
    PLAYER_TWO ("PlayerTwo.png", BuildingBlock.playerTwoImage(), 2),
    PLAYER_THREE ("PlayerThree.png", BuildingBlock.playerThreeImage(), 3),
    PLAYER_FOUR ("PlayerFour.png", BuildingBlock.playerFourImage(), 4),
    MAKE_LONGER_BONUS("car186.png", BuildingBlock.greenBonusImage(), 5),
    MAKE_FASTER_BONUS("fuelpump.png", BuildingBlock.yellowBonusImage(), 6),
    SHOOT_BONUS("bombs2.png", BuildingBlock.redBonusImage(), 7),
    DEATH("stop5.png", BuildingBlock.greyBonusImage(), 8),
    DEATH_PLAYER("overturned.png", BuildingBlock.playerDeathImage(), 9);

    private ImagePattern blockImage;
    private final String objectImage;
    private final int actionNumber;
    public static final int MAKE_LONGER = 5;
    public static final int MAKE_FASTER = 6;
    public static final int SHOOT = 7;
    
    VisibleObjects(String playerImage, ImagePattern blockImage, int actionNumber) {
        this.objectImage = playerImage;
        this.blockImage = blockImage;
        this.actionNumber = actionNumber;
    }
    public String getImage() {
        return objectImage;
    }
    public ImagePattern getBlockImage() {
        return blockImage;
    }
    public void setBlockImage(ImagePattern image) {
        blockImage = image;
    }
    public int getActionNumber() {
        return actionNumber;
    }
}
