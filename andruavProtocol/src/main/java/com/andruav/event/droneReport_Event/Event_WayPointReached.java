package com.andruav.event.droneReport_Event;


import com.andruav.andruavUnit.AndruavUnitBase;

/**
 * Created by mhefny on 2/20/16.
 */
public class Event_WayPointReached {

    public final AndruavUnitBase mAndruavWe7da;
    public final int mWaypointIndex;



    public Event_WayPointReached(AndruavUnitBase andruavWe7da, int wayPointIndex)
    {
        mAndruavWe7da = andruavWe7da;
        mWaypointIndex = wayPointIndex;
    }



}
