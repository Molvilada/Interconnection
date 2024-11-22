package model.logic;

import model.data_structures.*;

public class GraphManager {
    private final GrafoListaAdyacencia grafo;

    public GraphManager(GrafoListaAdyacencia grafo) {
        this.grafo = grafo;
    }

    public static float distancia(double lon1, double lat1, double lon2, double lat2) {
        double earthRadius = 6371; // km

        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double dlon = (lon2 - lon1);
        double dlat = (lat2 - lat1);

        double sinlat = Math.sin(dlat / 2);
        double sinlon = Math.sin(dlon / 2);

        double a = (sinlat * sinlat) + Math.cos(lat1) * Math.cos(lat2) * (sinlon * sinlon);
        double c = 2 * Math.asin(Math.min(1.0, Math.sqrt(a)));

        double distance = earthRadius * c;

        return (int) distance;

    }

    public Vertex agregarVertice(String cableId, Landing landing) {
        if (landing != null) {
            String verticeId = landing.getLandingId() + cableId;
            grafo.insertVertex(verticeId, landing);
            return grafo.getVertex(verticeId);
        }
        return null;
    }

    public void agregarOActualizarArista(Landing landing1, Landing landing2, String cableId) {
        if (landing1 != null && landing2 != null) {
            Edge existe1 = grafo.getEdge(landing1.getLandingId() + cableId, landing2.getLandingId() + cableId);

            if (existe1 == null) {
                float weight3 = distancia(landing1.getLongitude(), landing1.getLatitude(), landing2.getLongitude(), landing2.getLatitude());
                grafo.addEdge(landing1.getLandingId() + cableId, landing2.getLandingId() + cableId, weight3);
            } else {
                float weight3 = distancia(landing1.getLongitude(), landing1.getLatitude(), landing2.getLongitude(), landing2.getLatitude());
                float peso3 = existe1.getWeight();

                if (weight3 > peso3) {
                    existe1.setWeight(weight3);
                }
            }
        }
    }

    public void agregarAristaPaisALanding(Country pais, Landing landing1, Landing landing2, String cableid) {
        if (pais != null) {
            float weight = distancia(pais.getLongitude(), pais.getLatitude(), landing1.getLongitude(), landing1.getLatitude());
            grafo.addEdge(pais.getCapitalName(), landing2.getLandingId() + cableid, weight);
        }
    }

    public void conectarVerticesMismosClusters(ITablaSimbolos landingidtabla) {
        try {
            ILista valores = landingidtabla.valueSet();

            for (int i = 1; i <= valores.size(); i++) {
                for (int j = 1; j <= ((ILista) valores.getElement(i)).size(); j++) {
                    Vertex vertice1;
                    if (valores.getElement(i) != null) {
                        vertice1 = (Vertex) ((ILista) valores.getElement(i)).getElement(j);
                        for (int k = 2; k <= ((ILista) valores.getElement(i)).size(); k++) {
                            Vertex vertice2 = (Vertex) ((ILista) valores.getElement(i)).getElement(k);
                            grafo.addEdge(vertice1.getId(), vertice2.getId(), 100);
                        }
                    }
                }
            }
        } catch (PosException | VacioException e) {
            e.printStackTrace();
        }
    }
}
