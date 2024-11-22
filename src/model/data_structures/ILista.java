package model.data_structures;

public interface ILista<T extends Comparable<T>> extends Comparable<ILista<T>> {

    void addFirst(T element);

    void addLast(T element);

    void insertElement(T elemento, int pos) throws PosException, NullException;

    T removeFirst() throws VacioException;

    T removeLast();

    T deleteElement(int pos) throws PosException, VacioException;

    T firstElement() throws VacioException;

    T lastElement() throws VacioException;

    T getElement(int pos) throws PosException, VacioException;

    int size();

    boolean isEmpty();

    int isPresent(T element) throws VacioException, NullException, PosException;

    void exchange(int pos1, int pos2) throws PosException, VacioException;

    void changeInfo(int pos, T element) throws PosException, VacioException, NullException;

    /**
     * Crear una sublista de la lista original (this).
     * Los elementos se toman en el mismo orden como aparecen en la lista original (this).
     *
     * @param numero de elementos que contendrá la sublista. Si el número es superior al tamaño
     *               original de la lista, se obtiene una copia de la lista original.
     * @return sublista creada con la misma representación de la lista original (this).
     * @throws VacioException
     * @throws PosException
     * @throws NullException
     */
    ILista<T> sublista(int pos, int numElementos) throws PosException, VacioException, NullException;

}
