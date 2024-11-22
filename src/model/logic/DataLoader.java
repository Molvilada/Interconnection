package model.logic;

import model.data_structures.Country;
import model.data_structures.Landing;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    public List<Country> cargarPaises(String filePath) throws IOException {
        List<Country> countries = new ArrayList<>();
        Reader in = new FileReader(filePath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);

        for (CSVRecord record : records) {
            if (!record.get(0).isEmpty()) {
                String countryName = record.get(0);
                String capitalName = record.get(1);
                double latitude = Double.parseDouble(record.get(2));
                double longitude = Double.parseDouble(record.get(3));
                String code = record.get(4);
                String continentName = record.get(5);
                float population = Float.parseFloat(record.get(6).replace(".", ""));
                double users = Double.parseDouble(record.get(7).replace(".", ""));
                countries.add(new Country(countryName, capitalName, latitude, longitude, code, continentName, population, users));
            }
        }
        return countries;
    }

    public List<Landing> cargarLandingPoints(String filePath) throws IOException {
        List<Landing> landings = new ArrayList<>();
        Reader in2 = new FileReader(filePath);
        Iterable<CSVRecord> records2 = CSVFormat.RFC4180.withHeader().parse(in2);
        for (CSVRecord record2 : records2) {
            String landingId = record2.get(0);
            String id = record2.get(1);
            String[] x = record2.get(2).split(", ");
            String name = x[0];
            String paisnombre = x[x.length - 1];
            double latitude = Double.parseDouble(record2.get(3));
            double longitude = Double.parseDouble(record2.get(4));
            landings.add(new Landing(landingId, id, name, paisnombre, latitude, longitude));
        }
        return landings;
    }

    public List<String[]> cargarConexiones(String filePath) throws IOException {
        List<String[]> conexiones = new ArrayList<>();
        Reader in3 = new FileReader(filePath);
        Iterable<CSVRecord> records3 = CSVFormat.RFC4180.withHeader().parse(in3);

        for (CSVRecord record3 : records3) {
            String origin = record3.get(0);
            String destination = record3.get(1);
            String cableid = record3.get(3);
            conexiones.add(new String[]{origin, destination, cableid});
        }

        return conexiones;
    }

}
