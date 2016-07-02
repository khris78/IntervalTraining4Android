package fr.gallioz.intervals.utils;

public class DisplayTime {
    private String val = "00:00'00\"00";
    private StringBuilder sb = new StringBuilder(val);
    private long currentTime = 0L;
    private long hours = 0L;
    private long minutes = 0L;
    private long sec = 0L;
    private long millisec = 0L;

    private boolean showMillisec = true;
    private boolean hasChanged = false;

    public synchronized void setTime(long time) {
        currentTime = time;

        if (time >= 86400000) {
            time = time % 86400000;
        }
        time = time / 10;

        setMillisec(time % 100);
        time /= 100;

        setSec(time % 60);
        time /= 60;

        setMinutes(time % 60);
        time /= 60;

        setHours(time);
    }

    public long getTime() {
        return currentTime;
    }

    public synchronized String getTimeRepresentation() {
        if (hasChanged) {
            val = sb.toString();
            hasChanged = false;
        }
        return val;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public synchronized void setShowMillisec(boolean showMillisec) {
        if (this.showMillisec != showMillisec) {
            this.showMillisec = showMillisec;
            if (showMillisec) {
                if (millisec < 10) {
                    sb.append('0');
                }
                sb.append(millisec);
            } else {
                sb.setLength(sb.length() - 2);
            }
            hasChanged = true;
        }
    }

    private void setHours(long hours) {
        if (this.hours != hours) {
            this.hours = hours;
            if (hours < 10) {
                sb.setCharAt(0, '0');
                sb.setCharAt(1, (char) ('0' + hours));
            } else {
                sb.replace(0, 2, Long.toString(hours));
            }
            hasChanged = true;
        }
    }

    private void setMinutes(long minutes) {
        if (this.minutes != minutes) {
            this.minutes = minutes;
            if (minutes < 10) {
                sb.setCharAt(3, '0');
                sb.setCharAt(4, (char) ('0' + minutes));
            } else {
                sb.replace(3, 5, Long.toString(minutes));
            }
            hasChanged = true;
        }
    }

    private void setSec(long sec) {
        if (this.sec != sec) {
            this.sec = sec;
            if (sec < 10) {
                sb.setCharAt(6, '0');
                sb.setCharAt(7, (char) ('0' + sec));
            } else {
                sb.replace(6, 8, Long.toString(sec));
            }
            hasChanged = true;
        }
    }

    private void setMillisec(long millisec) {
        if (this.millisec != millisec) {
            this.millisec = millisec;
            if (showMillisec) {
                if (millisec < 10) {
                    sb.setCharAt(9, '0');
                    sb.setCharAt(10, (char) ('0' + millisec));
                } else {
                    sb.replace(9, 11, Long.toString(millisec));
                }
                hasChanged = true;
            }
        }
    }
}
