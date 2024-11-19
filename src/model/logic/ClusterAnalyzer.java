package model.logic;

import model.data_structures.*;

public class ClusterAnalyzer {

    private ITablaSimbolos<String, Integer> scc; // Tabla de componentes conectados
    private ITablaSimbolos<String, String> nombreCodigo; // Mapeo nombre -> código
    private ITablaSimbolos<String, Vertex<String, Landing>> landingIdTabla; // Cambiado a Vertex<String, Landing>
    // Cambiado a String

    public ClusterAnalyzer(ITablaSimbolos<String, Integer> scc,
                           ITablaSimbolos<String, String> nombreCodigo,
                           ITablaSimbolos<String, Vertex<String, Landing>> landingIdTabla) { // Cambiado a Vertex<String, Landing>
        this.scc = scc;
        this.nombreCodigo = nombreCodigo;
        this.landingIdTabla = landingIdTabla;
    }


    /**
     * Obtiene el número máximo de componentes conectados en el grafo.
     */
    public int getMaxConnectedComponents() {
        ILista<Integer> componentValues = scc.valueSet();
        int max = 0;

        for (int i = 1; i <= componentValues.size(); i++) {
            try {
                int value = componentValues.getElement(i);
                if (value > max) {
                    max = value;
                }
            } catch (PosException | VacioException e) {
                e.printStackTrace();
            }
        }
        return max;
    }

    /**
     * Verifica si dos puntos de conexión pertenecen al mismo clúster.
     */
    public boolean arePointsInSameCluster(String punto1, String punto2) {
        try {
            // Obtención de códigos
            String codigo1 = nombreCodigo.get(punto1);
            String codigo2 = nombreCodigo.get(punto2);

            if (codigo1 == null || codigo2 == null) {
                throw new IllegalArgumentException("Los puntos de conexión no tienen códigos válidos.");
            }

            // Obtención de vértices
            Vertex<String, Landing> vertice1 = landingIdTabla.get(codigo1);
            Vertex<String, Landing> vertice2 = landingIdTabla.get(codigo2);

            if (vertice1 == null || vertice2 == null) {
                throw new IllegalArgumentException("No se encontraron vértices para los códigos proporcionados.");
            }

            // Obtención de componentes conectados
            int elemento1 = scc.get(vertice1.getId());
            int elemento2 = scc.get(vertice2.getId());

            return elemento1 == elemento2;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

}

