package game;

public class Location {
    public int x, y, plateID;
    public double elevation;

    public Location() {
        x = 0;
        y = 0;
        plateID = 0;
        elevation = 0;
    }

    public Location(int x, int y, int plateID, double elevation) {
        this.x = x;
        this.y = y;
        this.plateID = plateID;
        this.elevation = elevation;
    }
}
