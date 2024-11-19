package model.logic;

import model.data_structures.*;

public class PathAnalyzer {
    private GrafoListaAdyacencia grafo;

    public PathAnalyzer(GrafoListaAdyacencia grafo) {
        this.grafo = grafo;
    }

    /**
     * Calcula el camino más corto entre dos nodos del grafo.
     */
    public PilaEncadenada<Edge<String, Landing>> getShortestPath(String origen, String destino) {
        return grafo.minPath(origen, destino);
    }
}

