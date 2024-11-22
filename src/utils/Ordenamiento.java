package utils;

import java.util.Comparator;

import model.data_structures.ILista;
import model.data_structures.NullException;
import model.data_structures.PosException;
import model.data_structures.VacioException;

public final class Ordenamiento <T extends Comparable <T>> {
	
	/**
	 * Ordena una lista de elementos usando el algoritmo de ordenamiento QuickSort.
	 * @param lista Lista de elementos a ordenar.
	 * @param criterio Criterio de comparación.
	 * @param ascendente true si se quiere ordenar de forma ascendente, false de forma descendente.
	 * @throws PosException Si se intenta acceder a una posición inválida.
	 * @throws VacioException Si la lista está vacía.
	 * @throws NullException Si la lista es nula.
	 */
	
	public void ordenarMergeSort(ILista<T> lista, Comparator<T> criterio, boolean ascendente) throws PosException, VacioException, NullException
	{
		int size = lista.size();
		if(size > 1)
		{
			int mid = size/2;
			//Se divide la lista original en dos partes, izquierda y derecha, desde el punto mid.
			ILista<T> leftList = lista.sublista(1, mid);
			ILista<T> rightList = lista.sublista(mid+1, size - mid);

			//Se hace el llamado recursivo con la lista izquierda y derecha.
			ordenarMergeSort(leftList, criterio, ascendente);
			ordenarMergeSort(rightList, criterio, ascendente);
			
			//i recorre la lista de la izquierda, j la derecha y k la lista original.
			int i,j,k;
			i=j=k= 1;
			
			int leftelements = leftList.size();
			int rightelements = rightList.size();
			
			while(i <= leftelements && j <= rightelements)
			{
				T elemi = leftList.getElement(i);
				T elemj = rightList.getElement(j);
				//Compara y ordena los elementos
				int factorComparacion = (ascendente?1:-1) * criterio.compare(elemi, elemj);
				
				if(factorComparacion <= 0) 
				{
					lista.changeInfo(k, elemi);
					i++;
				}
				else
				{
					lista.changeInfo(k, elemj);
					j++;
				}
				k++;
			}
			
			//Agrega los elementos que no se compararon y están ordenados
			while(i <= leftelements)
			{
				lista.changeInfo(k, leftList.getElement(i));
				i++;
				k++;
			}
			
			while(j <= rightelements)
			{
				lista.changeInfo(k, rightList.getElement(j));
				j++;
				k++;
			}
		}
	}

	
}
