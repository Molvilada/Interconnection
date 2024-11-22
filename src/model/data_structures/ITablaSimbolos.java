package model.data_structures;

public interface ITablaSimbolos<K extends Comparable<K>, V extends Comparable<V>> {
    void put(K key, V value);

    V get(K key);

    V remove(K key);

    boolean contains(K key);

    boolean isEmpty();

    int size();

    ILista<K> keySet();

    ILista<V> valueSet();

    ILista<NodoTS<K, V>> darListaNodos();


    int hash(K key);

}
