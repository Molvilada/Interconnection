package model.logic;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import model.data_structures.Country;
import model.data_structures.GrafoListaAdyacencia;
import model.data_structures.ITablaSimbolos;

public class CountryLoader {

    public void loadCountries(GrafoListaAdyacencia grafo, ITablaSimbolos<String, Country> paises) throws IOException {
        Reader in = new FileReader("./data/countries.csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);

        for (CSVRecord record : records) {
            if (!record.get(0).equals("")) {
                String countryName = record.get(0);
                String capitalName = record.get(1);
                double latitude = Double.parseDouble(record.get(2));
                double longitude = Double.parseDouble(record.get(3));
                String code = record.get(4);
                String continentName = record.get(5);
                float population = Float.parseFloat(record.get(6).replace(".", ""));
                double users = Double.parseDouble(record.get(7).replace(".", ""));

                Country pais = new Country(countryName, capitalName, latitude, longitude, code, continentName, population, users);
                grafo.insertVertex(capitalName, pais);
                paises.put(countryName, pais);
            }
        }
    }
}

