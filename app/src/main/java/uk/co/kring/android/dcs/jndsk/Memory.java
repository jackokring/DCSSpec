package uk.co.kring.android.dcs.jndsk;

public class Memory {

    static Memory m;
    static boolean correct = false;

    public Memory() {
        if(!correct) throw new JNDSKException();
    }

    public static void reuseInstance(Object object) {
        correct = true;
        if(m == null) m = new Memory();
        correct = false;
        //Recycle for new manual gc intervention
    }
}
