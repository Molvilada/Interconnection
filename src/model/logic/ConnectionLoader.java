package model.logic;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import model.data_structures.Edge;
import model.data_structures.GrafoListaAdyacencia;
import model.data_structures.ITablaSimbolos;
import model.data_structures.Landing;
import model.data_structures.Vertex;

public class ConnectionLoader {

    public void loadConnections(
            GrafoListaAdyacencia grafo,
            ITablaSimbolos<String, Landing> points,
            ITablaSimbolos<String, String> nombrecodigo,
            ITablaSimbolos<String, Vertex<String, Landing>> landingidtabla
    ) throws IOException {
        Reader in = new FileReader("./data/connections.csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);

        for (CSVRecord record : records) {
            String origin = record.get(0);
            String destination = record.get(1);
            String cableid = record.get(3);

            Landing landing1 = points.get(origin);
            Landing landing2 = points.get(destination);

            if (landing1 != null && landing2 != null) {
                String id1 = landing1.getLandingId() + cableid;
                String id2 = landing2.getLandingId() + cableid;

                grafo.insertVertex(id1, landing1);
                grafo.insertVertex(id2, landing2);

                float weight = calculateDistance(landing1.getLongitude(), landing1.getLatitude(), landing2.getLongitude(), landing2.getLatitude());
                grafo.addEdge(id1, id2, weight);

                // Additional processing for landingidtabla and nombrecodigo can go here if needed
            }
        }
    }

    private float calculateDistance(double lon1, double lat1, double lon2, double lat2) {
        double earthRadius = 6371; // km
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (earthRadius * c);
    }
}
