package main.utils.Instances;

public class TotemHolder {
    long f;
    long s;
    long t;

    public TotemHolder(long f,
                       long s,
                       long t) {
        this.f = f;
        this.s = s;
        this.t = t;
    }

    public void setS(long s) {
        this.s = s;
    }

    public void setT(long t) {
        this.t = t;
    }

    public long getF() {
        return f;
    }

    public long getS() {
        return s;
    }

    public long getT() {
        return t;
    }
}
