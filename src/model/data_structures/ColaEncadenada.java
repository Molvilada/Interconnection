package model.data_structures;

public class ColaEncadenada<T extends Comparable<T>> extends ListaEncadenada<T> {
    public void enqueue(T element) {
        try {
            this.addLastCola(element);
        } catch (NullException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
