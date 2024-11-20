package model.logic;

import model.data_structures.*;

public class MSTAnalyzer<K extends Comparable<K>, V extends Comparable<V>> {

    private GrafoListaAdyacencia<K, V> grafo;

    public MSTAnalyzer(GrafoListaAdyacencia<K, V> grafo) {
        this.grafo = grafo;
    }

    /**
     * Encuentra el vértice inicial para el MST, basado en el mayor número de conexiones.
     */
    public K findInitialVertex(ITablaSimbolos<K, Vertex<K, V>> landingIdTable) {
        K initialVertex = null;
        int maxConnections = 0;

        ILista<K> keys = landingIdTable.keySet();
        for (int i = 1; i <= keys.size(); i++) {
            try {
                K key = keys.getElement(i);
                Vertex<K, V> vertex = landingIdTable.get(key);
                if (vertex != null && vertex.outdegree() > maxConnections) {
                    maxConnections = vertex.outdegree();
                    initialVertex = key;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return initialVertex;
    }

    /**
     * Calcula el Árbol de Expansión Mínima (MST) desde un vértice inicial.
     */
    public ILista<Edge<K, V>> calculateMST(K initialVertex) {
        return grafo.mstPrimLazy(initialVertex);
    }

    /**
     * Encuentra la rama más larga dentro del MST.
     */
    public PilaEncadenada<Vertex<K, V>> findLongestPath(ILista<Edge<K, V>> mstEdges) {
        // Utiliza el método ya existente en el grafo para manejar el cálculo
        // Reutiliza estructuras de datos internas para simplificar el proceso.
        ITablaSimbolos<K, Vertex<K, V>> parentMap = new TablaHashSeparteChaining<>(mstEdges.size());

        for (int i = 1; i <= mstEdges.size(); i++) {
            try {
                Edge<K, V> edge = mstEdges.getElement(i);
                parentMap.put(edge.getDestination().getId(), edge.getSource());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return findDeepestPath(parentMap);
    }

    private PilaEncadenada<Vertex<K, V>> findDeepestPath(ITablaSimbolos<K, Vertex<K, V>> parentMap) {
        PilaEncadenada<Vertex<K, V>> longestPath = new PilaEncadenada<>();
        int maxDepth = 0;

        ILista<K> keys = parentMap.keySet();
        for (int i = 1; i <= keys.size(); i++) {
            try {
                K currentKey = keys.getElement(i);
                PilaEncadenada<Vertex<K, V>> currentPath = new PilaEncadenada<>();
                int currentDepth = 0;

                while (currentKey != null) {
                    Vertex<K, V> parent = parentMap.get(currentKey);
                    if (parent != null) {
                        currentPath.push(parent);
                        currentKey = parent.getId();
                        currentDepth++;
                    } else {
                        currentKey = null;
                    }
                }

                if (currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                    longestPath = currentPath;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return longestPath;
    }
}
