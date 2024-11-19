package model.logic;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import model.data_structures.ITablaSimbolos;
import model.data_structures.Landing;

public class LandingPointLoader {

    public void loadLandingPoints(ITablaSimbolos<String, Landing> points) throws IOException {
        Reader in = new FileReader("./data/landing_points.csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);

        for (CSVRecord record : records) {
            String landingId = record.get(0);
            String id = record.get(1);
            String[] locationParts = record.get(2).split(", ");
            String name = locationParts[0];
            String countryName = locationParts[locationParts.length - 1];
            double latitude = Double.parseDouble(record.get(3));
            double longitude = Double.parseDouble(record.get(4));

            Landing landing = new Landing(landingId, id, name, countryName, latitude, longitude);
            points.put(landingId, landing);
        }
    }
}
