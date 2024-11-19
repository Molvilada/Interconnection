package model.logic;

import model.data_structures.*;
import utils.DistanceCalculator;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

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

                float weight = DistanceCalculator.calculateDistance(
                        landing1.getLongitude(), landing1.getLatitude(),
                        landing2.getLongitude(), landing2.getLatitude());
                grafo.addEdge(id1, id2, weight);
            }
        }
    }
}
