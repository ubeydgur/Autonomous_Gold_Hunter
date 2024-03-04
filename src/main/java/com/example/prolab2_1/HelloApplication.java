package com.example.prolab2_1;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

class InfoRect {
    Rectangle rectangle;
    boolean isAvailable = true;
}

public class HelloApplication extends Application {
    ArrayList<InfoRect> rectangleArray = new ArrayList<>();
    Random random = new Random();



    @Override
    public void start(Stage stage) throws IOException {
        int windowHeight;
        int windowWidth;
        double rectangleSize = 9.5;
        double gapSize = 0.5;

        /*System.out.println("Enter Window Height: ");
        Scanner scanner = new Scanner(System.in);
        windowHeight = scanner.nextInt();
        System.out.println("Enter Window Width: ");
        windowWidth = scanner.nextInt();*/

        double rectAmountOnX = 1000 / (rectangleSize + gapSize);
        double rectAmountOnY = 1000 / (rectangleSize + gapSize);
        double rectTotal = rectAmountOnX * rectAmountOnY;


        // Creating Ractangles
        for (double y = 0; y < 1000; y+=rectangleSize + gapSize) {
            for (double x = 0; x < 1000; x+=rectangleSize + gapSize) {
                InfoRect infoRect = new InfoRect();
                infoRect.rectangle = new Rectangle(x, y, rectangleSize, rectangleSize);
                if (x > 490)
                    infoRect.rectangle.setFill(Color.LIGHTGREEN);
                else
                    infoRect.rectangle.setFill(Color.WHITE);
                rectangleArray.add(infoRect);
            }
        }


        // Creating Obstacles and Treasures
        ArrayList<StaticObstacle.SummerObstacles> summerObstacle = new ArrayList<>();
        summerObstacle.addAll(Arrays.asList(StaticObstacle.SummerObstacles.values()));

        ArrayList<StaticObstacle.WinterObstacles> winterObstacle = new ArrayList<>();
        winterObstacle.addAll(Arrays.asList(StaticObstacle.WinterObstacles.values()));

        ArrayList<DynamicObstacle.DynamicObstacles> dynamicObstacle = new ArrayList<>();
        dynamicObstacle.addAll(Arrays.asList(DynamicObstacle.DynamicObstacles.values()));

        ArrayList<StaticObstacle> staticObstacles = new ArrayList<>();
        ArrayList<DynamicObstacle> dynamicObstacles = new ArrayList<>();
        ArrayList<Treasure> treasures = new ArrayList<>();
        TreasureGenerator treasureGenerator = new TreasureGenerator();
        ObstacleGenerator obstacleGenerator = new ObstacleGenerator();

        int totalStaticObstacle = 20;
        int totalDynamicObstacle = 3;
        int totalTreasure = 5;
        int randomObstacleIndex;
        int randomSeason;

        for (int i = 0; i < totalStaticObstacle; i++) {
            randomSeason = random.nextInt(2);
            switch (randomSeason){
                case 0:
                    randomObstacleIndex = random.nextInt(winterObstacle.size());
                    staticObstacles.add(obstacleGenerator.generateWinterObstacle(winterObstacle.get(randomObstacleIndex)));
                    break;
                case 1:
                    randomObstacleIndex = random.nextInt(summerObstacle.size());
                    staticObstacles.add(obstacleGenerator.generateSummerObstacle(summerObstacle.get(randomObstacleIndex)));
                    break;
            }
        }

        for (int i = 0; i < totalDynamicObstacle; i++){
            randomObstacleIndex = random.nextInt(dynamicObstacle.size());
            dynamicObstacles.add(obstacleGenerator.generateDynamicObstacle(dynamicObstacle.get(randomObstacleIndex)));
        }

        for (int i = 0; i < totalTreasure; i++){
            treasures.add(treasureGenerator.goldChest());
            treasures.add(treasureGenerator.silverChest());
            treasures.add(treasureGenerator.emeraldChest());
            treasures.add(treasureGenerator.copperChest());
        }

        // Coordinates of Treasures and Obstacles
        ArrayList<InfoRect> infoRects = new ArrayList<>();
        int imageX;
        int imageY;
        int index;

        // Coordinate of Static Obstacles
        for (int k = 0; k < staticObstacles.size(); k++) {
            search:
            for (int m = 0; m < 1; m++) {
                imageY = random.nextInt(99 - staticObstacles.get(k).sizeY);
                if (staticObstacles.get(k).getSeason() == "summer")
                    imageX = random.nextInt(49 - staticObstacles.get(k).sizeX) + 50;
                else
                    imageX = random.nextInt(49 - staticObstacles.get(k).sizeX);
                for (int y = 0; y < staticObstacles.get(k).sizeY + 2; y++) {
                    for (int x = 0; x < staticObstacles.get(k).sizeX + 2; x++) {
                        index = (imageX + x) + ((imageY + y) * 100);
                        if (rectangleArray.get(index).isAvailable) {
                            infoRects.add(rectangleArray.get(index));
                        }
                        else {
                            m--;
                            infoRects.clear();
                            continue search;
                        }
                    }
                }
            }

            for (int i = 0; i < infoRects.size(); i++)
                infoRects.get(i).isAvailable = false;

            staticObstacles.get(k).imageView.setX(infoRects.get(staticObstacles.get(k).sizeX + 3).rectangle.getX());
            staticObstacles.get(k).imageView.setY(infoRects.get(staticObstacles.get(k).sizeX + 3).rectangle.getY());
            infoRects.clear();
        }

        // Coordinate of Dynamic Obstacles
        for (int k = 0; k < dynamicObstacles.size(); k++) {
            search:
            for (int m = 0; m < 1; m++) {
                imageY = random.nextInt(99 - dynamicObstacles.get(k).sizeY);
                imageX = random.nextInt(99 - dynamicObstacles.get(k).sizeX);

                for (int y = 0; y < dynamicObstacles.get(k).sizeY; y++) {
                    for (int x = 0; x < dynamicObstacles.get(k).sizeX; x++) {
                        index = (imageX + x) + ((imageY + y) * 100);
                        if (rectangleArray.get(index).isAvailable) {
                            infoRects.add(rectangleArray.get(index));
                        }
                        else {
                            m--;
                            infoRects.clear();
                            continue search;
                        }
                    }
                }
            }

            for (int i = 0; i < infoRects.size(); i++) {
                infoRects.get(i).rectangle.setFill(Color.LIGHTPINK);
                infoRects.get(i).isAvailable = false;
            }

            dynamicObstacles.get(k).imageView.setX(infoRects.get(0).rectangle.getX());
            dynamicObstacles.get(k).imageView.setY(infoRects.get(0).rectangle.getY());
            infoRects.clear();
        }

        // Coordinate of Treasures
        for (int k = 0; k < treasures.size(); k++) {
            search:
            for (int m = 0; m < 1; m++) {
                imageY = random.nextInt(99 - treasures.get(k).sizeY);
                imageX = random.nextInt(99 - treasures.get(k).sizeX);

                for (int y = 0; y < treasures.get(k).sizeY; y++) {
                    for (int x = 0; x < treasures.get(k).sizeX; x++) {
                        index = (imageX + x) + ((imageY + y) * 100);
                        if (rectangleArray.get(index).isAvailable) {
                            infoRects.add(rectangleArray.get(index));
                        }
                        else {
                            m--;
                            infoRects.clear();
                            continue search;
                        }
                    }
                }
            }

            for (int i = 0; i < infoRects.size(); i++)
                infoRects.get(i).isAvailable = false;

            treasures.get(k).imageView.setX(infoRects.get(0).rectangle.getX());
            treasures.get(k).imageView.setY(infoRects.get(0).rectangle.getY());
            infoRects.clear();
        }

        // Add Obstacles, Treasures and Rectangles to Screen
        Group myGroup = new Group();

        for (int i = 0; i < rectTotal; i++)
            myGroup.getChildren().add(rectangleArray.get(i).rectangle);

        for (int i = 0; i < staticObstacles.size(); i++)
            myGroup.getChildren().add(staticObstacles.get(i).imageView);

        for (int i = 0; i < dynamicObstacles.size(); i++)
            myGroup.getChildren().add(dynamicObstacles.get(i).imageView);

        for (int i = 0; i < treasures.size(); i++)
            myGroup.getChildren().add(treasures.get(i).imageView);
        
        Scene scene = new Scene(myGroup,1000,1000/*windowWidth,windowHeight*/);
        scene.setFill(Color.BLACK);
        stage.setTitle("GOLDEN HUNTER");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}