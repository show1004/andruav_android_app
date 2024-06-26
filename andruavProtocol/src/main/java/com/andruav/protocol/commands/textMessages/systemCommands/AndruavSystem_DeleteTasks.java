package com.andruav.protocol.commands.textMessages.systemCommands;

import com.andruav.protocol.commands.textMessages.AndruavMessageBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by mhefny on 8/7/16.
 */
public class AndruavSystem_DeleteTasks extends AndruavMessageBase {

    public final static int AndruavSystem_DeleteTasks = 9003;


    public final int largerThan_SID;
    public final String accessCode;
    public final String accountID;
    public final String party_sid;
    public final String groupName;
    public final String sender;
    public final String receiver;
    public final String messageType;
    private String task;
    public final boolean isPermanent;


    public AndruavSystem_DeleteTasks (int largerThan_SID,
                                      String accessCode,
                                      String accountID,
                                      String party_sid,
                                      String groupName,
                                      String sender,
                                      String receiver,
                                      String messageType,
                                      boolean isPermanent) throws Exception {
        this.largerThan_SID = largerThan_SID;
        this.party_sid = party_sid;
        this.accessCode = accessCode;
        this.accountID = accountID;
        this.groupName = groupName;
        this.sender = sender;
        this.receiver = receiver;
        this.messageType = messageType;
        this.isPermanent = isPermanent;

        messageTypeID = AndruavSystem_DeleteTasks;
    }



    public String  getTask()
    {
        return task;
    }


    @Override
    public void setMessageText(String messageText) throws JSONException, ParseException {


    }


    @Override
    public String getJsonMessage () throws org.json.JSONException
    {
        //Gson gson = new Gson();
        JSONObject json_data= new JSONObject();

        if (largerThan_SID > 0)     json_data.accumulate("lts",largerThan_SID);
        if (party_sid != null)      json_data.accumulate("ps",party_sid);
        if (accessCode != null)     json_data.accumulate("ac",accessCode);
        if (accountID != null)      json_data.accumulate("ai",accountID);
        if (groupName != null)      json_data.accumulate("gn",groupName);
        if (sender != null)         json_data.accumulate("s",sender);
        if (receiver != null)       json_data.accumulate("r",receiver);
        if (messageType != null)    json_data.accumulate("mt",messageType);
        int v = 0;
        if (isPermanent) v  = 1;
        json_data.accumulate("ip",v);

        return json_data.toString();
    }
}

