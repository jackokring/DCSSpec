package uk.co.kring.android.dcs.jndsk;

import java.util.LinkedList;

//A stacked object recycler to reduce GC overheads for an easier port to nds
//This is just a work-alike
public class Recycle {

    static Recycle s;
    static LinkedList<Object> saves = new LinkedList<Object>();
    static LinkedList<Integer> marks = new LinkedList<Integer>();

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

    public void forget(int markCount) {
        //TODO: go back number of marks in allocations using new
        for(int i = 0; i < markCount; ++i) {
            int index = marks.removeLast();
            while (index > saves.size()) {
                saves.removeLast();
            }
        }
    }

    public void mark() {
        //TODO: mark a collection point
        marks.add(saves.size());
    }

    public int save(Object object) {
        //TODO: make a note to save this and return index
        saves.add(object);
        return saves.size() - 1;
    }

    public Object load(int index) {
        //TODO: return a new object pointer for a saved object
        return saves.get(index);
    }
}
