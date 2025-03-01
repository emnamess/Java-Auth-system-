package esprit.tn.entities;

import javafx.scene.Scene;
import java.util.Stack;

public class NavigationManager {
    private static final Stack<Scene> history = new Stack<>();

    // Save the current scene before switching
    public static void pushScene(Scene scene) {
        history.push(scene);
    }

    // Get the last scene and remove it from the stack
    public static Scene popScene() {
        if (!history.isEmpty()) {
            return history.pop();
        }
        return null;
    }

    // Check if there's a previous scene
    public static boolean hasPreviousScene() {
        return !history.isEmpty();
    }
    public static int getStackSize() {
        return history.size(); // Assuming sceneStack is a Stack object
    }

}

