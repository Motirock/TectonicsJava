package game;

public class Plate {
    double x, y, vx, vy;
    int[] color;
    boolean isOceanic = false;

    public Plate(double x, double y, double vx, double vy, int[] color, boolean isOceanic) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.isOceanic = isOceanic;
    }

}
