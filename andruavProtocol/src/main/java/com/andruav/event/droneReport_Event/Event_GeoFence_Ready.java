package com.andruav.event.droneReport_Event;


import com.andruav.andruavUnit.AndruavUnitBase;

/**
 * Created by mhefny on 6/19/16.
 */
public class Event_GeoFence_Ready {

    public final AndruavUnitBase andruavWe7da;
    public final String fenceName;


    public Event_GeoFence_Ready(AndruavUnitBase andruavUnit, String fenceName)
    {
        andruavWe7da = andruavUnit;
        this.fenceName = fenceName;
    }
}
