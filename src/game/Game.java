package game;

import main.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;

public class Game {
    private long updates = 0;

    private GamePanel gp;

    int mapMode = 1;
    long seed = 0;

    int numPlates = 50;
    int width = 800;
    int height = 450;
    int drawScale = 2;

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
                    cellGrid[x][y].elevation += getBoundaryElevationChange(cellGrid[x][y].plateID, cellGrid[x][y-1].plateID);
                }
                //If not matching below
                else if (y < height-1 && cellGrid[x][y].plateID != cellGrid[x][y+1].plateID) {
                    cellGrid[x][y].elevation += getBoundaryElevationChange(cellGrid[x][y].plateID, cellGrid[x][y+1].plateID);
                }
                //If not matching left
                else if (x > 0 && cellGrid[x][y].plateID != cellGrid[x-1][y].plateID) {
                    cellGrid[x][y].elevation += getBoundaryElevationChange(cellGrid[x][y].plateID, cellGrid[x-1][y].plateID);
                }
                //If not matching right
                else if (x < width-1 && cellGrid[x][y].plateID != cellGrid[x+1][y].plateID) {
                    cellGrid[x][y].elevation += getBoundaryElevationChange(cellGrid[x][y].plateID, cellGrid[x+1][y].plateID);
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
        if (mapMode == 3) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (cellGrid[x][y].elevation < -0.5)
                        g2.setColor(new Color(8,83,159));
                    else if (cellGrid[x][y].elevation < 0.0)
                        g2.setColor(new Color(12,124,236));
                    else if (cellGrid[x][y].elevation < 0.75)
                        g2.setColor(new Color(116,212,92));
                    else
                        g2.setColor(new Color(124,124,128));
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

    public double getBoundaryElevationChange(int firstPlateID, int secondPlateID) {
        Plate firstPlate = plates[firstPlateID];
        Plate secondPlate = plates[secondPlateID];
        if (firstPlate.isOceanic && secondPlate.isOceanic)
            return getBoundaryAngleDifference(firstPlate, secondPlate);
        if (firstPlate.isOceanic && !secondPlate.isOceanic)
            return getBoundaryAngleDifference(firstPlate, secondPlate)*2-1;
        if (!firstPlate.isOceanic && secondPlate.isOceanic) {
            return getBoundaryAngleDifference(firstPlate, secondPlate)*2-1;
        }
        if (!firstPlate.isOceanic && !secondPlate.isOceanic) {
            return Math.abs(getBoundaryAngleDifference(firstPlate, secondPlate)*2-1);
        }
        return 0;
    }

    //Return 0 to 1 depending on how close the angle between them is to 0 degrees
    public double getBoundaryAngleDifference(Plate firstPlate, Plate secondPlate) {
        double angleDifference = Math.abs(Math.atan2(firstPlate.vy, firstPlate.vx) - Math.atan2(secondPlate.vy, secondPlate.vx));
        if (angleDifference > Math.PI)
            angleDifference = Math.PI*2 - angleDifference;
        return angleDifference / Math.PI;
    }
}
