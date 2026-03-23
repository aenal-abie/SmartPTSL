package smartgis.project.app.smartgis.nmea;

import android.location.Location;

import java.util.List;

import smartgis.project.app.smartgis.nmea.basic.BasicNMEAHandler;

public class NMEAAdapter implements NMEAHandler {
    @Override
    public void onStart() {

    }

    @Override
    public void onLocation(Location location) {

    }

    @Override
    public void onSatellites(List<GpsSatellite> satellites) {

    }

    @Override
    public void onUnrecognized(String sentence) {

    }

    @Override
    public void onBadChecksum(int expected, int actual) {

    }

    @Override
    public void onException(Exception e) {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void quality(BasicNMEAHandler.FixQuality quality, BasicNMEAHandler.QualityOrigin origin, Float altitude) {

    }

  @Override
  public void hrmsVrms(double hrms, double vrms, double rms) {

  }


  @Override
    public void hdopVdopPdop(float hdop, float vdop, float pdop) {

    }
}
