package model.data_structures;

public class TablaHashSeparteChaining<K extends Comparable<K>, V extends Comparable<V>> extends AbstractTablaHash<K, V, ILista<NodoTS<K, V>>> {
    public TablaHashSeparteChaining(int tamInicial) {
        super(tamInicial);
    }

    @Override
    public void put(K key, V value) {
        int posicion = hash(key);
        try {
            ILista<NodoTS<K, V>> listasc = listaNodos.getElement(posicion);

            if (listasc != null && !contains(key)) {
                listasc.insertElement(new NodoTS<K, V>(key, value), listasc.size() + 1);
            } else {
                listaNodos.changeInfo(posicion, new ArregloDinamico<NodoTS<K, V>>(1));
                listasc = listaNodos.getElement(posicion);
                listasc.insertElement(new NodoTS<K, V>(key, value), listasc.size() + 1);
            }
        } catch (PosException | VacioException | NullException e) {
            e.printStackTrace();
        }

        tamanoAct++;

        double tam = tamanoAct;
        double tam2 = tamanoTabla;
        double tamanoCarga = tam / tam2;

        if (tamanoCarga > 5) {
            rehash();
        }

    }

    @Override
    public V get(K key) {
        V retornar = null;
        int posicion = hash(key);
        try {
            ILista<NodoTS<K, V>> listasc = listaNodos.getElement(posicion);
            if (listasc != null) {
                for (int i = 1; i <= listasc.size() && retornar == null; i++) {
                    if (listasc.getElement(i).getKey().compareTo(key) == 0) {
                        retornar = listasc.getElement(i).getValue();
                    }
                }
            }
        } catch (PosException | VacioException e) {
            e.printStackTrace();
        }

        return retornar;
    }

    @Override
    public V remove(K key) {
        V retornar = null;
        int posicion = hash(key);
        try {
            ILista<NodoTS<K, V>> listasc = listaNodos.getElement(posicion);
            if (listasc != null) {
                for (int i = 1; i <= listasc.size() && retornar == null; i++) {
                    if (listasc.getElement(i).getKey().compareTo(key) == 0) {
                        retornar = listasc.getElement(i).getValue();
                        listasc.deleteElement(i);
                    }
                }
            }
        } catch (PosException | VacioException e) {
            e.printStackTrace();
        }
        tamanoAct--;
        return retornar;


    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }


    @Override
    public ILista<K> keySet() {
        ILista<K> lista = new ArregloDinamico(1);
        try {
            for (int i = 1; i <= tamanoTabla; i++) {
                if (listaNodos.getElement(i) != null) {
                    for (int j = 1; j <= listaNodos.getElement(i).size(); j++) {
                        if (listaNodos.getElement(i).getElement(j) != null) {
                            lista.insertElement(listaNodos.getElement(i).getElement(j).getKey(), lista.size() + 1);
                        }
                    }
                }
            }
        } catch (PosException | NullException | VacioException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public ILista<V> valueSet() {
        ILista<V> lista = new ArregloDinamico(1);

        try {
            for (int i = 1; i <= tamanoTabla; i++) {
                if (listaNodos.getElement(i) != null) {
                    for (int j = 1; j <= listaNodos.getElement(i).size(); j++) {
                        if (listaNodos.getElement(i).getElement(j) != null) {
                            lista.insertElement(listaNodos.getElement(i).getElement(j).getValue(), lista.size() + 1);
                        }
                    }
                }

            }
        } catch (PosException | NullException | VacioException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public ILista<NodoTS<K, V>> darListaNodos() {
        ILista<NodoTS<K, V>> nodos = new ArregloDinamico<NodoTS<K, V>>(1);
        try {
            for (int i = 1; i <= tamanoTabla; i++) {
                ILista<NodoTS<K, V>> elemento = listaNodos.getElement(i);
                if (elemento != null && !elemento.isEmpty()) {
                    for (int j = 1; j <= elemento.size(); j++) {
                        NodoTS<K, V> elemento2 = elemento.getElement(j);
                        if (elemento2 != null && !elemento.isEmpty()) {
                            nodos.insertElement(elemento2, nodos.size() + 1);
                        }
                    }
                }
            }
        } catch (PosException | NullException | VacioException e) {
            e.printStackTrace();
        }

        return nodos;

    }

}