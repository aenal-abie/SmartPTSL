package smartgis.project.app.smartgis.nmea;

import android.location.Location;

import java.util.List;

import smartgis.project.app.smartgis.nmea.basic.BasicNMEAHandler;

public interface NMEAHandler {
    void onStart();

    void onLocation(Location location);

    void onSatellites(List<GpsSatellite> satellites);

    void onUnrecognized(String sentence);

    void onBadChecksum(int expected, int actual);

    void onException(Exception e);

    void onFinish();

    void quality(BasicNMEAHandler.FixQuality quality, BasicNMEAHandler.QualityOrigin origin, Float altitude);

  void hrmsVrms(double hrms, double vrms, double rms);

    void hdopVdopPdop(float hdop, float vdop, float pdop);
}
