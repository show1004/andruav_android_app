package ap.andruav_ap.activities.main;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import ap.andruav_ap.Emergency;
import de.greenrobot.event.EventBus;
import ap.andruav_ap.activities.HUBCommunication;
import ap.andruav_ap.activities.remote.RemoteControlSettingGCSActivityTab;
import ap.andruav_ap.activities.settings.SettingsDrone;
import ap.andruav_ap.activities.baseview.BaseAndruavShasha;
import ap.andruav_ap.activities.data.DataShashaTab;
import ap.andruav_ap.activities.drone.IMUShasha;
import ap.andruav_ap.activities.fcb.drone.FCB_AndruavShashaL2;
import ap.andruav_ap.activities.fcb.FCB_TCPShasha;
import ap.andruav_ap.activities.fpv.FPVActivityFactory;
import ap.andruav_ap.activities.map.AndruavMapsShasha;
import ap.andruav_ap.activities.remote.RemoteControlSettingActivityTab;
import ap.andruav_ap.App;


import com.andruav.AndruavInternalCommands;
import com.andruav.AndruavFacade;

import ap.andruav_ap.helpers.CheckAppPermissions;
import ap.andruavmiddlelibrary.eventClasses.remoteControl.Event_ProtocolChanged;
import ap.andruavmiddlelibrary.LoginClient;
import ap.andruav_ap.communication.telemetry.TelemetryDroneProtocolParser;
import ap.andruav_ap.communication.telemetry.TelemetryGCSProtocolParser;
import ap.andruav_ap.communication.telemetry.TelemetryModeer;
import ap.andruav_ap.DeviceManagerFacade;

import com.andruav.event.fcb_event.Event_UDP_Proxy;
import com.andruav.event.networkEvent.EventLoginClient;
import com.andruav.event.networkEvent.EventSocketState;

import ap.andruav_ap.helpers.GUI;
import ap.andruav_ap.helpers.RemoteControl;
import com.andruav.FeatureSwitch;

import ap.andruavmiddlelibrary.preference.Preference;
import ap.andruavmiddlelibrary.preference.PreferenceValidator;
import ap.andruav_ap.R;
import ap.andruavmiddlelibrary.factory.communication.NetInfoAdapter;
import ap.andruavmiddlelibrary.factory.tts.TTS;
import ap.andruavmiddlelibrary.factory.util.GMail;
import ap.andruavmiddlelibrary.factory.tts.SoundManager;
import ap.andruavmiddlelibrary.factory.util.DialogHelper;

import com.andruav.AndruavEngine;
import com.andruav.andruavUnit.AndruavUnitBase;
import com.andruav.event.droneReport_Event.Event_GCSBlockedChanged;
import com.andruav.protocol.commands.textMessages.Andruav_2MR;

import com.andruav.AndruavSettings;
import com.andruav.interfaces.INotification;
import com.andruav.protocol.commands.textMessages.systemCommands.AndruavSystem_ConnectedCommServer;
import com.andruav.protocol.commands.textMessages.systemCommands.AndruavSystem_EnteredChatRoom;
import com.andruav.protocol.commands.textMessages.systemCommands.AndruavSystem_Ping;
import com.andruav.protocol.communication.websocket.AndruavWSClientBase;

import static com.andruav.protocol.communication.websocket.AndruavWSClientBase.SOCKETACTION_CONNECTING;
import static com.andruav.protocol.communication.websocket.AndruavWSClientBase.SOCKETSTATE_CONNECTED;
import static com.andruav.protocol.communication.websocket.AndruavWSClientBase.SOCKETSTATE_DISCONNECTED;
import static com.andruav.protocol.communication.websocket.AndruavWSClientBase.SOCKETSTATE_ERROR;
import static com.andruav.protocol.communication.websocket.AndruavWSClientBase.SOCKETSTATE_FREASH;
import static com.andruav.protocol.communication.websocket.AndruavWSClientBase.SOCKETSTATE_REGISTERED;


public class MainScreen extends BaseAndruavShasha {

    //////// Attributes

    //////  Variables
    private MainScreen Me;
    // https://developers.google.com/+/mobile/android/recommend
    // The request code must be 0 or greater.
    private final boolean pauseToExit = false;
    private Button mbtnIMU;
    private Button mbtnFPV;
    private Button mbtnConnection;
    private Button mbtnMap;
    private Button mbtnFCB;
    private Button mbtnData;
    private TextView mtxtAccessCode;
    private TextView mtxtUDPProxy;

    private Handler mhandle;

    private Menu mMenu;
    private MenuItem miConnect;

    private String text;
    private final int MSG_TEXT = 1;
    private final int MSG_ICON = 2;
    private final int MSG_BOTH = 3;
    private final int MSG_DONE = 4;

    private ProgressDialog mprogressDialog;

    private boolean onFinalConnectionSucceededCalled = false;

    /***
     * Enabled when press disconnect button
     * to prevent a connection lost error message
     */
    private boolean isDisconnect = false;
    private boolean autoConnect = false;

    /***
     * true when the app is called from a USB device.
     */
    private boolean isUSBIntent = false;
    ////// EOF ActivityMosa3ed Variables


    /////////// EOF Attributes


    //////////BUS EVENT



    public void onEvent(Event_UDP_Proxy event_protocolChanged) {

        if (!event_protocolChanged.mAndruavWe7da.IsMe()) return ;

        Message msg = new Message();
        msg.obj = event_protocolChanged;
        mhandle.sendMessageDelayed(msg, 0);
    }

    public void onEvent (final Event_GCSBlockedChanged a7adath_gcsBlockedChanged)
    {

        Message msg = new Message();
        msg.obj = a7adath_gcsBlockedChanged;
        mhandle.sendMessageDelayed(msg, 0);
    }

    public void onEvent(Event_ProtocolChanged event_protocolChanged) {

        Message msg = new Message();
        msg.obj = event_protocolChanged;
        mhandle.sendMessageDelayed(msg, 0);
    }

    public void onEvent(EventLoginClient event_LoginClient) {

        Message msg = new Message();
        msg.obj = event_LoginClient;
        mhandle.sendMessageDelayed(msg, 0);
    }


    public void onEvent(Andruav_2MR Andruav_2MR) {

        // sendMessageToModule all System messages and some of Communication messages
        Message msg = new Message();

        if (!Andruav_2MR.MessageRouting.equals(AndruavWSClientBase.MESSAGE_TYPE_SYSTEM)) {
            // only system message passes.
            return;
        }
        msg.obj = Andruav_2MR;
        mhandle.sendMessageDelayed(msg, 0);
    }


    public void onEvent(EventSocketState event) {
        Message msg = new Message();
        msg.obj = event;
        mhandle.sendMessageDelayed(msg, 100);
    }

    ///////////////////






    private void doProgressDialog() {

        mprogressDialog = new ProgressDialog(MainScreen.this);
        mprogressDialog.setMessage(getString(R.string.gen_step1) + getString(R.string.action_init));
        mprogressDialog.setTitle(getString(R.string.action_connect));
        mprogressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mprogressDialog.show();
    }

    protected void progressDialogSetMessage(CharSequence message) {
        if (mprogressDialog != null) {   // as we can reach this point with other messages when  the dialog is off
            // http://localhost:8080/mantis/view.php?id=30

            mprogressDialog.setMessage(message);
        }
    }


    private void exitProgressDialog() {
        //Update UI here if needed
        if (mprogressDialog != null) {
            mprogressDialog.dismiss();
        }
    }


    private void UIHandler() {

        mhandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.obj instanceof Event_ProtocolChanged)
                {
                    updateFCBButton();
                }
                else if (msg.obj instanceof Event_UDP_Proxy)
                {
                    Event_UDP_Proxy event_udp_proxy = (Event_UDP_Proxy) msg.obj;
                    if (AndruavSettings.andruavWe7daBase.isUdpProxyEnabled()) {
                        AndruavEngine.notification().Speak(getString(R.string.udp_proxy_en));
                    }
                    else
                    {
                        AndruavEngine.notification().Speak(getString(R.string.udp_proxy_dis));
                    }

                    writeUDPProxy();
                    AndruavFacade.sendUdpProxyStatus(null);

                }
                else if (msg.obj instanceof Event_GCSBlockedChanged)
                {
                    updateFCBButton();
                }
                else if (msg.obj instanceof EventLoginClient) {   //Event Login Client Handling

                    EventLoginClient eventLoginClient = (EventLoginClient) msg.obj;
                    switch (eventLoginClient.LastError) {
                        case LoginClient.ERR_SUCCESS:
                            exitProgressDialog();
                            AndruavSettings.Account_SID = eventLoginClient.Parameters.get(LoginClient.CONST_SENDER_ID);
                            AndruavSettings.andruavWe7daBase.setPermissions(eventLoginClient.Parameters.get(LoginClient.CONST_PERMISSION));
                            if ((!AndruavSettings.andruavWe7daBase.canBeGCS() && AndruavSettings.andruavWe7daBase.getIsCGS())
                                || (!AndruavSettings.andruavWe7daBase.canBeDrone() && !AndruavSettings.andruavWe7daBase.getIsCGS()))
                            {
                                // cannot be in this Mode
                                progressDialogSetMessage(getString(R.string.gen_step2) + getString(R.string.err_per_bad_mode));
                                AndruavEngine.notification().Speak(getString(R.string.err_per_bad_mode));
                                return ;
                            }

                            // used when logging using authentication server.
                            // till now the only authentication server is Andruav.com
                            AndruavSettings.WebServerURL = eventLoginClient.Parameters.get(LoginClient.CONST_COMM_SERVER_PUBLIC_HOST);
                            AndruavSettings.WebServerPort = eventLoginClient.Parameters.get(LoginClient.CONST_COMM_SERVER_PORT);
                            AndruavSettings.WEBMOFTA7 = eventLoginClient.Parameters.get(LoginClient.CONST_COMM_SERVER_LOGIN_TEMP_KEY);

                            progressDialogSetMessage(getString(R.string.gen_step2) + getString(R.string.gen_accesscodevalid));
                            AndruavEngine.notification().Speak(getString(R.string.gen_accesscodevalid));
                            startAndruavConnection();
                            writeInfoLabel();
                            break;
                        case LoginClient.ERR_OLD_APP_VERSION:
                            exitProgressDialog();
                            AndruavEngine.notification().Speak(getString(R.string.err_old_app_version));
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                            break;
                        case LoginClient.ERR_SUCCESS_DISPLAY_MESSAGE:
                            exitProgressDialog();
                            AndruavEngine.notification().Speak(eventLoginClient.LastMessage);
                            DialogHelper.doModalDialog(Me, "Login", eventLoginClient.LastMessage, null);
                            break;
                        case LoginClient.ERR_SERVER_UNREACHABLE:
                            mhandle.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    exitProgressDialog();
                                    if (pauseToExit)
                                    {
                                        return;
                                    }
                                    doLogin(true);

                                }
                            },1000);
                            break;
                        default:
                            Intent intent = new Intent(Me, GUI.getLoginActivity());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra(LoginClient.CONST_ERROR, (byte) LoginClient.ERR_ACCOUNT_NOT_FOUND);
                            startActivity(intent);
                            break;
                    }

                }   // EOF EventLoginClient
                else if (msg.obj instanceof EventSocketState) {
                    EventSocketState eventSocketState = (EventSocketState) msg.obj;
                    if (eventSocketState.SocketState == EventSocketState.ENUM_SOCKETSTATE.onConnect) {
                        updateConnectionIconsStatus(AndruavEngine.getAndruavWSStatus(), AndruavEngine.getAndruavWSAction());
                        progressDialogSetMessage(getString(R.string.gen_step3) + getString(R.string.gen_hostfound));
                    } else if (eventSocketState.SocketState == EventSocketState.ENUM_SOCKETSTATE.onDisconnect) {
                        exitProgressDialog();
                        updateConnectionIconsStatus(AndruavEngine.getAndruavWSStatus(), AndruavEngine.getAndruavWSAction());


                    } else if (eventSocketState.SocketState == EventSocketState.ENUM_SOCKETSTATE.onError) {
                        exitProgressDialog();
                        updateConnectionIconsStatus(AndruavEngine.getAndruavWSStatus(), AndruavEngine.getAndruavWSAction());


                    } else if (eventSocketState.SocketState == EventSocketState.ENUM_SOCKETSTATE.onMessage) {

                    }
                } else if (msg.obj instanceof Andruav_2MR) {
                    //onFinalConnectionSucceededCalled = false;
                    Andruav_2MR eventWSComm = (Andruav_2MR) msg.obj;

                    if ((eventWSComm.IsReceived) && (eventWSComm.MessageRouting.equals(AndruavWSClientBase.MESSAGE_TYPE_SYSTEM))) { // Pass system messages only as this part is used for login.
                        if (eventWSComm.andruavMessageBase instanceof AndruavSystem_ConnectedCommServer) {
                            onFinalConnectionSucceededCalled = false;
                            if (!eventWSComm.IsErr) {
                                text = getString(R.string.gen_step4) + getString(R.string.gen_hostfound);
                            } else {
                                text = "<br><font color=#F75050>" + getString(R.string.err_hostnotfound) + "</font>";
                                AndruavEngine.notification().Speak(getString(R.string.gen_connectionlost));
                            }
                            progressDialogSetMessage(Html.fromHtml(text));
                            if (!eventWSComm.IsErr) {
                                text += "<br><font color=#75A4D3>" + getString(R.string.gen_ws_registered) + "</font'>";
                                updateConnectionIconsStatus(AndruavEngine.getAndruavWSStatus(),AndruavEngine.getAndruavWSAction());
                                AndruavEngine.getAndruavWS().sendPing();

                            } else {
                                text += "<br><font color=#F75050>" + getString(R.string.err_ws_replicatedid) + "</font'>";
                                AndruavEngine.notification().Speak(getString(R.string.err_ws_replicatedid));
                                AndruavEngine.getAndruavWS().disconnect();
                            }

                            progressDialogSetMessage(Html.fromHtml(text));
                            return;

                        }

                        //text = "<br><font color=#36AB36>" + eventWSComm.GroupName + "</font>.<font color=#75A4D3>" + eventWSComm.PartyID + "</font>.";
                        text = "<br><font color=#36AB36>" + eventWSComm.groupName + "</font>.<font color=#75A4D3>" + AndruavSettings.andruavWe7daBase.UnitID + "</font>.";

                        if ((eventWSComm.andruavMessageBase instanceof AndruavSystem_EnteredChatRoom)) {
                            if (!eventWSComm.IsErr) {
                                text += "<br><font color=#75A4D3>" + getString(R.string.gen_ws_registered) + "</font'>";
                                updateConnectionIconsStatus(AndruavEngine.getAndruavWSStatus(),AndruavEngine.getAndruavWSAction());
                                AndruavEngine.getAndruavWS().sendPing();

                            } else {
                                text += "<br><font color=#F75050>" + getString(R.string.err_ws_replicatedid) + "</font'>";
                                AndruavEngine.notification().Speak(getString(R.string.err_ws_replicatedid));
                                AndruavEngine.getAndruavWS().disconnect();
                            }

                            progressDialogSetMessage(Html.fromHtml(text));

                        } else if (eventWSComm.andruavMessageBase instanceof AndruavSystem_Ping) {
                            if (!eventWSComm.IsErr) {


                                text += "<br><font color=#75A4D3>" + "msg duration: " + eventWSComm.timeStamp + "ms" + "</font'>";
                                Toast.makeText(Me, "Ping time " + eventWSComm.timeStamp + "ms", Toast.LENGTH_LONG).show();
                                if (!onFinalConnectionSucceededCalled) onFinalConnectionSucceeded();
                                /* always start it as u may want to track GCS in follow me forexample
                                if (App.isCGS == false) {
                                    App.startSensorService();
                                }*/
                            } else {
                                text += "<br><font color=F75050>" + getString(R.string.err_ws_cmd_ping) + "</font'>";
                            }

                            progressDialogSetMessage(Html.fromHtml(text));

                            exitProgressDialog();

                        }
                    }  // end IsRecieved = true
                }
            }
        };
    }

    /***
     * Called after Register and successful ping
     */
    private void onFinalConnectionSucceeded() {
        App.nextTimeEvent =0; //reset
        App.isDone =false; //reset
        onFinalConnectionSucceededCalled = true;
        AndruavSettings.andruavWe7daBase.setShutdown(false);
        AndruavFacade.broadcastID(); // tell them I am online
        AndruavFacade.requestID();  // guys !! who are there ?
        AndruavFacade.sendID((AndruavUnitBase)null); // guys I am here
        App.startSensorService();



        if (AndruavSettings.andruavWe7daBase.getIsCGS())
        {
            ap.andruavmiddlelibrary.Voting.onConnectToServer();


        }
        else
        {
            AndruavSettings.loadGenericPermanentTasks();
            AndruavSettings.loadMyPermanentTasksByPartyID();
        }

        if (Preference.isAutoFCBConnect(null))
        {
            TelemetryModeer.connectToPreferredConnection(Me,false);
        }
    }


    private void init() {
    }

    private void initGUI() {


        if (isInEditMode) return;

        mbtnIMU = findViewById(R.id.btnIMU);
        mbtnIMU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                startActivity(new Intent(MainScreen.this, IMUShasha.class));
            }
        });
        mbtnFPV = findViewById(R.id.btnFPV);
        mbtnFPV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                FPVActivityFactory.startFPVActivity(MainScreen.this);

            }
        });
        mbtnConnection = findViewById(R.id.btnCommServer);
        mbtnConnection.setOnClickListener(arg0 -> startActivity(new Intent(MainScreen.this, HUBCommunication.class)));

        mbtnMap = findViewById(R.id.btnMap);
        mbtnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(MainScreen.this, AndruavMapsShasha.class));
            }
        });

        mbtnFCB = findViewById(R.id.btnFCB);
        mbtnFCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if (!AndruavSettings.andruavWe7daBase.getIsCGS()) {
                    if ((App.telemetryProtocolParser instanceof TelemetryGCSProtocolParser))
                    {
                        App.telemetryProtocolParser.shutDown();
                        App.telemetryProtocolParser = null;
                    }

                    startActivity(new Intent(MainScreen.this, FCB_AndruavShashaL2.class));
                }

            }
        });




        mbtnData = findViewById(R.id.btnData);
        mbtnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Emergency) AndruavEngine.getEmergency()).sendSMSLocation("+201029000028");

                startActivity(new Intent(MainScreen.this, DataShashaTab.class));
            }
        });


        mtxtAccessCode = findViewById(R.id.mainactivity_txtAccessCode);
        mtxtUDPProxy = findViewById(R.id.mainactivity_txtUdpProxy);
        writeInfoLabel();
        writeUDPProxy();

        // Define UI Handler
        UIHandler();


        View parentLayout = findViewById(android.R.id.content);
        App.notification.showSnack(INotification.NOTIFICATION_TYPE_NORMAL, "Info", "Andruav version: " + App.versionName);

        //Toast.makeText(this, "Andruav version:" + App.versionName  , Toast.LENGTH_LONG).show();

        // Snackbar.make(this, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private void writeInfoLabel() {
        mtxtAccessCode.setText(Html.fromHtml(String.format("<font color=#75A4D3><b>access code: </b></font><font color=#36AB36>%s</font>", GUI.writeTextAccessCode())));
    }

    private void writeUDPProxy() {
        mtxtUDPProxy.setText(Html.fromHtml(GUI.writeUdpProxy()));
    }




    /**
     * Login to AndruavAuthenticator
     * @param Connect
     */
    protected void doLogin(boolean Connect) {
        Log.d("ws","doLogin");

        onFinalConnectionSucceededCalled = false;

        /* GARBAGE http://www.scriptscoop.net/t/9fc9e0b24f55/how-can-i-turn-off-on-4g-data-programmatically-on-android.html
        try {


            final ConnectivityManager conman = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("isNetworkTypeValid", Integer.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        //final JSch jsch = new JSch();
        //SshConnection ssh = new SshConnection("192.168.1.2","ss","ss",null);
        if (!AndruavSettings.andruavWe7daBase.getIsCGS()) {
            RemoteControl.loadDualRates();
        }



//        if (DeviceManagerFacade.isRunningOnEmulator()) {
//            AndruavEngine.log().logException(AndruavSettings.Account_SID, "emulator", new Exception("I am on a simulator"));
//
//        }

        if (AndruavEngine.isAndruavWSStatus(SOCKETSTATE_REGISTERED)) {
            return; // I am already connected.
        }


        if (!PreferenceValidator.isValidWebRTC())
        {
            DialogHelper.doModalDialog(this, getString(R.string.pref_gr_fpv_stunserver), getString(R.string.err_rtc_nostunserver), null,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            doSettings();
                        }
                    });
            return;
        }



        // Test IP connection Including Tethering
        NetInfoAdapter.Update();

        if (!NetInfoAdapter.isConnected()) {   // No Internet Access
            Log.d("ws","isConnected is false");
            DialogHelper.doModalDialog(this, getString(R.string.gen_connection), getString(R.string.err_no_internet), null);
            return;
        }

        if (Connect) {
            Log.d("ws","isConnected Connect");

            AndruavInternalCommands.init();
            isDisconnect = false;

            if (PreferenceValidator.isInvalidLoginCode()) {
                // you need internt connection here to register for the first time
                if ((!FeatureSwitch.IGNORE_NO_INTERNET_CONNECTION) && (!NetInfoAdapter.isHasValidIPAddress())) {   // No Internet Access
                    DialogHelper.doModalDialog(this, getString(R.string.gen_connection), getString(R.string.err_no_internet), null);
                    //   AndruavMo7arek.log().log(AndruavSettings.AccessCode, "No-Net", NetInfoAdapter.DebugStr);

                    return;
                }

                startActivity(new Intent(MainScreen.this, GUI.getLoginActivity()));

                return;
            }


            // Connect Globally
            try {
                AndruavSettings.AuthIp = Preference.getAuthServerURL(null);
                AndruavSettings.AuthPort =Preference.getAuthServerPort(null);

                LoginClient.ValidateAccount(Preference.getLoginUserName(null), Preference.getLoginAccessCode(null),Preference.getWebServerGroupName(null), null);

                doProgressDialog();
            } catch (UnsupportedEncodingException e) {
                AndruavEngine.log().logException("exception_log", e);
                DialogHelper.doModalDialog(this, getString(R.string.action_login), getString(R.string.err_loginfailed), null);
            }

        }
    }

    /***
     * logout from AndruavAuthenticator & AndruavServer
     */
    protected void doLogout() {

        isDisconnect = true;
        AndruavSettings.andruavWe7daBase.setShutdown(true);
        App.stopAndruavWS(false);

        if (AndruavSettings.andruavWe7daBase.getIsCGS())
        {
            ap.andruavmiddlelibrary.Voting.onDisconnecFromServerSafely();
        }

        AndruavSettings.andruavWe7daBase.getMohemmaMapBase().clear();
        AndruavEngine.getAndruavWe7daMapBase().clear();
        App.stopSensorService();
    }

    protected void updateConnectionIconsStatus(final int status, final int  action) {
        if (miConnect == null) return; // issue 42
        updateButtonsStatus(status, action);
        switch (status) {
            case SOCKETSTATE_CONNECTED:
                miConnect.setIcon(R.drawable.connected_w_32x32);
              break;
            case SOCKETSTATE_DISCONNECTED:
                miConnect.setIcon(R.drawable.connect_w_32x32);
                AndruavEngine.notification().Speak(getString(R.string.gen_connectiondisconnected));
                break;
            case SOCKETSTATE_ERROR:
                if (!isDisconnect) {
                    miConnect.setIcon(R.drawable.connected_error_32x32);
               } else {
                    miConnect.setIcon(R.drawable.connect_w_32x32);
                    AndruavEngine.notification().Speak(getString(R.string.gen_connectiondisconnected));
                }
                break;
            case SOCKETSTATE_FREASH:
                miConnect.setIcon(R.drawable.connect_w_32x32);
                App.soundManager.playSound(SoundManager.SND_ERR);
                break;
            case SOCKETSTATE_REGISTERED:
                miConnect.setIcon(R.drawable.connected_color_32x32);
                break;
        }
    }

    protected void updateButtonsStatus(final int status, final int action) {

        boolean enabled = true;
        boolean BigButtonsenabled = true;

        if (action == SOCKETACTION_CONNECTING)
        {
            BigButtonsenabled = false;
            enabled = false;
            if (AndruavSettings.andruavWe7daBase.getIsCGS()) {
                mbtnFCB.setEnabled(false);
            }

        }
        else {

            switch (status) {
                case SOCKETSTATE_CONNECTED:
                    BigButtonsenabled = false;
                    enabled = false;
                    if (AndruavSettings.andruavWe7daBase.getIsCGS()) {
                        mbtnFCB.setEnabled(false);
                    }
                    break;
                case SOCKETSTATE_REGISTERED:
                    enabled = false;
                    BigButtonsenabled = true;
                    if (AndruavSettings.andruavWe7daBase.getIsCGS()) {
                        mbtnFCB.setEnabled(AndruavSettings.andruavWe7daBase.canTelemetry());
                    }

                    break;

                case SOCKETSTATE_ERROR:
                    enabled = true;
                    BigButtonsenabled = false;
                    if (AndruavSettings.andruavWe7daBase.getIsCGS()) {
                        mbtnFCB.setEnabled(false);
                    }
                    break;
                case SOCKETSTATE_FREASH:
                    if (AndruavSettings.andruavWe7daBase.getIsCGS()) {
                        mbtnFCB.setEnabled(false);
                    }
                    enabled = true;
                    BigButtonsenabled = true;
                    break;
                case SOCKETSTATE_DISCONNECTED:
                    enabled = true;
                    BigButtonsenabled = true;
                    if (AndruavSettings.andruavWe7daBase.getIsCGS()) {
                        mbtnFCB.setEnabled(false);
                    }
                    break;
            }

        }
        mMenu.findItem(R.id.mi_main_ResetFactory).setEnabled(enabled);
        mMenu.findItem(R.id.mi_main_signout).setEnabled(enabled);

        mbtnConnection.setEnabled(BigButtonsenabled && enabled);

        mbtnData.setEnabled(BigButtonsenabled);
        if (!AndruavSettings.andruavWe7daBase.getIsCGS()) {
            mbtnFCB.setEnabled(BigButtonsenabled);
            mbtnIMU.setEnabled(BigButtonsenabled && enabled);
            mbtnFPV.setEnabled(BigButtonsenabled);

            mMenu.findItem(R.id.mi_main_Settings_drone).setEnabled(enabled);
        }
        else
        {

            mbtnIMU.setEnabled(false);
            mbtnFPV.setEnabled(false);

            mMenu.findItem(R.id.mi_main_Settings_drone).setEnabled(false);
        }
        mbtnMap.setEnabled(BigButtonsenabled);



        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // a Lollypop Issue http://stackoverflow.com/questions/26958909/why-is-my-button-text-coerced-to-all-caps-on-lollipop
            mbtnIMU.setTransformationMethod(null);
        }
    }

    protected void updateFCBButton() {
        if ((App.isSocketListenerRunning()  && AndruavSettings.andruavWe7daBase.getIsCGS()))
        {
            if (AndruavSettings.remoteTelemetryAndruavWe7da.getisGCSBlockedFromBoard())
            {
                // Traffic is BLOCKED
                mbtnFCB.setBackgroundResource(R.drawable.big_button_shape_error);
            }
            else
            if (!AndruavSettings.andruavWe7daBase.canTelemetry())
            {
                mbtnFCB.setBackgroundResource(R.drawable.big_button_shape_disabled);
            }
            else
            {
                mbtnFCB.setBackgroundResource(R.drawable.big_button_shape_active);
            }
        } else
        if  ((TelemetryModeer.getConnectionInfo() != TelemetryModeer.CURRENTCONNECTION_NON) && !AndruavSettings.andruavWe7daBase.getIsCGS())
        {
            if (AndruavSettings.andruavWe7daBase.getisGCSBlockedFromBoard())
            {
                // Traffic is BLOCKED
                mbtnFCB.setBackgroundResource(R.drawable.big_button_shape_error);
            }
            else
            {
                mbtnFCB.setBackgroundResource(R.drawable.big_button_shape_active);
            }
        }
        else
        {
            mbtnFCB.setBackgroundResource(R.drawable.sel_big_button_color);
        }

    }

    protected void doSignOut() {


        App.stopAndruavWS(false);
        startActivity(new Intent(MainScreen.this, GUI.getLoginActivity()));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        //registerReceiver(mUsbAttachReceiver , filter);
        //filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        //registerReceiver(mUsbDetachReceiver , filter);

        Me = this;


        //http://stackoverflow.com/questions/23703778/exit-android-application-programmatically
        final Intent localIntent = getIntent();
        if (localIntent.getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }


        if (CheckAppPermissions.isPermissionsOK(Me)) {
            ((App)(getApplication())).initSignalMonitor();
        }
        isUSBIntent = localIntent.getBooleanExtra("USBConnect", false);


       /* Bundle b= getIntent().getExtras();
        if (b!= null) {
            String Error = b.getString("Error");
            if (Error != null) {
                Log.e(App.TAG, "Report:" + Error);
                GMail.sendGMail(this, "Please Report Error Via GMAIL", "rcmobilestuff@gmail.com", "Error Report", Error.toString());
            }
        }
        */
        if (App.MainIntent == null) {
            App.MainIntent = new Intent(this, MainScreen.class);
            App.MainPendingIntent = PendingIntent.getActivity(this, 0, App.MainIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        }


        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        disableButtonBar();
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs


        ////// Set Content View Here
        AndruavEngine.notification().Speak(getString(R.string.hello_world));
        init();
        initGUI();





        if ((getIntent().getBooleanExtra("autoconnect",false)  || Preference.isAutoStart(null)) && (!Preference.isGCS(null)))
        {

            autoConnect = true;
            Log.d("ac","autoconnect onCreate  true");
        }
        else {
            // login to test if there is an account.
            doLogin(false);  // true makes a bg
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void disableButtonBar() {
        try {
            // For the main activity, make sure the app icon in the action bar
            // does not behave as a button
            androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) return;
            actionBar.setHomeButtonEnabled(false);
        } catch (Throwable I) {
            // https://github.com/google/iosched/issues/79 SAMSUNG ISSUE
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        miConnect = mMenu.findItem(R.id.action_main_wsconnect);
        miConnect.setIcon(App.gui_ConnectionIconID);

        if (DeviceManagerFacade.canBeDroneAndruav()) {
            activateDroneMode();
        } else {
            final String msg = getString(R.string.err_config_drone);
            AndruavEngine.notification().Speak(msg);
            DialogHelper.doModalDialog(this, "Limitation", msg, null);
        }

        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

        TTS.getInstance().muteTTS = true; // we dont want to speak of update status of buttons
        updateConnectionIconsStatus(AndruavEngine.getAndruavWSStatus(), AndruavEngine.getAndruavWSAction());
        TTS.getInstance().muteTTS = false;

        return  super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {


        final  MenuItem mi_wsconnect = item;


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_main_wsconnect) {

            mi_wsconnect.setEnabled(false);
            this.mhandle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mi_wsconnect.setEnabled(true);
                }
            },1500);


            final int status = AndruavEngine.getAndruavWSStatus();
            final int action = AndruavEngine.getAndruavWSAction();

            if ((status == SOCKETSTATE_CONNECTED) || (status == SOCKETSTATE_REGISTERED))
            {
                doLogout();
            }
            else  if ((status == SOCKETSTATE_DISCONNECTED) || (status == SOCKETSTATE_FREASH))
            {

                doLogin(true);

            }
            else  if ((status == SOCKETSTATE_ERROR) || (action == SOCKETACTION_CONNECTING))
            {
                doLogout();
                EventBus.getDefault().post(new EventSocketState(EventSocketState.ENUM_SOCKETSTATE.onDisconnect, "manual closing"));
            }

        } else if (id == R.id.mi_main_signout) {
            doSignOut();
        } else if (id == R.id.mi_main_Exit) {
            doExit();
        } else if (id == R.id.mi_main_Settings_drone) {
            doSettings_Drone();
        } else if (id == R.id.mi_main_ResetFactory) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Me);
            builder.setMessage(getString(R.string.conf_factoryReset))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Preference.FactoryReset(null);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();


        } else if (id == R.id.mi_main_Help) {
            GMail.sendGMail(this, getString(R.string.email_title), getString(R.string.email_to), getString(R.string.email_subject), getString(R.string.email_body), null);

        } else if (id == R.id.mi_remotesettings) {
            if (AndruavSettings.andruavWe7daBase.getIsCGS()) {
                startActivity(new Intent(MainScreen.this, RemoteControlSettingGCSActivityTab.class));
            }
            else
            {
                startActivity(new Intent(MainScreen.this, RemoteControlSettingActivityTab.class));
            }
            return true;
        } else if (id == R.id.mi_main_About) {

            DialogHelper.doModalDialog(Me, getString(R.string.gen_about), Html.fromHtml(String.format("<font color=#75A4D3><b>version:</b></font><font color=#36AB36>%s</font><br><font color=#75A4D3><b>email:</b></font><font color=#36AB36>%s</font><br><font color=#75A4D3><b>access code:</b></font><font color=#36AB36>%s</font><br><font color=#75A4D3><b>pin code:</b></font><font color=#36AB36>%s</font><br>%s",
                    App.versionName,
                    GUI.writeTextEmail(),
                    GUI.writeTextAccessCode(),
                    AndruavSettings.andruavWe7daBase.PartyID,
                    GUI.writeUdpProxy()
            )), null);

        }
        return super.onOptionsItemSelected(item);
    }

    private void startAndruavConnection() {

        if (!PreferenceValidator.isValidWebSocket()) {
            startActivity(new Intent(MainScreen.this, HUBCommunication.class));
        }
        if (!App.isAndruavWSConnected()) {
            doProgressDialog();
            App.startAndruavWS();
            App.startAndruavSMS();
        } else {
            App.stopAndruavWS(false);
        }

    }

    protected void activateDroneMode() {


        Preference.isGCS(null, false);
        //AndruavSettings.andruavWe7daBase.IsCGS = false;
        if ((AndruavSettings.andruavWe7daBase== null) || (AndruavSettings.andruavWe7daBase.getIsCGS())) {
            // define unit if available unit is GCS or null
            App.defineAndruavUnit(false);
        }
        mbtnIMU.setEnabled(true);
        mbtnFPV.setEnabled(true);
        mbtnFCB.setEnabled(true);
        mMenu.findItem(R.id.mi_remotesettings).setVisible(true);
        // Dont Reset Vehicle Type if Connected to FCB.
        if (!AndruavSettings.andruavWe7daBase.useFCBIMU()) {
            AndruavSettings.andruavWe7daBase.setVehicleType(Preference.getVehicleType(null));
        }

        AndruavEngine.notification().Speak(getString(R.string.gen_speak_droneactivated));
    }



    protected void ToggleGCS_Drone() {

        TelemetryModeer.closeAllConnections();

        if (AndruavSettings.andruavWe7daBase.getIsCGS()) {
            // GCS & Switch to Drone
            if (DeviceManagerFacade.canBeDroneAndruav()) {
                activateDroneMode();
            } else {
                final String msg = getString(R.string.err_config_drone);
                AndruavEngine.notification().Speak(msg);
                DialogHelper.doModalDialog(this, "Device Limitation", msg, null);
            }
        }

    }


    private void doSettings_Drone() {
        startActivity(new Intent(MainScreen.this, SettingsDrone.class));
    }

    private void doSettings() {
       doSettings_Drone();
    }


    /***
     * The final call you receive before your activity is destroyed.
     * This can happen either because the activity is finishing (someone called finish() on it,
     * or because the system is temporarily destroying this instance of the activity to save space.
     * You can distinguish between these two scenarios with the isFinishing() method.
     *
     * @html http://developer.android.com/reference/android/app/Activity.html
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        App.shutDown();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")

        //http://stackoverflow.com/questions/3611457/android-temporarily-disable-orientation-changes-in-an-activity


    }

    @Override
    protected void onResume() {
        // The activity has become visible (it is now "resumed").
        super.onResume();
        //http://stackoverflow.com/questions/3611457/android-temporarily-disable-orientation-changes-in-an-activity
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        invalidateOptionsMenu();
        updateFCBButton();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        EventBus.getDefault().register(this);
        //SMS.sendSMS("01029000028","HI that is me ANdroid");

        if (AndruavSettings.andruavWe7daBase.mIsModule)
        {
            doExit(true, getString(R.string.gen_must_exit));
        }

       if (autoConnect)
        {

           // Toast.makeText(Me,"SHOULD  COnnect",Toast.LENGTH_LONG).show();
            Log.d("ac","autoconnect resume true");
            mhandle.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!NetInfoAdapter.isConnected())
                    {
                        Toast.makeText(getApplicationContext(), "no connection", Toast.LENGTH_LONG).show();
                        mhandle.postDelayed(this, 1000);
                        return ;
                    }
                    autoConnect = false; /// toggle it back
                    Log.d("ac","autoconnect doLogin Resume  true");
                    doLogin(true);

                }
            },100);
        }
        else
       {
           Log.d("ac","autoconnect resume false");
       }

        if (autoConnect)
        {

            Log.d("ac","autoconnect resume true");
            mhandle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TelemetryModeer.startAutoConnection(isUSBIntent);
                    isUSBIntent = false;

                }
            },300);
        }
        else
        {
            Log.d("ac","autoconnect resume false");
        }




       writeInfoLabel();
    }

    @Override
    protected void onPause() {
        // Another activity is taking focus (this activity is about to be "paused").
        EventBus.getDefault().unregister(this);
        mhandle.removeCallbacksAndMessages(null);
        super.onPause();


    }

    @Override
    public void onBackPressed() {
        /*
        ************ IMPORTANT
        http://stackoverflow.com/questions/23703778/exit-android-application-programmatically
        For Other forms please use this
            Intent intent = new Intent(getApplicationContext(), FirstShasha.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
         */
        doExit();
    }


    BroadcastReceiver mUsbAttachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Toast.makeText(Me,"Usb Attached",Toast.LENGTH_LONG).show();
                TelemetryModeer.connectToPreferredConnection(Me,false);
            }
        }
    };

    BroadcastReceiver mUsbDetachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Toast.makeText(Me,"Usb Detached",Toast.LENGTH_LONG).show();

            }
        }
    };
}