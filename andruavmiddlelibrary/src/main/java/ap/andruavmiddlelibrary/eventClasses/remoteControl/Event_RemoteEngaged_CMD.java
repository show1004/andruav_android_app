package ap.andruavmiddlelibrary.eventClasses.remoteControl;

/**
 * Created by M.Hefny on 28-Mar-15.
 */
public class Event_RemoteEngaged_CMD {
    
    public final Boolean mEngaged;

    public Event_RemoteEngaged_CMD (boolean abcIcClearDisabledFocusLight)
    {
        mEngaged = abcIcClearDisabledFocusLight;
    }
}
