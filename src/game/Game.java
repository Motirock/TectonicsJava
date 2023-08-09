package game;

import main.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;

public class Game {
    private long updates = 0;

    private GamePanel gp;

    int numPlates = 100;
    int width = 160;
    int height = 90;
    int drawScale = 10;

    private double[][] plateCenters = new double[numPlates][2];
    private double[][] plateVelocities = new double[numPlates][2]; //Between -1.0 and 1.0
    private int[][] plateColors = new int[numPlates][3];
    private int[][] cellGrid = new int[width][height];

    public Game(GamePanel gp) {
        this.gp = gp;

        for (int i = 0; i < numPlates; i++) {
            plateCenters[i][0] = Math.random() * width;
            plateCenters[i][1] = Math.random() * height;

            plateVelocities[i][0] = (Math.random() * 2 - 1) * 0.1;
            plateVelocities[i][1] = (Math.random() * 2 - 1) * 0.1;

            plateColors[i][0] = (int) (Math.random() * 255);
            plateColors[i][1] = (int) (Math.random() * 255);
            plateColors[i][2] = (int) (Math.random() * 255);
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cellGrid[x][y] = nearestPlate(x, y);
            }
        }
    }

    public void update() {
        for (int i = 0; i < numPlates; i++) {
            plateCenters[i][0] += plateVelocities[i][0];
            plateCenters[i][1] += plateVelocities[i][1];

            if (plateCenters[i][0] < 0) {
                plateCenters[i][0] = 0;
                plateVelocities[i][0] *= -1;
            }
            if (plateCenters[i][0] >= width) {
                plateCenters[i][0] = width-1;
                plateVelocities[i][0] *= -1;
            }
            if (plateCenters[i][1] < 0) {
                plateCenters[i][1] = 0;
                plateVelocities[i][1] *= -1;
            }
            if (plateCenters[i][1] >= height) {
                plateCenters[i][1] = height-1;
                plateVelocities[i][1] *= -1;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cellGrid[x][y] = nearestPlate(x, y);
            }
        }
        
        updates++;
    }

    public void draw(Graphics2D g2, double GS) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g2.setColor(new Color(plateColors[cellGrid[x][y]][0], plateColors[cellGrid[x][y]][1], plateColors[cellGrid[x][y]][2]));
                g2.fillRect(x*drawScale, y*drawScale, drawScale, drawScale);
            }
        }

        for (int i = 0; i < numPlates; i++) {
            g2.setColor(new Color(0, 0, 0));
            g2.fillOval((int) (plateCenters[i][0]*drawScale-1), (int) (plateCenters[i][1]*drawScale-1), drawScale+2, drawScale+2);
            g2.setColor(new Color(255-plateColors[i][0], 255-plateColors[i][1], 255-plateColors[i][2]));
            g2.fillOval((int) (plateCenters[i][0]*drawScale), (int) (plateCenters[i][1]*drawScale), drawScale, drawScale);
        }
    }

    public int nearestPlate(int x, int y) {
        int nearestPlate = 0;
        double nearestDistance = 1000000000;
        for (int i = 0; i < numPlates; i++) {
            double distance = Math.sqrt(Math.pow(x - plateCenters[i][0], 2) + Math.pow(y - plateCenters[i][1], 2));
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPlate = i;
            }
        }
        return nearestPlate;
    }

    public int getCell(int x, int y) {
        return cellGrid[x][y];
    }
}
