public class TestCase {
    private boolean alive;

    public TestCase(){
        Instances++;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public static int Instances;
}
