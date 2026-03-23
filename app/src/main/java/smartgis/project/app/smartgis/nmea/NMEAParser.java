package smartgis.project.app.smartgis.nmea;

import android.location.Location;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;

import smartgis.project.app.smartgis.nmea.basic.ArrivalQuality;
import smartgis.project.app.smartgis.nmea.basic.BasicNMEAHandler;
import smartgis.project.app.smartgis.nmea.basic.BasicNMEAParser;
import smartgis.project.app.smartgis.nmea.basic.ModeIndicator;

public class NMEAParser implements BasicNMEAHandler {
    public static final String LOCATION_PROVIDER_NAME = "nmea-parser";
    private static final int SATELLITES_COUNT = 24;
    private final NMEAHandler handler;
    private final BasicNMEAParser basicParser;
    private final LocationFactory locationFactory;
    private Location location;
    private FixQuality fixQuality;
    private long lastTime;
    private int flags;
    private int satellitesCount;
    private GpsSatellite[] tempSatellites = new GpsSatellite[SATELLITES_COUNT];
    private Set<Integer> activeSatellites;

    public NMEAParser(NMEAHandler handler) {
        this(handler, new LocationFactory() {
            @Override
            public Location newLocation() {
                return new Location(LOCATION_PROVIDER_NAME);
            }
        });
    }

    public NMEAParser(NMEAHandler handler, LocationFactory locationFactory) {
        this.handler = handler;
        this.locationFactory = locationFactory;
        basicParser = new BasicNMEAParser(this);

        if (handler == null) {
            throw new NullPointerException();
        }
    }

    public synchronized void parse(String sentence) {
        basicParser.parse(sentence);
    }

    private void resetLocationState() {
        flags = 0;
        lastTime = 0;
    }

    private void newLocation(long time) {
        if (location == null || time != lastTime) {
            location = locationFactory.newLocation();
            resetLocationState();
        }
    }

    private boolean hasAllSatellites() {
        for (int i = 0; i < satellitesCount; i++) {
            if (tempSatellites[i] == null) {
                return false;
            }
        }

        return true;
    }

    private void yieldSatellites() {
        if (satellitesCount > 0 && hasAllSatellites() && activeSatellites != null) {
            for (GpsSatellite satellite : tempSatellites) {
                if (satellite == null) {
                    break;
                } else {
                    satellite.setUsedInFix(activeSatellites.contains(satellite.getPrn()));
                    satellite.setHasAlmanac(true); // TODO: ...
                    satellite.setHasEphemeris(true);  // TODO: ...
                }
            }

            handler.onSatellites(Arrays.asList(Arrays.copyOf(tempSatellites, satellitesCount)));

            Arrays.fill(tempSatellites, null);
            activeSatellites = null;
            satellitesCount = 0;
        }
    }

    private void newSatellite(int index, int count, int prn, float elevation, float azimuth, int snr) {
        if (count != satellitesCount) {
            satellitesCount = count;
        }

        GpsSatellite satellite = new GpsSatellite(prn);
        satellite.setAzimuth(azimuth);
        satellite.setElevation(elevation);
        satellite.setSnr(snr);

        tempSatellites[index] = satellite;
    }

    @Override
    public void onGns(long time, double latitude, double longitude, float altitude, float hdop, @NotNull ModeIndicator indicator, int satellites) {
        newLocation(time);

        location.setTime(time);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        switch (indicator) {
            case SIMULATOR:
                fixQuality = FixQuality.Simulation;
                break;
            case PRECISE:
                fixQuality = FixQuality.PPS;
                break;
            case FRTK:
                fixQuality = FixQuality.FRTK;
                break;
            case IRTK:
                fixQuality = FixQuality.IRTK;
                break;
            case AUTONOMUS:
                fixQuality = FixQuality.GPS;
                break;
            case DIFFERENTIAL:
                fixQuality = FixQuality.DGPS;
            case ESTIMATED:
                fixQuality = FixQuality.Estimated;
                break;
            case NOT_VALID:
                fixQuality = FixQuality.Invalid;
                break;
            case MANUAL_INPUT:
                fixQuality = FixQuality.Manual;
                break;
        }

        handler.onLocation(location);
      handler.quality(fixQuality, QualityOrigin.GNS, null);
    }

    @Override
    public void onGll(long time, double latitude, double longitude, @NotNull ArrivalQuality arrivalQuality, @NotNull ModeIndicator modeIndicator) {
        newLocation(time);

        location.setTime(time);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        switch (modeIndicator) {
            case AUTONOMUS:
                fixQuality = FixQuality.GPS;
                break;
            case DIFFERENTIAL:
                fixQuality = FixQuality.DGPS;
            case ESTIMATED:
                fixQuality = FixQuality.Estimated;
                break;
            case NOT_VALID:
                fixQuality = FixQuality.Invalid;
                break;
            case MANUAL_INPUT:
                fixQuality = FixQuality.Manual;
                break;
        }

      handler.onLocation(location);
      handler.quality(fixQuality, QualityOrigin.GLL, null);
    }

  @Override
  public void onGst(double hrms, double vrms, double rms) {
    Log.i("parse", "ongstevent called with data: hrmvsvrms " + hrms + " vrms " + vrms);
    handler.hrmsVrms(hrms, vrms, rms);
  }

    @Override
    public synchronized void onStart() {
        handler.onStart();
    }

    @Override
    public synchronized void onRMC(long date, long time, double latitude, double longitude, float speed, float direction) {
        newLocation(time);

        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(date | time);
        location.setSpeed(speed);
        location.setBearing(direction);

        handler.onLocation(location);
    }

    @Override
    public synchronized void onGGA(long time, double latitude, double longitude, float altitude, FixQuality quality, int satellites, float hdop) {
        newLocation(time);

        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(altitude);
        location.setAccuracy(hdop * 4.0f);

      handler.onLocation(location);
      handler.quality(quality, QualityOrigin.GGA, altitude);
    }

    @Override
    public synchronized void onGSV(int satellites, int index, int prn, float elevation, float azimuth, int snr) {
        newSatellite(index, satellites, prn, elevation, azimuth, snr);

        yieldSatellites();
    }

    @Override
    public void onGSA(FixType type, Set<Integer> prns, float pdop, float hdop, float vdop) {
        activeSatellites = prns;
        handler.hdopVdopPdop(hdop, vdop, pdop);
        yieldSatellites();
    }

    @Override
    public synchronized void onUnrecognized(String sentence) {
        handler.onUnrecognized(sentence);
    }

    @Override
    public synchronized void onBadChecksum(int expected, int actual) {
        handler.onBadChecksum(expected, actual);
    }

    @Override
    public synchronized void onException(Exception e) {
        handler.onException(e);
    }

    @Override
    public synchronized void onFinished() {
        handler.onFinish();
    }
}
