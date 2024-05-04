package observers;

import Ada.GameObject;
import observers.events.Event;

public interface Observer {
    void onNotify(GameObject object, Event event);
}
