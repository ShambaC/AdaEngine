package Ada;

public class LevelEditorScene extends Scene {

    public  LevelEditorScene() {
        
    }

    @Override
    public void init() {

    }

    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
    }

}
