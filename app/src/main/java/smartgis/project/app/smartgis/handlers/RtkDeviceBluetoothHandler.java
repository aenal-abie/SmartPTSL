package smartgis.project.app.smartgis.handlers;

import android.util.Log;

import smartgis.project.app.smartgis.nmea.NMEAHandler;
import smartgis.project.app.smartgis.nmea.NMEAParser;

public class RtkDeviceBluetoothHandler {

    private NMEAParser parser;

    private RtkDeviceBluetoothHandler(NMEAHandler handler) {
        this.parser = new NMEAParser(handler);
    }

    public void handleMessage(String message) {
        Log.i("parsing", message);
        parser.parse(message);
    }


    private static RtkDeviceBluetoothHandler instance;

    public static RtkDeviceBluetoothHandler getInstance(NMEAHandler handler) {
        if (instance == null) instance = new RtkDeviceBluetoothHandler(handler);
        return instance;
    }

}

