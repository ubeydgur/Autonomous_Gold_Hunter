package com.example.prolab2_1;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

enum MotionDirection{
    RIGHT,
    LEFT,
    UP,
    DOWN
}

public class Character {
    int locationX;
    int locationY;
    int lastLocationX;
    int lastLocationY;
    int characterSizeX;
    int characterSizeY;
    int currentRectangleIndex;
    int maxStraigthWay;
    int minStraigthWay;
    int randomStraigthWay;
    int currentStraightWay = 0;
    final int viewField = 7;
    final int viewDirection = 3;
    InputStream imagePath;
    ImageView imageView;
    Image image;
    MotionDirection frontDirection;
    MotionDirection backDirection;
    ArrayList<MotionDirection> emptyDirections = new ArrayList<>();
    ArrayList<MotionDirection> lastFourDirections = new ArrayList<>();


    public Character(String imagePath, int locationX, int locationY, int characterSizeX, int characterSizeY, int rectangleAndGapSize, int maxStraigthWay, int minStraigthWay) throws FileNotFoundException {
        this.locationX = locationX;
        this.locationY = locationY;
        this.lastLocationX = locationX * rectangleAndGapSize;
        this.lastLocationY = locationY * rectangleAndGapSize;
        this.characterSizeX = characterSizeX * rectangleAndGapSize;
        this.characterSizeY = characterSizeY * rectangleAndGapSize;
        this.imagePath = new FileInputStream(imagePath);
        image = new Image(this.imagePath);
        imageView = new ImageView(image);
        imageView.setX(locationX * rectangleAndGapSize);
        imageView.setY(locationY * rectangleAndGapSize);
        imageView.setFitHeight(this.characterSizeY);
        imageView.setFitWidth(this.characterSizeX);
        this.maxStraigthWay = maxStraigthWay;
        this.minStraigthWay = minStraigthWay;
    }


    MotionDirection getDirection(int windowWidth, int windowHeight, int rectangleAndGapSize, ArrayList<RectangleInfo> rectanglesInfo) {
        emptyDirections.clear();
        boolean isIteratedPath = false;
        if (lastFourDirections.size() == 4){
            isIteratedPath = true;
            search :
            for (int i = 0; i < lastFourDirections.size() -1; i++)
            {
                for (int j = i+1; j < lastFourDirections.size(); j++)
                {
                    if (lastFourDirections.get(i) == lastFourDirections.get(j)){
                        isIteratedPath = false;
                        lastFourDirections.removeFirst();
                        break search;
                    }
                }
            }
        }

        if ((currentRectangleIndex - windowWidth / rectangleAndGapSize) >= 0 &&
                rectanglesInfo.get(currentRectangleIndex - windowWidth / rectangleAndGapSize).isPlayerMoved && backDirection != MotionDirection.UP) {
            emptyDirections.add(MotionDirection.UP);
        }
        if ((currentRectangleIndex + windowWidth / rectangleAndGapSize) < (windowWidth / rectangleAndGapSize * windowHeight / rectangleAndGapSize) &&
                rectanglesInfo.get(currentRectangleIndex + windowWidth / rectangleAndGapSize).isPlayerMoved && backDirection != MotionDirection.DOWN) {
            emptyDirections.add(MotionDirection.DOWN);
        }
        if ((currentRectangleIndex - 1 > currentRectangleIndex - (currentRectangleIndex % (windowWidth / rectangleAndGapSize)) - 1) &&
                rectanglesInfo.get(currentRectangleIndex - 1).isPlayerMoved && backDirection != MotionDirection.LEFT) {
            emptyDirections.add(MotionDirection.LEFT);
        }
        if ((currentRectangleIndex + 1 < currentRectangleIndex + ((windowWidth / rectangleAndGapSize) - currentRectangleIndex % (windowWidth / rectangleAndGapSize))) &&
                rectanglesInfo.get(currentRectangleIndex + 1).isPlayerMoved && backDirection != MotionDirection.RIGHT) {
            emptyDirections.add(MotionDirection.RIGHT);
        }

        if (emptyDirections.size() == 1 && emptyDirections.getFirst() == frontDirection) {
            currentStraightWay = 0;
            return frontDirection;
        }
        else {
            if (isIteratedPath) {
                emptyDirections.remove(lastFourDirections.getFirst());
                lastFourDirections.removeFirst();
            }
            emptyDirections.remove(frontDirection);
        }

        if (emptyDirections.isEmpty()) {
            currentStraightWay = 0;
            return backDirection;
        }

        currentStraightWay = 0;
        Random random = new Random();
        randomStraigthWay = random.nextInt(minStraigthWay) + (maxStraigthWay - minStraigthWay);
        int randomDirectionIndex = random.nextInt(emptyDirections.size());
        MotionDirection newDirection = emptyDirections.get(randomDirectionIndex);
        lastFourDirections.add(newDirection);

        return newDirection;
    }

    public void checkMotionDirection(int windowWidth, int windowHeight, int rectangleAndGapSize, ArrayList<RectangleInfo> rectanglesInfo) {
        switch (frontDirection) {
            case UP:
                for (int i = 1; i <= 3; i++) {
                    if ((currentRectangleIndex - windowWidth / rectangleAndGapSize * i) >= 0 &&
                            !rectanglesInfo.get(currentRectangleIndex - windowWidth / rectangleAndGapSize * i).isPlayerMoved) {
                        frontDirection = getDirection(windowWidth, windowHeight, rectangleAndGapSize, rectanglesInfo);
                        backDirection = getBackDirection();
                    }
                }
                break;

            case DOWN:
                for (int i = 1; i <= 3; i++) {
                    if ((currentRectangleIndex + windowWidth / rectangleAndGapSize * i) < (windowWidth / rectangleAndGapSize * windowHeight / rectangleAndGapSize) &&
                            !rectanglesInfo.get(currentRectangleIndex + windowWidth / rectangleAndGapSize * i).isPlayerMoved) {
                        frontDirection = getDirection(windowWidth, windowHeight, rectangleAndGapSize, rectanglesInfo);
                        backDirection = getBackDirection();
                    }
                }
                break;

            case LEFT:
                for (int i = 1; i <= 3; i++) {
                    if ((currentRectangleIndex - i > currentRectangleIndex - (currentRectangleIndex % (windowWidth / rectangleAndGapSize)) - 1)
                            && !rectanglesInfo.get(currentRectangleIndex - i).isPlayerMoved) {
                        frontDirection = getDirection(windowWidth, windowHeight, rectangleAndGapSize, rectanglesInfo);
                        backDirection = getBackDirection();
                    }
                }
                break;

            case RIGHT:
                for (int i = 1; i <= 3; i++) {
                    if ((currentRectangleIndex + i < currentRectangleIndex + ((windowWidth / rectangleAndGapSize) - currentRectangleIndex % (windowWidth / rectangleAndGapSize))) &&
                            !rectanglesInfo.get(currentRectangleIndex + i).isPlayerMoved) {
                        frontDirection = getDirection(windowWidth, windowHeight, rectangleAndGapSize, rectanglesInfo);
                        backDirection = getBackDirection();
                    }
                }
                break;
        }
    }


    public void checkAround(int windowWidth, int rectangleAndGapSize, ArrayList<RectangleInfo> rectanglesInfo) {
        int aroundInitialIndex = currentRectangleIndex - viewDirection - viewDirection * windowWidth / rectangleAndGapSize;
        if (aroundInitialIndex < 0) {

        }
        for (int y = 0; y < viewField; y++) {
            for (int x = 0; x < viewField; x++) {
                if (aroundInitialIndex + x + y * windowWidth / rectangleAndGapSize >= 0 && aroundInitialIndex + x + y * windowWidth / rectangleAndGapSize < 10000) {
                    rectanglesInfo.get(aroundInitialIndex + x + y * windowWidth / rectangleAndGapSize).rectangle.setFill(Color.BLUE);
                }

            }
        }
    }


    public MotionDirection getBackDirection() {
        switch (frontDirection) {
            case UP:
                return MotionDirection.DOWN;
            case DOWN:
                return MotionDirection.UP;
            case LEFT:
                return MotionDirection.RIGHT;
            case RIGHT:
                return MotionDirection.LEFT;
        }
        return null;
    }
}
