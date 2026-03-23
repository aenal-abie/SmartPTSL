package smartgis.project.app.smartgis.nmea.basic;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface BasicNMEAHandler extends GGLHandler, GSTHandler, GNSHandler {

    @Override
    void onGns(long time, double latitude, double longitude, float altitude, float hdop, @NotNull ModeIndicator indicator, int satellites);

    @Override
    void onGll(long time, double latitude, double longitude, @NotNull ArrivalQuality arrivalQuality, @NotNull ModeIndicator modeIndicator);

    @Override
    void onGst(double hrms, double vrms, double rms);

    void onStart();

    /***
     * Called on GPRMC parsed.
     *
     * @param date      milliseconds since midnight, January 1, 1970 UTC.
     * @param time      actual UTC time (without date)
     * @param latitude  angular y position on the Earth.
     * @param longitude angular x position on the Earth.
     * @param speed     in meters per second.
     * @param direction angular bearing value to the North.
     */
    void onRMC(long date, long time, double latitude, double longitude, float speed, float direction);

    /***
     * Called on GPGGA parsed.
     *
     * @param time        actual UTC time (without date)
     * @param latitude    angular y position on the Earth.
     * @param longitude   angular x position on the Earth.
     * @param altitude    altitude in meters above corrected geoid
     * @param quality     fix-quality type {@link FixQuality}
     * @param satellites  actual number of satellites
     * @param hdop        horizontal dilution of precision
     */
    void onGGA(long time, double latitude, double longitude, float altitude, FixQuality quality, int satellites, float hdop);

    /***
     * Called on GPGSV parsed.
     * Note that single nmea sentence contains up to 4 satellites therefore you can receive 4 calls per sentence.
     *
     * @param satellites total number of satellites
     * @param index      index of satellite
     * @param prn        pseudo-random noise number
     * @param elevation  elevation in degrees
     * @param azimuth    azimuth in degrees
     * @param snr        signal to noise ratio
     */
    void onGSV(int satellites, int index, int prn, float elevation, float azimuth, int snr);

    /***
     * Called on GPGSA parsed.
     *
     * @param type type of fix
     * @param prns set of satellites used for the current fix
     * @param pdop position dilution of precision
     * @param hdop horizontal dilution of precision
     * @param vdop vertical dilution of precision
     */
    void onGSA(FixType type, Set<Integer> prns, float pdop, float hdop, float vdop);

    void onUnrecognized(String sentence);

    void onBadChecksum(int expected, int actual);

    void onException(Exception e);

    void onFinished();

    enum FixQuality {
        Invalid(0, "Invalid"),
        GPS(1, "GPS Fix/Stand Alone"),
        DGPS(2, "Differential"),
        PPS(3, "PPS Fix"),
        IRTK(4, "Fixed"),
        FRTK(5, "Float RTK"),
        Estimated(6, "Estimated (Dead Reckoning)"),
        Manual(7, "Manual Input Mode"),
        Simulation(8, "Simulation Mode"),
        Autonomus(9, "Autonomus");

        public final int value;
        public final String translate;

        FixQuality(int value, String translate) {
            this.value = value;
            this.translate = translate;
        }
    }

    enum QualityOrigin {
        GGA, GNS, GLL
    }

    enum FixType {
        Invalid(0),
        None(1),
        Fix2D(2),
        Fix3D(3);

        public final int value;

        FixType(int value) {
            this.value = value;
        }
    }
}
