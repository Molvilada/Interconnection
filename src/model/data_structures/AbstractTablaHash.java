package model.data_structures;

import java.text.DecimalFormat;

public abstract class AbstractTablaHash<K extends Comparable<K>, V extends Comparable<V>, T extends Comparable<T>> implements ITablaSimbolos<K, V> {
    protected final int minicial;
    protected int tamanoAct;
    protected int tamanoTabla;
    protected ILista<T> listaNodos;
    private double cantidadRehash;

    public AbstractTablaHash(int tamInicial) {
        int m = nextPrime(tamInicial);
        minicial = m;
        tamanoAct = 0;
        tamanoTabla = m;
        listaNodos = new ArregloDinamico<>(m);
        for (int i = 1; i <= tamanoTabla; i++) {
            try {
                listaNodos.insertElement(null, i);
            } catch (PosException | NullException e) {
                e.printStackTrace();
            }
        }
    }

    // Métodos comunes
    protected static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i = i + 6)
            if (n % i == 0 || n % (i + 2) == 0)
                return false;
        return true;
    }

    protected static int nextPrime(int N) {
        if (N <= 1)
            return 2;
        int prime = N;
        boolean found = false;

        while (!found) {
            prime++;
            if (isPrime(prime))
                found = true;
        }
        return prime;
    }

    public void rehash() {
        try {
            ILista<NodoTS<K, V>> nodos = darListaNodos();

            tamanoAct = 0;
            tamanoTabla *= 2;
            tamanoTabla = nextPrime(tamanoTabla);
            listaNodos = new ArregloDinamico<>(tamanoTabla);

            for (int i = 1; i <= tamanoTabla; i++) {
                listaNodos.insertElement(null, size() + 1);
            }

            NodoTS<K, V> actual = null;
            for (int i = 1; i <= nodos.size(); i++) {
                actual = nodos.getElement(i);
                put(actual.getKey(), actual.getValue());
            }
        } catch (NullException | VacioException | PosException e) {
            e.printStackTrace();
        }

        cantidadRehash++;

    }

    public String toString() {
        String retorno = "";
        retorno += "La cantidad de duplas: " + keySet().size();
        retorno += "\nEl m inicial es: " + minicial;
        retorno += "\nEl m final es: " + tamanoTabla;
        double tam = tamanoAct;
        double tam2 = tamanoTabla;
        DecimalFormat df = new DecimalFormat("###.##");
        double tamañoCarga = tam / tam2;
        retorno += "\nEl factor de carga es: " + df.format(tamañoCarga);
        retorno += "\nLa cantidad de rehash es: " + cantidadRehash;

        return retorno;
    }

    @Override
    public int hash(K key) {
        return Math.abs(key.hashCode() % tamanoTabla) + 1;
    }

    @Override
    public int size() {
        return tamanoAct;
    }

    @Override
    public boolean contains(K key) {
        V valor = get(key);
        return valor != null;
    }


    // Métodos abstractos a implementar por las subclases
    public abstract void put(K key, V value);

    public abstract V get(K key);

    public abstract V remove(K key);
}
