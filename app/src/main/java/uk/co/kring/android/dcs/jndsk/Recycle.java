package uk.co.kring.android.dcs.jndsk;

//A stacked object recycler to reduce GC overheads for an easier port to nds
public class Recycle {

    static Recycle s;

    public Recycle() {
        if(s != null) {
            s = this;
        } else {
            throw new JNDSKException();
        }
    }

    public static Recycle getInstance() {
        return s;
    }

    public void forget(int marks) {
        //TODO: go back number of marks in allocations using new
        //and pack saves
    }

    public void mark() {
        //TODO: mark a collection point
    }

    public int save(Object object) {
        //TODO: make a note to save this and return index
        return 0;
    }

    public Object load(int index) {
        //TODO: return a new object pointer for a saved object
        return null;
    }
}
