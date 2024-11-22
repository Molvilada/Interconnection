package model.data_structures;

public class TablaHashLinearProbing<K extends Comparable<K>, V extends Comparable<V>> extends AbstractTablaHash<K, V, NodoTS<K, V>> {
    public TablaHashLinearProbing(int tamInicial) {
        super(tamInicial);
    }

    @Override
    public void put(K key, V value) {
        int posicion = hash(key);
        try {
            NodoTS<K, V> nodo = listaNodos.getElement(posicion);
            if (nodo != null && !nodo.isEmpty()) {
                posicion = getNextEmpty(posicion);
            }

            NodoTS<K, V> nuevo = new NodoTS<K, V>(key, value);
            listaNodos.changeInfo(posicion, nuevo);
            tamanoAct++;

        } catch (PosException | VacioException | NullException e) {
            e.printStackTrace();
        }
        double tam = tamanoAct;
        double tam2 = tamanoTabla;
        double tamanoCarga = tam / tam2;

        if (tamanoCarga > 0.75) {
            rehash();
        }

    }

    public int getNextEmpty(int posicion) {
        int posicionRetornar = (posicion % tamanoTabla) + 1;

        try {
            while (listaNodos.getElement(posicionRetornar) != null && !listaNodos.getElement(posicion).isEmpty()) {
                posicionRetornar++;
                if (posicionRetornar > tamanoTabla) {
                    posicionRetornar = 1;
                }
            }
        } catch (PosException | VacioException e) {
            e.printStackTrace();
        }
        return posicionRetornar;
    }

    private Object[] searchNode(K key) {
        int posicion = hash(key);
        boolean encontroNull = false;
        V retornar = null;
        while (retornar == null && !encontroNull) {
            NodoTS<K, V> nodoActual;
            try {
                nodoActual = listaNodos.getElement(posicion);
                if (nodoActual == null) {
                    encontroNull = true;
                } else if (!nodoActual.isEmpty() && nodoActual.getKey().compareTo(key) == 0) {
                    retornar = nodoActual.getValue();
                } else {
                    posicion++;

                    if (posicion > tamanoTabla) {
                        posicion = 1;
                    }
                }

            } catch (PosException | VacioException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return new Object[]{retornar, posicion};
    }


    @Override
    public V get(K key) {
        return (V) searchNode(key)[0];
    }

    @Override
    public V remove(K key) {
        V retornar = null;
        try {
            retornar = (V) searchNode(key)[0];
            if (retornar != null) {
                listaNodos.getElement((Integer) searchNode(key)[1]).setEmpty();
            } else {
                throw new PosException("No está el elemento");
            }
        } catch (PosException | VacioException e) {
            e.printStackTrace();
        }

        return retornar;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ILista<K> keySet() {

        ILista<K> lista = new ArregloDinamico(1);
        try {
            for (int i = 1; i <= tamanoTabla; i++) {
                if (listaNodos.getElement(i) != null) {
                    lista.insertElement(listaNodos.getElement(i).getKey(), lista.size() + 1);
                }
            }
        } catch (PosException | NullException | VacioException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public ILista<V> valueSet() {
        ILista<V> lista = new ArregloDinamico(1);
        for (int i = 1; i <= tamanoTabla; i++) {
            try {

                if (listaNodos.getElement(i) != null) {
                    lista.insertElement(listaNodos.getElement(i).getValue(), lista.size() + 1);
                }
            } catch (PosException | NullException | VacioException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return lista;
    }

    @Override
    public ILista<NodoTS<K, V>> darListaNodos() {
        ILista<NodoTS<K, V>> nodos = new ArregloDinamico<NodoTS<K, V>>(1);

        try {
            for (int i = 1; i <= tamanoTabla; i++) {
                NodoTS<K, V> elemento = listaNodos.getElement(i);
                if (elemento != null && !elemento.isEmpty()) {
                    nodos.insertElement(elemento, nodos.size() + 1);
                }
            }
        } catch (PosException | NullException | VacioException e) {
            e.printStackTrace();
        }

        return nodos;
    }

}