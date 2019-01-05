package com.slowfrog.qwop;

public class RunInfo {

    private static final long serialVersionUID = 1L;
    final String string;
    final int delay;
    final boolean crashed;
    final boolean stopped;
    final long duration;
    final float distance;

    public RunInfo(String pstring, int pdelay, boolean pcrashed,
                   boolean pstopped, long pduration, float pdistance) {
        this.string = pstring;
        this.delay = pdelay;
        this.crashed = pcrashed;
        this.stopped = pstopped;
        this.duration = pduration;
        this.distance = pdistance;
    }

    public static RunInfo unmarshal(String line) {
        String[] parts = line.split("\\|");
        if (parts[0].equals("RunInfo#1")) {
            String string = parts[1];
            int delay = Integer.parseInt(parts[2]);
            float distance = Float.parseFloat(parts[3]);
            long duration = Long.parseLong(parts[4]);
            String resultCode = parts[5];
            boolean crashed = resultCode.equals("C");
            boolean stopped = resultCode.equals("S");
            return new RunInfo(string, delay, crashed, stopped, duration, distance);

        } else {
            throw new RuntimeException("Unknown format: " + parts[0]);
        }
    }

    private String getResultCode() {
        if (this.distance > 100) {
            return "W";
        }
        if (this.stopped) {
            return "S";
        }
        if (this.crashed) {
            return "C";
        }
        return "?";
    }

    private String getSuffix() {
        String statusCode = this.getResultCode();
        if ("W".equals(statusCode)) {
            return " and won";
        }
        if ("S".equals(statusCode)) {
            return " and was stopped";
        }
        if ("C".equals(statusCode)) {
            return " and crashed";
        }
        return "";
    }

    public String toString() {
        return "Ran " + distance + "m during " + duration + "ms" + this.getSuffix();
    }

    public String marshal() {
        return "RunInfo#" + serialVersionUID + "|" + this.string + "|" +
                this.delay + "|" + this.distance + "|" + this.duration + "|" +
                this.getResultCode();
    }

    public String getString() {
        return string;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isCrashed() {
        return crashed;
    }

    public boolean isStopped() {
        return stopped;
    }

    public long getDuration() {
        return duration;
    }

    public float getDistance() {
        return distance;
    }
}
