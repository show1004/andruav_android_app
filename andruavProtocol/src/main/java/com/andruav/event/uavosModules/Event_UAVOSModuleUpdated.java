package com.andruav.event.uavosModules;


import com.andruav.uavos.modules.UAVOSModuleUnit;

public class Event_UAVOSModuleUpdated {

    public final UAVOSModuleUnit uavosModuleUnit;


    public Event_UAVOSModuleUpdated (final UAVOSModuleUnit uavosModule)
    {
        uavosModuleUnit = uavosModule;
    }
}
