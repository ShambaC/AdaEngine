package Ada;

public class WindowListener {
    private static WindowListener instance;

    private WindowListener() {}

    public static WindowListener get() {
        if (instance == null) {
            instance = new WindowListener();
        }

        return instance;
    }

    public static void windowCallback(long window, int width, int height) {
        Window.get().setWidth(width);
        Window.get().setHeight(height);
    }
}
