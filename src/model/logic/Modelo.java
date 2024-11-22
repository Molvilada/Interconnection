package model.logic;

import model.data_structures.*;
import model.data_structures.Country.ComparadorXKm;
import utils.Ordenamiento;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;


/**
 * Definicion del modelo del mundo
 */
public class Modelo {
    /**
     * Atributos del modelo del mundo
     */
    private final ILista datos;
    private final DataLoader dataLoader;
    private final GrafoListaAdyacencia grafo;
    private final ITablaSimbolos paises;
    private final ITablaSimbolos points;
    private final ITablaSimbolos landingidtabla;
    private final ITablaSimbolos nombrecodigo;
    private final GraphManager graphManager;

    /**
     * Constructor del modelo del mundo con capacidad dada
     *
     * @param capacidad
     */
    public Modelo(int capacidad) {
        datos = new ArregloDinamico<>(capacidad);
        dataLoader = new DataLoader();

        grafo = new GrafoListaAdyacencia(2);
        paises = new TablaHashLinearProbing(2);
        points = new TablaHashLinearProbing(2);
        landingidtabla = new TablaHashSeparteChaining(2);
        nombrecodigo = new TablaHashSeparteChaining(2);

        graphManager = new GraphManager(grafo);
    }

    /**
     * Servicio de consulta de numero de elementos presentes en el modelo
     *
     * @return numero de elementos presentes en el modelo
     */
    public int darTamano() {
        return datos.size();
    }


    public String toString() {
        String fragmento = "Info básica:";

        fragmento += "\n El número total de conexiones (arcos) en el grafo es: " + grafo.edges().size();
        fragmento += "\n El número total de puntos de conexión (landing points) en el grafo: " + grafo.vertices().size();
        fragmento += "\n La cantidad total de países es:  " + paises.size();
        Landing landing = null;
        try {
            landing = (Landing) ((NodoTS) points.darListaNodos().getElement(1)).getValue();
            fragmento += "\n Info primer landing point " + "\n Identificador: " + landing.getId() + "\n Nombre: " + landing.getName()
                    + " \n Latitud " + landing.getLatitude() + " \n Longitud" + landing.getLongitude();

            Country pais = (Country) ((NodoTS) paises.darListaNodos().getElement(paises.darListaNodos().size())).getValue();

            fragmento += "\n Info último país: " + "\n Capital: " + pais.getCapitalName() + "\n Población: " + pais.getPopulation() +
                    "\n Usuarios: " + pais.getUsers();
        } catch (PosException | VacioException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return fragmento;

    }

    private int calcularComponentesConectados() {
        ITablaSimbolos tabla = grafo.getSSC();
        ILista lista = tabla.valueSet();
        int max = 0;
        for (int i = 1; i <= lista.size(); i++) {
            try {
                if ((int) lista.getElement(i) > max) {
                    max = (int) lista.getElement(i);
                }
            } catch (PosException | VacioException e) {
                e.printStackTrace();
            }
        }
        return max;
    }

    private boolean verificarMismoCluster(String punto1, String punto2) {
        try {
            String codigo1 = (String) nombrecodigo.get(punto1);
            String codigo2 = (String) nombrecodigo.get(punto2);
            Vertex vertice1 = (Vertex) ((ILista) landingidtabla.get(codigo1)).getElement(1);
            Vertex vertice2 = (Vertex) ((ILista) landingidtabla.get(codigo2)).getElement(1);

            ITablaSimbolos tabla = grafo.getSSC();
            int elemento1 = (int) tabla.get(vertice1.getId());
            int elemento2 = (int) tabla.get(vertice2.getId());

            return elemento1 == elemento2;
        } catch (PosException | VacioException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String req1String(String punto1, String punto2) {
        int componentesConectados = calcularComponentesConectados();
        String fragmento = "La cantidad de componentes conectados es: " + componentesConectados;

        if (verificarMismoCluster(punto1, punto2)) {
            fragmento += "\n Los landing points pertenecen al mismo clúster";
        } else {
            fragmento += "\n Los landing points no pertenecen al mismo clúster";
        }

        return fragmento;

    }


    public String req2String() {
        StringBuilder fragmento = new StringBuilder();

        ILista lista = landingidtabla.valueSet();

        int cantidad = 0;

        int contador = 0;

        for (int i = 1; i <= lista.size(); i++) {
            try {
                if (((ILista) lista.getElement(i)).size() > 1 && contador <= 10) {
                    Landing landing = (Landing) ((Vertex) ((ILista) lista.getElement(i)).getElement(1)).getInfo();

                    for (int j = 1; j <= ((ILista) lista.getElement(i)).size(); j++) {
                        cantidad += ((Vertex) ((ILista) lista.getElement(i)).getElement(j)).edges().size();
                    }

                    fragmento.append("\n Landing " + "\n Nombre: ").append(landing.getName()).append("\n País: ").append(landing.getPais()).append("\n Id: ").append(landing.getId()).append("\n Cantidad: ").append(cantidad);

                    contador++;
                }
            } catch (PosException | VacioException e) {
                e.printStackTrace();
            }

        }

        return fragmento.toString();
    }

    /**
     * Obtiene los datos necesarios de un vértice: longitud, latitud y nombre.
     *
     * @param vertice El vértice a procesar.
     * @return Un arreglo de tamaño 3: [logintud, latitud, nombre].
     */
    private Object[] obtenerDatosVertice(Vertex vertice) {
        Object info = vertice.getInfo();
        if (info instanceof Landing) {
            Landing landing = (Landing) info;
            return new Object[]{landing.getLongitude(), landing.getLatitude(), landing.getLandingId()};
        } else if (info instanceof Country) {
            Country country = (Country) info;
            return new Object[]{country.getLongitude(), country.getLatitude(), country.getCapitalName()};
        }
        return new Object[]{0, 0, "Desconocido"};
    }


    public String req3String(String pais1, String pais2) {
        Country pais11 = (Country) paises.get(pais1);
        Country pais22 = (Country) paises.get(pais2);
        String capital1 = pais11.getCapitalName();
        String capital2 = pais22.getCapitalName();

        PilaEncadenada pila = grafo.minPath(capital1, capital2);

        float distancia = 0;

        StringBuilder fragmento = new StringBuilder("Ruta: ");

        float disttotal = 0;

        while (!pila.isEmpty()) {
            Edge arco = ((Edge) pila.pop());

            Object[] origenData = obtenerDatosVertice(arco.getSource());
            double longorigen = (double) origenData[0];
            double latorigen = (double) origenData[0];
            String origennombre = origenData[2].toString();

            Object[] destinoData = obtenerDatosVertice(arco.getDestination());
            double latdestino = (double) destinoData[1];
            double longdestino = (double) destinoData[1];
            String destinonombre = destinoData[2].toString();

            distancia = GraphManager.distancia(longdestino, latdestino, longorigen, latorigen);
            fragmento.append("\n \n Origen: ").append(origennombre).append("  Destino: ").append(destinonombre).append("  Distancia: ").append(distancia);
            disttotal += distancia;

        }

        fragmento.append("\n Distancia total: ").append(disttotal);

        return fragmento.toString();

    }

    public String req4String() {
        String fragmento = "";
        ILista lista1 = landingidtabla.valueSet();

        String llave = "";

        int distancia = 0;

        try {
            int max = 0;
            for (int i = 1; i <= lista1.size(); i++) {
                if (((ILista) lista1.getElement(i)).size() > max) {
                    max = ((ILista) lista1.getElement(i)).size();
                    llave = (String) ((Vertex) ((ILista) lista1.getElement(i)).getElement(1)).getId();
                }
            }

            ILista lista2 = grafo.mstPrimLazy(llave);

            ITablaSimbolos tabla = new TablaHashSeparteChaining<>(2);
            ILista candidatos = new ArregloDinamico<>(1);
            for (int i = 1; i <= lista2.size(); i++) {
                Edge arco = ((Edge) lista2.getElement(i));
                distancia += (int) arco.getWeight();

                candidatos.insertElement(arco.getSource(), candidatos.size() + 1);

                candidatos.insertElement(arco.getDestination(), candidatos.size() + 1);

                tabla.put(arco.getDestination().getId(), arco.getSource());
            }

            ILista unificado = unificar(candidatos, "Vertice");
            fragmento += " La cantidad de nodos conectada a la red de expansión mínima es: " + unificado.size() + "\n El costo total es de: " + distancia;

            int maximo = 0;
            int contador = 0;
            PilaEncadenada caminomax = new PilaEncadenada();
            for (int i = 1; i <= unificado.size(); i++) {

                PilaEncadenada path = new PilaEncadenada();
                String idBusqueda = (String) ((Vertex) unificado.getElement(i)).getId();
                Vertex actual;

                while ((actual = (Vertex) tabla.get(idBusqueda)) != null && actual.getInfo() != null) {
                    path.push(actual);
                    idBusqueda = (String) actual.getId();
                    contador++;
                }

                if (contador > maximo) {
                    caminomax = path;
                }
            }

            fragmento += "\n La rama más larga está dada por lo vértices: ";
            for (int i = 1; i <= caminomax.size(); i++) {
                Vertex pop = (Vertex) caminomax.pop();
                fragmento += "\n Id " + i + " : " + pop.getId();
            }
        } catch (PosException | VacioException | NullException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (fragmento.isEmpty()) {
            return "No hay ninguna rama";
        } else {
            return fragmento;
        }
    }

    public ILista req5(String punto) {
        String codigo = (String) nombrecodigo.get(punto);
        ILista lista = (ILista) landingidtabla.get(codigo);

        ILista countries = new ArregloDinamico<>(1);
        try {
            Country paisoriginal = (Country) paises.get(((Landing) ((Vertex) lista.getElement(1)).getInfo()).getPais());
            countries.insertElement(paisoriginal, countries.size() + 1);
        } catch (PosException | VacioException | NullException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        for (int i = 1; i <= lista.size(); i++) {
            try {
                Vertex vertice = (Vertex) lista.getElement(i);
                ILista arcos = vertice.edges();

                for (int j = 1; j <= arcos.size(); j++) {
                    Vertex vertice2 = ((Edge) arcos.getElement(j)).getDestination();

                    Country pais = null;
                    if (vertice2.getInfo() instanceof Landing) {
                        Landing landing = (Landing) vertice2.getInfo();
                        pais = (Country) paises.get(landing.getPais());
                        countries.insertElement(pais, countries.size() + 1);

                        float distancia = GraphManager.distancia(pais.getLongitude(), pais.getLatitude(), landing.getLongitude(), landing.getLatitude());

                        pais.setDistlan(distancia);
                    } else {
                        pais = (Country) vertice2.getInfo();
                    }
                }

            } catch (PosException | VacioException | NullException e) {
                e.printStackTrace();
            }
        }

        ILista unificado = unificar(countries, "Country");

        Comparator<Country> comparador = null;

        Ordenamiento<Country> algsOrdenamientoEventos = new Ordenamiento<Country>();

        comparador = new ComparadorXKm();

        try {

            if (lista != null) {
                algsOrdenamientoEventos.ordenarMergeSort(unificado, comparador, true);
            }
        } catch (PosException | VacioException | NullException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return unificado;


    }

    public String req5String(String punto) {
        ILista afectados = req5(punto);

        String fragmento = "La cantidad de paises afectados es: " + afectados.size() + "\n Los paises afectados son: ";

        for (int i = 1; i <= afectados.size(); i++) {
            try {
                fragmento += "\n Nombre: " + ((Country) afectados.getElement(i)).getCountryName() + "\n Distancia al landing point: " + ((Country) afectados.getElement(i)).getDistlan();
            } catch (PosException | VacioException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return fragmento;
    }

    private <T extends Comparable<T>> void procesarUnificacion(ILista lista, ILista lista2, Comparator<T> comparador) throws PosException, VacioException, NullException {
        Ordenamiento<T> algsOrdenamientoEventos = new Ordenamiento<>();
        algsOrdenamientoEventos.ordenarMergeSort(lista, comparador, false);

        for (int i = 1; i <= lista.size(); i++) {
            T actual = (T) lista.getElement(i);
            T siguiente = (i + 1 <= lista.size()) ? (T) lista.getElement(i + 1) : null;

            if (siguiente != null) {
                if (comparador.compare(actual, siguiente) != 0) {
                    lista2.insertElement(actual, lista2.size() + 1);
                }
            } else {
                T anterior = (i > 1) ? (T) lista.getElement(i - 1) : null;
                if (anterior == null || comparador.compare(anterior, actual) != 0) {
                    lista2.insertElement(actual, lista2.size() + 1);
                }
            }
        }
    }

    public ILista unificar(ILista lista, String criterio) {

        ILista lista2 = new ArregloDinamico(1);

        if (criterio.equals("Vertice")) {
            Comparator<Vertex<String, Landing>> comparador = new Vertex.ComparadorXKey();

            try {
                procesarUnificacion(lista, lista2, comparador);
            } catch (PosException | VacioException | NullException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Comparator<Country> comparador = new Country.ComparadorXNombre();
            try {
                procesarUnificacion(lista, lista2, comparador);
            } catch (PosException | VacioException | NullException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return lista2;
    }


    private Country obtenerPais(String nombrePais) {
        if (nombrePais.equals("Côte d'Ivoire")) {
            return (Country) paises.get("Cote d'Ivoire");
        }
        return (Country) paises.get(nombrePais);
    }


    private void actualizarTablaLanding(Landing landing, Vertex vertice) {
        try {
            ILista elementopc = (ILista) landingidtabla.get(landing.getLandingId());
            if (elementopc == null) {
                ILista valores = new ArregloDinamico(1);
                valores.insertElement(vertice, valores.size() + 1);

                landingidtabla.put(landing.getLandingId(), valores);

            } else if (elementopc != null) {
                elementopc.insertElement(vertice, elementopc.size() + 1);
            }
        } catch (PosException | NullException e) {
            e.printStackTrace();
        }
    }

    private void actualizarNombreCodigo(ITablaSimbolos nombrecodigo, Landing landing1) {
        ILista elementopc = (ILista) nombrecodigo.get(landing1.getLandingId());

        if (elementopc == null) {
            String nombre = landing1.getName();
            String codigo = landing1.getLandingId();

            nombrecodigo.put(nombre, codigo);

        }
    }


    private void procesarConexiones(String origin, String destination, String cableid) {
        Landing landing1 = (Landing) points.get(origin);
        Vertex vertice1 = graphManager.agregarVertice(cableid, landing1);

        Landing landing2 = (Landing) points.get(destination);
        Vertex vertice2 = graphManager.agregarVertice(cableid, landing2);

        String nombrepais1 = landing1.getPais();
        String nombrepais2 = landing2.getPais();

        Country pais1 = obtenerPais(nombrepais1);
        Country pais2 = obtenerPais(nombrepais2);

        graphManager.agregarAristaPaisALanding(pais1, landing1, landing1, cableid);
        graphManager.agregarAristaPaisALanding(pais2, landing1, landing2, cableid);
        graphManager.agregarOActualizarArista(landing1, landing2, cableid);

        actualizarTablaLanding(landing1, vertice1);
        actualizarTablaLanding(landing2, vertice2);
        actualizarNombreCodigo(nombrecodigo, landing1);

    }


    public void cargar() throws IOException {
        List<Country> countries = dataLoader.cargarPaises("./data/countries.csv");
        for (Country pais : countries) {
            grafo.insertVertex(pais.getCapitalName(), pais);
            paises.put(pais.getCountryName(), pais);
        }

        List<Landing> landings = dataLoader.cargarLandingPoints("./data/landing_points.csv");
        for (Landing landing : landings) {
            points.put(landing.getLandingId(), landing);
        }

        List<String[]> conexiones = dataLoader.cargarConexiones("./data/connections.csv");
        for (String[] conexion : conexiones) {
            procesarConexiones(conexion[0], conexion[1], conexion[2]);
        }

        graphManager.conectarVerticesMismosClusters(landingidtabla);
    }
}
