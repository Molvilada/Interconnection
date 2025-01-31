package model.data_structures;


public class ArregloDinamico<T extends Comparable<T>> implements ILista<T> {
    /**
     * Capacidad maxima del arreglo
     */
    private int tamanoMax;
    /**
     * Numero de elementos presentes en el arreglo (de forma compacta desde la posicion 0)
     */
    private int tamanoAct;
    /**
     * Arreglo de elementos de tamaNo maximo
     */
    private T[] elementos;

    /**
     * Construir un arreglo con la capacidad maxima inicial.
     *
     * @param max Capacidad maxima inicial
     */
    public ArregloDinamico(int max) {
        elementos = (T[]) new Comparable[max];
        tamanoMax = max;
        tamanoAct = 0;
    }

    //Siempre se llama a insert o a delete primero, esos métodos manejan los casos de que el elemento sea null,
    //isEmpty o que la posición no sea válida
    public void addLast(T dato) {
        if (tamanoAct == tamanoMax) {  // caso de arreglo lleno (aumentar tamaNo)
            tamanoMax = 2 * tamanoMax;
            T[] copia = elementos;
            elementos = (T[]) new Comparable[tamanoMax];
            if (tamanoAct >= 0) System.arraycopy(copia, 0, elementos, 0, tamanoAct);
        }
        if (tamanoAct < tamanoMax) {
            elementos[tamanoAct] = dato; // Asignar el dato
        } else {
            throw new ArrayIndexOutOfBoundsException("Indice tamanoAct fuera del rango del arreglo: " + tamanoAct);
        }
    }

    public int size() {
        return tamanoAct;
    }

    public T getElement(int pos) throws PosException, VacioException {
        if (pos < 1) {
            throw new PosException("La posición no es válida");
        }
        if (pos > tamanoMax) {
            throw new PosException("La posición no es válida");
        } else if (isEmpty()) {
            throw new VacioException("La lista está vacía");
        } else {
            if (elementos[pos - 1] == null) {
                return null;
            } else {
                return elementos[pos - 1];
            }
        }
    }


    //Siempre se llama a insert o a delete primero, esos métodos manejan los casos de que el elemento sea null,
    //isEmpty o que la posición no sea válida
    @Override
    public void addFirst(T element) {
        if (tamanoAct > 0) {
            if (tamanoAct == tamanoMax) {
                tamanoMax = 2 * tamanoMax;
            }
            T[] copia = elementos;
            elementos = (T[]) new Comparable[tamanoMax];

            elementos[0] = element;
            System.arraycopy(copia, 0, elementos, 1, tamanoAct);
        } else {
            elementos[0] = element;
        }

    }

    @Override
    public void insertElement(T elemento, int pos) throws PosException, NullException {
        if (pos - 1 > tamanoMax) {
            throw new PosException("La posición no es válida");
        } else if (pos < 1) {
            throw new PosException("La posición no es válida");
        } else {
            if (pos == 1) {
                addFirst(elemento);
            } else if (tamanoAct + 1 == pos) {
                addLast(elemento);

            } else {
                if (tamanoAct == tamanoMax) {
                    tamanoMax = 2 * tamanoMax;
                }
                T[] copia = elementos;
                elementos = (T[]) new Comparable[tamanoMax];

                System.arraycopy(copia, 0, elementos, 0, pos - 1);

                elementos[pos - 1] = elemento;

                if (tamanoAct - pos >= 0) System.arraycopy(copia, pos - 1, elementos, pos, tamanoAct - pos);
            }

            tamanoAct++;
        }

    }


    //Siempre se llama a insert o a delete primero, esos métodos manejan los casos de que el elemento sea null,
    //isEmpty o que la posición no sea válida
    @Override
    public T removeFirst() {
        T elemento = elementos[0];

        T[] copia = elementos;
        elementos = (T[]) new Comparable[tamanoMax];


        if (tamanoAct - 1 >= 0) System.arraycopy(copia, 1, elementos, 0, tamanoAct - 1);

        tamanoAct--;
        return elemento;

    }

    //Siempre se llama a insert o a delete primero, esos métodos manejan los casos de que el elemento sea null,
    //isEmpty o que la posición no sea válida
    @Override
    public T removeLast() {
        T elemento = elementos[tamanoAct - 1];
        elementos[tamanoAct - 1] = null;
        tamanoAct--;
        return elemento;
    }


    @Override
    public T deleteElement(int pos) throws PosException, VacioException {
        T elemento = null;

        if (pos > tamanoMax) {
            throw new PosException("La posición no es válida");
        } else if (pos < 1) {
            throw new PosException("La posición no es válida");
        } else if (isEmpty()) {
            throw new VacioException("La lista está vacía");
        } else {
            elemento = elementos[pos];
            if (pos == 1) {
                removeFirst();
            } else if (pos == tamanoAct) {
                removeLast();
            } else {
                T[] copia = elementos;
                elementos = (T[]) new Comparable[tamanoMax];

                System.arraycopy(copia, 0, elementos, 0, pos - 1);

                if (tamanoAct - (pos - 1) >= 0)
                    System.arraycopy(copia, pos - 1 + 1, elementos, pos - 1, tamanoAct - (pos - 1));
                tamanoAct--;
            }
        }

        return elemento;
    }


    @Override
    public T firstElement() throws VacioException {
        T retorno = null;
        if (tamanoAct == 0) {
            throw new VacioException("La lista está vacía");
        } else {
            retorno = elementos[0];
        }

        return retorno;
    }


    @Override
    public T lastElement() throws VacioException {
        if (tamanoAct == 0) {
            throw new VacioException("La lista está vacía");
        } else {
            return elementos[tamanoAct - 1];
        }

    }


    @Override
    public boolean isEmpty() {
        return tamanoAct < 0;
    }


    @Override
    public int isPresent(T element) throws NullException, VacioException {
        int pos = -1;
        if (element == null) {
            throw new NullException("No es válido el elemento ingresado");
        } else if (isEmpty()) {
            throw new VacioException("La lista está vacía");
        } else {
            boolean ya = false;
            for (int i = 0; i < tamanoAct && !ya; i++) {
                if (elementos[i].compareTo(element) == 0) {
                    pos = i;
                    ya = true;
                }

            }
        }

        return pos + 1;
    }


    @Override
    public void exchange(int pos1, int pos2) throws PosException, VacioException {
        if (pos1 > tamanoMax) {
            throw new PosException("La posición no es válida");
        } else if (pos2 > tamanoMax) {
            throw new PosException("La posición no es válida");
        } else if (pos1 < 1) {
            throw new PosException("La posición no es válida");
        } else if (pos2 < 1) {
            throw new PosException("La posición no es válida");
        } else if (isEmpty()) {
            throw new VacioException("La lista está vacía");
        } else if (pos1 != pos2 && tamanoAct > 1) {
            T elemento1 = elementos[pos1 - 1];
            T elemento2 = elementos[pos2 - 1];

            elementos[pos1 - 1] = elemento2;
            elementos[pos2 - 1] = elemento1;
        }

    }

    @Override
    public void changeInfo(int pos, T element) throws PosException, VacioException, NullException {
        if (pos < 1 || pos > tamanoMax) {
            throw new PosException("La posición no es válida");
        } else if (isEmpty()) {
            throw new VacioException("La lista está vacía");
        } else if (element == null) {
            throw new NullException("No es válido el elemento ingresado");
        } else {
            elementos[pos - 1] = element;
        }

    }

    public ILista<T> sublista(int pos, int numElementos) throws PosException, VacioException, NullException {
        if (isEmpty()) {
            throw new VacioException("La lista está vacía");
        } else if (numElementos < 0) {
            throw new PosException("La cantidad de elementos no es válida");
        } else if (numElementos >= size()) {
            return this;
        } else {
            ILista<T> copia = new ArregloDinamico(numElementos);

            int contador = pos;
            for (int i = 0; i < numElementos; i++) {
                T elemento = this.getElement(contador);
                copia.insertElement(elemento, i + 1);
                contador++;
            }

            return copia;
        }
    }

    @Override
    public int compareTo(ILista o) {
        // TODO Auto-generated method stub
        return 0;
    }

}
