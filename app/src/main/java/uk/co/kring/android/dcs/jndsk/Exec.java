package uk.co.kring.android.dcs.jndsk;

public class Exec {

    public Exec() {
        setup();
    }

    public void setup() {

    }

    public void run() {

    }

    public static class PushI extends Exec {

        public PushI(int i) {

        }

        public void run() {
            //TODO:
        }
    }

    public static class PushIC extends PushI {

        public PushIC(int i) {
            //TODO
            super(i);
        }

        public void run() {
            //TODO:
        }
    }

    public static class PushLC extends PushIC {

        public PushLC(int i) {
            //TODO
            super(i);
        }

        public void run() {
            //TODO:
        }
    }
}
