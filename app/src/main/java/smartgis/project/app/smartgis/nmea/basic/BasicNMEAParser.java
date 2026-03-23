package smartgis.project.app.smartgis.nmea.basic;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BasicNMEAParser {
    private static final float KNOTS2MPS = 0.514444f;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmmss", Locale.US);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyy", Locale.US);
    private static final String COMMA = ",";
    private static final String CAP_FLOAT = "(\\d*[.]?\\d+)";
    private static final String HEX_INT = "[0-9a-fA-F]";
    private static final Pattern GENERAL_SENTENCE = Pattern.compile("^\\$(\\w{5}),(.*)[*](" + HEX_INT + "{2})$");
    private static final Pattern GPGST = Pattern.compile("(\\d{6}.\\d{2}),(\\d*.\\d*),(\\d*.\\d*),(\\d*.\\d*),(\\d*.\\d*),(\\d*.\\d*),(\\d*.\\d*),(\\d*.\\d*)");
    private static final Pattern GPGLL = Pattern.compile("(\\d{4}.\\d*),([NS]),(\\d{5}.\\d*),([EW]),(\\d*[.]?\\d+),([AV]),([ADEMN])");
    private static final Pattern GPGNS = Pattern.compile("(\\d{6}.\\d{2}),(\\d{4}.\\d*),([NS]),(\\d{5}.\\d*),([EW]),([NADPRFEMS]{3}),(\\d{2}),(\\d*.\\d*),(\\d*.\\d*),(\\d*.\\d*),(\\W)");

    private static final Pattern GPRMC = Pattern.compile("(\\d{5})?" +
            "(\\d[.]?\\d*)?" + COMMA +
            regexify(Status.class) + COMMA +
            "(\\d{2})(\\d{2}[.]\\d+)?" + COMMA +
            regexify(VDir.class) + "?" + COMMA +
            "(\\d{3})(\\d{2}[.]\\d+)?" + COMMA +
            regexify(HDir.class) + "?" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            "(\\d{6})?" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            regexify(HDir.class) + "?" + COMMA + "?" +
            regexify(FFA.class) + "?");
    private static final Pattern GPGGA = Pattern.compile("(\\d{5})?" +
            "(\\d[.]?\\d*)?" + COMMA +
            "(\\d{2})(\\d{2}[.]\\d+)?" + COMMA +
            regexify(VDir.class) + "?" + COMMA +
            "(\\d{3})(\\d{2}[.]\\d+)?" + COMMA +
            regexify(HDir.class) + "?" + COMMA +
            "(\\d)?" + COMMA +
            "(\\d{2})?" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            CAP_FLOAT + "?,[M]" + COMMA +
            CAP_FLOAT + "?,[M]" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            "(\\d{4})?");
    private static final Pattern GPGSV = Pattern.compile("(\\d+)" + COMMA +
            "(\\d+)" + COMMA +
            "(\\d{2})" + COMMA +

            "(\\d{2})" + COMMA +
            "(\\d{2})" + COMMA +
            "(\\d{3})" + COMMA +
            "(\\d{2})" + COMMA +

            "(\\d{2})?" + COMMA + "?" +
            "(\\d{2})?" + COMMA + "?" +
            "(\\d{3})?" + COMMA + "?" +
            "(\\d{2})?" + COMMA + "?" +

            "(\\d{2})?" + COMMA + "?" +
            "(\\d{2})?" + COMMA + "?" +
            "(\\d{3})?" + COMMA + "?" +
            "(\\d{2})?" + COMMA + "?" +

            "(\\d{2})?" + COMMA + "?" +
            "(\\d{2})?" + COMMA + "?" +
            "(\\d{3})?" + COMMA + "?" +
            "(\\d{2})?");
    private static final Pattern GPGSA = Pattern.compile(regexify(Mode.class) + COMMA +
            "(\\d)" + COMMA +

            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +

            CAP_FLOAT + "?" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            "(\\d)");
    private static HashMap<String, ParsingFunction> functions = new HashMap<>();

    static {
        TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        functions.put("GPRMC", functionParseRmc());
        functions.put("GNRMC", functionParseRmc());

        functions.put("GPGGA", functionParseGga());
        functions.put("GNGGA", functionParseGga());

        functions.put("GPGSV", functionHandleParseGsv());
        functions.put("GNGSV", functionHandleParseGsv());

        functions.put("GPGSA", functionHandleParseGsa());
        functions.put("GNGSA", functionHandleParseGsa());

        functions.put("GPGLL", functionHandleParseGgl());
        functions.put("GNGLL", functionHandleParseGgl());

        functions.put("GPGNS", functionHandleParseGns());
        functions.put("GNGNS", functionHandleParseGns());

        functions.put("GPGST", functionHandleParseGst());
        functions.put("GNGST", functionHandleParseGst());
    }

    private static ParsingFunction functionHandleParseGns() {
        return new ParsingFunction() {
            @Override
            public boolean parse(BasicNMEAHandler handler, String sentence) throws Exception {
                return parseGns(handler, sentence);
            }
        };
    }

    private static ParsingFunction functionHandleParseGst() {
        return new ParsingFunction() {
            @Override
            public boolean parse(BasicNMEAHandler handler, String sentence) throws Exception {
                return parseGst(handler, sentence);
            }
        };
    }

    private static ParsingFunction functionHandleParseGgl() {
        return new ParsingFunction() {
            @Override
            public boolean parse(BasicNMEAHandler handler, String sentence) throws Exception {
                return parseGll(handler, sentence);
            }
        };
    }

    private static ParsingFunction functionHandleParseGsa() {
        return new ParsingFunction() {
            @Override
            public boolean parse(BasicNMEAHandler handler, String sentence) throws Exception {
                return parseGPGSA(handler, sentence);
            }
        };
    }

    private static ParsingFunction functionHandleParseGsv() {
        return new ParsingFunction() {
            @Override
            public boolean parse(BasicNMEAHandler handler, String sentence) throws Exception {
                return parseGPGSV(handler, sentence);
            }
        };
    }

    private static ParsingFunction functionParseRmc() {
        return new ParsingFunction() {
            @Override
            public boolean parse(BasicNMEAHandler handler, String sentence) throws Exception {
                return parseGPRMC(handler, sentence);
            }
        };
    }

    private static ParsingFunction functionParseGga() {
        return new ParsingFunction() {
            @Override
            public boolean parse(BasicNMEAHandler handler, String sentence) throws Exception {
                return parseGPGGA(handler, sentence);
            }
        };
    }

    private final BasicNMEAHandler handler;

    public BasicNMEAParser(BasicNMEAHandler handler) {
        this.handler = handler;

        if (handler == null) {
            throw new NullPointerException();
        }
    }

    private static boolean parseGPRMC(BasicNMEAHandler handler, String sentence) throws Exception {
        ExMatcher matcher = new ExMatcher(GPRMC.matcher(sentence));
        if (matcher.matches()) {
            long time = TIME_FORMAT.parse(matcher.nextString("time") + "0").getTime();
            Float ms = matcher.nextFloat("time-ms");
            if (ms != null) {
                time += ms * 1000;
            }
            if (Status.valueOf(matcher.nextString("status")) == Status.A) {
                double latitude = toDegrees(matcher.nextInt("degrees"),
                        matcher.nextFloat("minutes"));
                VDir vDir = VDir.valueOf(matcher.nextString("vertical-direction"));
                double longitude = toDegrees(matcher.nextInt("degrees"),
                        matcher.nextFloat("minutes"));
                HDir hDir = HDir.valueOf(matcher.nextString("horizontal-direction"));
                float speed = matcher.nextFloat("speed") * KNOTS2MPS;
                float direction = matcher.nextFloat("direction", 0.0f);
                long date = DATE_FORMAT.parse(matcher.nextString("date")).getTime();
                Float magVar = matcher.nextFloat("magnetic-variation");
                String magVarDir = matcher.nextString("direction");
                String faa = matcher.nextString("faa");

                handler.onRMC(date,
                        time,
                        vDir.equals(VDir.N) ? latitude : -latitude,
                        hDir.equals(HDir.E) ? longitude : -longitude,
                        speed,
                        direction);

                return true;
            }
        }

        return false;
    }

    private static boolean parseGPGGA(BasicNMEAHandler handler, String sentence) throws Exception {
        ExMatcher matcher = new ExMatcher(GPGGA.matcher(sentence.replace("-","")));
        //System.out.println(sentence);
        //System.out.println(GPGGA.pattern());
        if (matcher.matches()) {
            long time = TIME_FORMAT.parse(matcher.nextString("time") + "0").getTime();
            Float ms = matcher.nextFloat("time-ms");
            if (ms != null) {
                time += ms * 1000;
            }
            double latitude = toDegrees(matcher.nextInt("degrees"),
                    matcher.nextFloat("minutes"));
            //System.out.println(latitude);
            VDir vDir = VDir.valueOf(matcher.nextString("vertical-direction"));
            double longitude = toDegrees(matcher.nextInt("degrees"),
                    matcher.nextFloat("minutes"));
            HDir hDir = HDir.valueOf(matcher.nextString("horizontal-direction"));
            BasicNMEAHandler.FixQuality quality = BasicNMEAHandler.FixQuality.values()[matcher.nextInt("quality")];
            int satellites = matcher.nextInt("n-satellites");
            float hdop = matcher.nextFloat("hdop");
            float altitude = matcher.nextFloat("altitude");
            float separation = matcher.nextFloat("separation");
            Float age = matcher.nextFloat("age");
            Integer station = matcher.nextInt("station");

            handler.onGGA(time,
                    vDir.equals(VDir.N) ? latitude : -latitude,
                    hDir.equals(HDir.E) ? longitude : -longitude,
                    altitude - separation,
                    quality,
                    satellites,
                    hdop);

            return true;
        }
        return false;
    }

    private static boolean parseGPGSV(BasicNMEAHandler handler, String sentence) throws Exception {
        ExMatcher matcher = new ExMatcher(GPGSV.matcher(sentence));
        if (matcher.matches()) {
            matcher.nextInt("n-sentences");
            int index = matcher.nextInt("sentence-index") - 1;
            int satellites = matcher.nextInt("n-satellites");

            for (int i = 0; i < 4; i++) {
                Integer prn = matcher.nextInt("prn");
                Integer elevation = matcher.nextInt("elevation");
                Integer azimuth = matcher.nextInt("azimuth");
                Integer snr = matcher.nextInt("snr");

                if (prn != null) {
                    handler.onGSV(satellites, index * 4 + i, prn, elevation, azimuth, snr);
                }
            }

            return true;
        }
        return false;
    }

    private static boolean parseGPGSA(BasicNMEAHandler handler, String sentence) {
        ExMatcher matcher = new ExMatcher(GPGSA.matcher(sentence));
        if (matcher.matches()) {
            Mode mode = Mode.valueOf(matcher.nextString("mode"));
            BasicNMEAHandler.FixType type = BasicNMEAHandler.FixType.values()[matcher.nextInt("fix-type")];
            Set<Integer> prns = new HashSet<>();
            for (int i = 0; i < 12; i++) {
                Integer prn = matcher.nextInt("prn");
                if (prn != null) {
                    prns.add(prn);
                }
            }
            float pdop = matcher.nextFloat("pdop");
            float hdop = matcher.nextFloat("hdop");
            float vdop = matcher.nextFloat("vdop");

            handler.onGSA(type, prns, pdop, hdop, vdop);

            return true;
        }
        return false;
    }

    private static boolean parseGst(BasicNMEAHandler handler, String sentence) {
        ExMatcher matcher = new ExMatcher(GPGST.matcher(sentence));
        Log.i("parse", "got gst sentence " + sentence);
        if (matcher.matches()) {
            Log.i("parse", sentence + " is matching with the regex");
            matcher.nextString("time");
            float rms = matcher.nextFloat("rms");
            //std = standar deviation
            matcher.nextString("smjr std");
            matcher.nextString("smnr std");
            matcher.nextString("orient");
            float latError = matcher.nextFloat("lat-std");
            float longError = matcher.nextFloat("long-std");
            matcher.nextString("alt-std");
            Log.i("parse", "calling ongst event");
            handler.onGst(latError, longError, rms);
            return true;
        }
        return false;
    }

    private static boolean parseGll(GGLHandler handler, String sentence) {
        ExMatcher matcher = new ExMatcher(GPGLL.matcher(sentence));
        if (matcher.matches()) {
            String latPartition = matcher.nextString("lat-partition");
            String latDir = matcher.nextString("lat-dir");
            String longPartition = matcher.nextString("long-partition");
            String longDir = matcher.nextString("long-dir");
            String time = matcher.nextString("time");
            String arrivalStatus = matcher.nextString("ar-status");
            String modeIndicator = matcher.nextString("mode-indicator");
            int latAngle = Integer.parseInt(latPartition.substring(0, 2));
            float lat = Float.parseFloat(latPartition.substring(2));
            double latitude = toDegrees(latAngle, lat);

            int longAngle = Integer.parseInt(longPartition.substring(0, 3));
            float lng = Float.parseFloat(longPartition.substring(3));
            double longitude = toDegrees(longAngle, lng);

            ArrivalQuality quality = ArrivalQuality.VOID;
            if (arrivalStatus.equalsIgnoreCase("a")) quality = ArrivalQuality.PERPENDICULAR;
            ModeIndicator indicator;
            switch (modeIndicator) {
                case "A":
                    indicator = ModeIndicator.AUTONOMUS;
                    break;
                case "D":
                    indicator = ModeIndicator.DIFFERENTIAL;
                    break;
                case "E":
                    indicator = ModeIndicator.ESTIMATED;
                    break;
                case "M":
                    indicator = ModeIndicator.MANUAL_INPUT;
                    break;
                default:
                    indicator = ModeIndicator.NOT_VALID;
            }

            if (latDir.equalsIgnoreCase("s")) latitude = -latitude;
            if (longDir.equalsIgnoreCase("w")) longitude = -longitude;

            long lTime = 0;
            try {
                lTime = TIME_FORMAT.parse(time).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            handler.onGll(lTime, latitude, longitude, quality, indicator);
            return true;
        }
        return false;
    }

    private static boolean parseGns(BasicNMEAHandler handler, String sentence) {
        ExMatcher matcher = new ExMatcher(GPGNS.matcher(sentence));
        Log.i("parse", "try handling sentence on gns " + sentence);
        if (matcher.matches()) {
            String time = matcher.nextString("time");
            String latPartition = matcher.nextString("lat");
            String latDir = matcher.nextString("latdir");
            String longPartition = matcher.nextString("long");
            String longDir = matcher.nextString("longdir");
            String modeIndicator = matcher.nextString("mode-indicator").substring(0, 1);
            int gpsses = matcher.nextInt("number-of-gps");
            float hdop = matcher.nextFloat("hdop");
            float altitude = matcher.nextFloat("altitude");
            float separation = matcher.nextFloat("Geoidal separation (in meters)");
            matcher.nextString("Age of differential corrections, in seconds");

            Log.i("parse", "translating indicator " + modeIndicator);
            ModeIndicator indicator = ModeIndicator.NOT_VALID;
            switch (modeIndicator) {
                case "A":
                    indicator = ModeIndicator.AUTONOMUS;
                    break;
                case "D":
                    indicator = ModeIndicator.DIFFERENTIAL;
                    break;
                case "P":
                    indicator = ModeIndicator.PRECISE;
                    break;
                case "F":
                    indicator = ModeIndicator.FRTK;
                    break;
                case "R":
                    indicator = ModeIndicator.IRTK;
                    break;
                case "E":
                    indicator = ModeIndicator.ESTIMATED;
                    break;
                case "M":
                    indicator = ModeIndicator.MANUAL_INPUT;
                    break;
                case "S":
                    indicator = ModeIndicator.SIMULATOR;
                    break;
            }

            Log.i("parse", "translated " + indicator.name());

            int latAngle = Integer.parseInt(latPartition.substring(0, 2));
            float lat = Float.parseFloat(latPartition.substring(2));
            double latitude = toDegrees(latAngle, lat);

            int longAngle = Integer.parseInt(longPartition.substring(0, 3));
            float lng = Float.parseFloat(longPartition.substring(3));
            double longitude = toDegrees(longAngle, lng);

            long timestamp = 0;
            try {
                timestamp = TIME_FORMAT.parse(time).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (latDir.equalsIgnoreCase("s")) latitude = -latitude;
            if (longDir.equalsIgnoreCase("w")) longitude = -longitude;

            handler.onGns(timestamp, latitude, longitude, altitude - separation, hdop, indicator, gpsses);
            return true;
        }
        return false;
    }

    private static int calculateChecksum(String sentence) throws UnsupportedEncodingException {
        byte[] bytes = sentence.substring(1, sentence.length() - 3).getBytes("US-ASCII");
        int checksum = 0;
        for (byte b : bytes) {
            checksum ^= b;
        }
        return checksum;
    }

    private static double toDegrees(int degrees, float minutes) {
        return degrees + minutes / 60.0;
    }

    private static <T extends Enum<T>> String regexify(Class<T> clazz) {
        StringBuilder sb = new StringBuilder();
        sb.append("([");
        for (T c : clazz.getEnumConstants()) {
            sb.append(c.toString());
        }
        sb.append("])");

        return sb.toString();
    }

    public synchronized void parse(String sentence) {
        if (sentence == null) {
            throw new NullPointerException();
        }

        handler.onStart();
        try {
            ExMatcher matcher = new ExMatcher(GENERAL_SENTENCE.matcher(sentence));
            //System.out.println(GENERAL_SENTENCE.pattern());
            if (matcher.matches()) {

                String type = matcher.nextString("type");
                //System.out.println(type);
                String content = matcher.nextString("content");
                int expected_checksum = matcher.nextHexInt("checksum");
                int actual_checksum = calculateChecksum(sentence);

                if (actual_checksum != expected_checksum) {
                    //System.out.println(expected_checksum);
                    handler.onBadChecksum(expected_checksum, actual_checksum);
                } else if (!functions.containsKey(type) || !functions.get(type).parse(handler, content)) {
                    handler.onUnrecognized(sentence);
                }
            } else {
                handler.onUnrecognized(sentence);
            }
        } catch (Exception e) {
            handler.onException(e);
        } finally {
            handler.onFinished();
        }
    }

    private enum Status {
        A,
        V
    }

    private enum HDir {
        E,
        W
    }

    private enum VDir {
        N,
        S,
    }

    private enum Mode {
        A,
        M
    }

    private enum FFA {
        A,
        D,
        E,
        M,
        S,
        N
    }

    private static abstract class ParsingFunction {
        public abstract boolean parse(BasicNMEAHandler handler, String sentence) throws Exception;
    }

    private static class ExMatcher {
        Matcher original;
        int index;

        ExMatcher(Matcher original) {
            this.original = original;
            reset();
        }

        void reset() {
            index = 1;
        }

        boolean matches() {
            return original.matches();
        }

        String nextString(String name) {
            return original.group(index++);
        }

        Float nextFloat(String name, Float defaultValue) {
            Float next = nextFloat(name);
            return next == null ? defaultValue : next;
        }

        Float nextFloat(String name) {
            String next = nextString(name);
            return next == null ? null : Float.parseFloat(next);
        }

        Integer nextInt(String name) {
            String next = nextString(name);
            return next == null ? null : Integer.parseInt(next);
        }

        Integer nextHexInt(String name) {
            String next = nextString(name);
            return next == null ? null : Integer.parseInt(next, 16);
        }
    }
}
