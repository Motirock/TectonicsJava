package game;

import main.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;

public class Game {
    private long updates = 0;

    private GamePanel gp;

    int mapMode = 1;
    long seed = 0;

    int numPlates = 20;
    int width = 160;
    int height = 90;
    int drawScale = 10;

    int minPlateCenterDistance = 1;

    private Plate[] plates = new Plate[numPlates];
    private Location[][] cellGrid = new Location[width][height];

    public Game(GamePanel gp) {
        this.gp = gp;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++)
                cellGrid[x][y] = new Location(x, y, 0, 0);
        }

        for (int i = 0; i < numPlates; i++) {
            plates[i] = new Plate(0, 0, 0, 0, new int[3], i%2 == 1);

            while (true) {
                double x = Math.random() * width;
                double y = Math.random() * height;

                boolean valid = true;
                for (int p = 0; p < numPlates && p < i; p++) {
                    if (Math.sqrt(Math.pow(x - plates[p].x, 2) + Math.pow(y - plates[p].y, 2)) < minPlateCenterDistance) {
                        valid = false;
                        break;
                    }
                }

                if (!valid)
                    continue;

                plates[i].x = x;
                plates[i].y = y;
                
                break;
            }

            plates[i].vx = (Math.random() * 2 - 1);
            plates[i].vy = (Math.random() * 2 - 1);

            plates[i].color[0] = (int) (Math.random() * 255);
            plates[i].color[1] = (int) (Math.random() * 255);
            plates[i].color[2] = (int) (Math.random() * 255);
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cellGrid[x][y].plateID = nearestPlate(x, y);
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (plates[cellGrid[x][y].plateID].isOceanic)
                    cellGrid[x][y].elevation = -0.5;
                else
                    cellGrid[x][y].elevation = 0.5;
                //If not matching above
                if (y > 0 && cellGrid[x][y].plateID != cellGrid[x][y-1].plateID) {
                    cellGrid[x][y].elevation += 0.5;
                }
                //If not matching below
                if (y < height-1 && cellGrid[x][y].plateID != cellGrid[x][y+1].plateID) {
                    cellGrid[x][y].elevation += 0.5;
                }
                //If not matching left
                if (x > 0 && cellGrid[x][y].plateID != cellGrid[x-1][y].plateID) {
                    cellGrid[x][y].elevation += 0.5;
                }
                //If not matching right
                if (x < width-1 && cellGrid[x][y].plateID != cellGrid[x+1][y].plateID) {
                    cellGrid[x][y].elevation += 0.5;
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (cellGrid[x][y].elevation < -1.0)
                    cellGrid[x][y].elevation = -1.0;
                if (cellGrid[x][y].elevation > 1.0)
                    cellGrid[x][y].elevation = 1.0;
            }
        }
    }

    public void update() {
        if (false) {
            for (int i = 0; i < numPlates; i++) {
                plates[i].x += plates[i].vx;
                plates[i].y += plates[i].vy;

                if (plates[i].x < 0) {
                    plates[i].x = 0;
                    plates[i].vx *= -1;
                }
                if (plates[i].x >= width) {
                    plates[i].x = width-1;
                    plates[i].vx *= -1;
                }
                if (plates[i].y < 0) {
                    plates[i].y = 0;
                    plates[i].vy *= -1;
                }
                if (plates[i].y >= height) {
                    plates[i].y = height-1;
                    plates[i].vy *= -1;
                }
            }

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    cellGrid[x][y].plateID = nearestPlate(x, y);
                }
            }
        }

        mapMode = gp.keyH.numKeyPressed;
        
        updates++;
    }

    public void draw(Graphics2D g2, double GS) {
        if (mapMode == 1) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    g2.setColor(new Color(plates[cellGrid[x][y].plateID].color[0], plates[cellGrid[x][y].plateID].color[1], plates[cellGrid[x][y].plateID].color[2]));
                    g2.fillRect(x*drawScale, y*drawScale, drawScale, drawScale);
                }
            }

            for (int i = 0; i < numPlates; i++) {
                g2.setColor(new Color(255-plates[i].color[0], 255-plates[i].color[1], 255-plates[i].color[2]));
                g2.fillOval((int) ((plates[i].x-0.5)*drawScale), (int) ((plates[i].y-0.5)*drawScale), drawScale, drawScale);
                g2.setColor(new Color(0, 0, 0));
                g2.drawLine((int) (plates[i].x*drawScale), (int) (plates[i].y*drawScale), (int) ((plates[i].x+plates[i].vx*10)*drawScale), (int) ((plates[i].y+plates[i].vy*10)*drawScale));
            }
        }
        if (mapMode == 2) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    g2.setColor(new Color((float) cellGrid[x][y].elevation/2.0f+0.5f, (float) cellGrid[x][y].elevation/2.0f+0.5f, (float) cellGrid[x][y].elevation/2.0f+0.5f));
                    g2.fillRect(x*drawScale, y*drawScale, drawScale, drawScale);
                }
            }
        }
    }

    public int nearestPlate(int x, int y) {
        int nearestPlate = 0;
        double nearestDistance = 1000000000;
        for (int i = 0; i < numPlates; i++) {
            double distance = Math.sqrt(Math.pow(x - plates[i].x, 2) + Math.pow(y - plates[i].y, 2));
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPlate = i;
            }
        }
        return nearestPlate;
    }

    public Location getCell(int x, int y) {
        return cellGrid[x][y];
    }
}
