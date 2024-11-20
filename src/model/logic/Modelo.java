package model.logic;

import java.io.IOException;
import java.util.Comparator;

import model.data_structures.ArregloDinamico;
import model.data_structures.Country;
import model.data_structures.Country.ComparadorXKm;
import model.data_structures.Edge;
import model.data_structures.GrafoListaAdyacencia;
import model.data_structures.ILista;
import model.data_structures.ITablaSimbolos;
import model.data_structures.Landing;
import model.data_structures.NodoTS;
import model.data_structures.NullException;
import model.data_structures.PilaEncadenada;
import model.data_structures.PosException;
import model.data_structures.TablaHashLinearProbing;
import model.data_structures.TablaHashSeparteChaining;
import model.data_structures.VacioException;
import model.data_structures.Vertex;
import model.data_structures.YoutubeVideo;
import utils.DistanceCalculator;
import utils.Ordenamiento;


/**
 * Definicion del modelo del mundo
 *
 */
public class Modelo {
	/**
	 * Atributos del modelo del mundo
	 */
	private ILista datos;
	private GrafoListaAdyacencia grafo;
	private ITablaSimbolos<String, Country> paises;
	private ITablaSimbolos<String, Landing> points;
	private ITablaSimbolos<String, Vertex<String, Landing>> landingidtabla;
	private ITablaSimbolos<String, String> nombrecodigo;

	/**
	 * Constructor del modelo del mundo con capacidad dada
	 * @param capacidad
	 */
	public Modelo(int capacidad)
	{
		datos = new ArregloDinamico<>(capacidad);
	}

	/**
	 * Servicio de consulta de numero de elementos presentes en el modelo 
	 * @return numero de elementos presentes en el modelo
	 */
	public int darTamano()
	{
		return datos.size();
	}


	/**
	 * Requerimiento buscar dato
	 * @param i Dato a buscar
	 * @return dato encontrado
	 * @throws VacioException 
	 * @throws PosException 
	 */
	public YoutubeVideo getElement(int i) throws PosException, VacioException
	{
		return (YoutubeVideo) datos.getElement( i);
	}

	public String toString()
	{
		String fragmento="Info básica:";
		
		fragmento+= "\n El número total de conexiones (arcos) en el grafo es: " + grafo.edges().size();
		fragmento+="\n El número total de puntos de conexión (landing points) en el grafo: " + grafo.vertices().size();
		fragmento+= "\n La cantidad total de países es:  " + paises.size();
		Landing landing=null;
		try 
		{
			landing = (Landing) ((NodoTS) points.darListaNodos().getElement(1)).getValue();
			fragmento+= "\n Info primer landing point " + "\n Identificador: " + landing.getId() + "\n Nombre: " + landing.getName()
			+ " \n Latitud " + landing.getLatitude() + " \n Longitud" + landing.getLongitude();
			
			Country pais= (Country) ((NodoTS) paises.darListaNodos().getElement(paises.darListaNodos().size())).getValue();
			
			fragmento+= "\n Info último país: " + "\n Capital: "+ pais.getCapitalName() + "\n Población: " + pais.getPopulation()+
			"\n Usuarios: "+ pais.getUsers();
		} 
		catch (PosException | VacioException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fragmento;

	}


	public String req1String(String punto1, String punto2) {
		// Inicializar el analizador de clústeres con los atributos necesarios
		ClusterAnalyzer analyzer = new ClusterAnalyzer(grafo.getSSC(), nombrecodigo, landingidtabla); // Fix this line

		// Obtener el número máximo de componentes conectados
		int maxConnectedComponents = analyzer.getMaxConnectedComponents();
		String fragmento = "La cantidad de componentes conectados es: " + maxConnectedComponents;

		// Verificar si los puntos pertenecen al mismo clúster
		boolean sameCluster = analyzer.arePointsInSameCluster(punto1, punto2);
		if (sameCluster) {
			fragmento += "\n Los landing points pertenecen al mismo clúster";
		} else {
			fragmento += "\n Los landing points no pertenecen al mismo clúster";
		}

		return fragmento;
	}
	
	public String req2String()	{
		String fragmento="";
		ILista lista= landingidtabla.valueSet();
		int cantidad=0;
		int contador=0;
		
		for(int i=1; i<= lista.size(); i++) {
			try {
				if( ( (ILista) lista.getElement(i) ).size()>1 && contador<=10)
				{
					Landing landing= (Landing) ((Vertex) ((ILista) lista.getElement(i) ).getElement(1)).getInfo();
					
					for(int j=1; j<=((ILista) lista.getElement(i)).size(); j++)
					{
						cantidad+= ((Vertex) ((ILista) lista.getElement(i)).getElement(j)).edges().size();
					}
					fragmento+= "\n Landing " + "\n Nombre: " + landing.getName() + "\n País: " + landing.getPais() + "\n Id: " + landing.getId() + "\n Cantidad: " + cantidad;
					contador++;
				}
			}
			catch (PosException | VacioException e) {
				e.printStackTrace();
			}
		}
		return fragmento;
	}

	public String req3String(String pais1, String pais2) {
		Country paisOrigen = paises.get(pais1);
		Country paisDestino = paises.get(pais2);

		if (paisOrigen == null || paisDestino == null) {
			return "Uno o ambos países no existen en el sistema.";
		}

		String capital1 = paisOrigen.getCapitalName();
		String capital2 = paisDestino.getCapitalName();

		PathAnalyzer pathAnalyzer = new PathAnalyzer(grafo);
		PilaEncadenada<Edge<String, Landing>> camino = pathAnalyzer.getShortestPath(capital1, capital2);

		if (camino == null || camino.isEmpty()) {
			return "No hay un camino entre las capitales de los países.";
		}

		StringBuilder fragmento = new StringBuilder("Ruta:");
		float distanciaTotal = 0;

		// Fail-safe: Contador de ciclos y límite máximo
		int contadorCiclos = 0;
		final int LIMITE_CICLOS = 100;

		while (!camino.isEmpty()) {
			contadorCiclos++;
			if (contadorCiclos > LIMITE_CICLOS) {
				fragmento.append("\nEl cálculo de la ruta excedió el límite de ciclos permitidos.");
				fragmento.append("\nPor favor, revise los datos de entrada o la configuración del grafo.");
				return fragmento.toString();
			}

			Edge<String, Landing> arco = camino.pop();
			Vertex<String, Landing> origen = arco.getSource();
			Vertex<String, Landing> destino = arco.getDestination();

			String nombreOrigen = origen.getInfo().getLandingId();
			String nombreDestino = destino.getInfo().getLandingId();

			float distancia = DistanceCalculator.calculateDistance(
					origen.getInfo().getLongitude(), origen.getInfo().getLatitude(),
					destino.getInfo().getLongitude(), destino.getInfo().getLatitude());
			distanciaTotal += distancia;

			fragmento.append(String.format("\nOrigen: %s  Destino: %s  Distancia: %.2f km",
					nombreOrigen, nombreDestino, distancia));
		}

		fragmento.append("\nDistancia total: ").append(distanciaTotal).append(" km");
		return fragmento.toString();
	}


	public String req4String() {
		StringBuilder fragmento = new StringBuilder();

		try {
			// Crear instancia de MSTAnalyzer
			MSTAnalyzer<String, Landing> mstAnalyzer = new MSTAnalyzer<>(grafo);

			// Encontrar el vértice inicial con mayor número de conexiones
			String initialVertex = mstAnalyzer.findInitialVertex(landingidtabla);
			if (initialVertex == null) {
				return "No se encontró un vértice inicial válido.";
			}

			// Calcular el MST
			ILista<Edge<String, Landing>> mstEdges = mstAnalyzer.calculateMST(initialVertex);
			int totalCost = 0;

			// Calcular costo total del MST
			for (int i = 1; i <= mstEdges.size(); i++) {
				totalCost += mstEdges.getElement(i).getWeight();
			}

			// Calcular la rama más larga
			PilaEncadenada<Vertex<String, Landing>> longestPath = mstAnalyzer.findLongestPath(mstEdges);

			// Construir resumen
			fragmento.append("La cantidad de nodos conectados a la red de expansión mínima es: ")
					.append(mstEdges.size())
					.append("\nEl costo total es de: ")
					.append(totalCost);

			fragmento.append("\nLa rama más larga está dada por los vértices:");
			int i = 1;
			while (!longestPath.isEmpty()) {
				Vertex<String, Landing> vertex = longestPath.pop();
				fragmento.append("\nId ").append(i++).append(" : ").append(vertex.getId());
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "Ocurrió un error al calcular el MST.";
		}

		return fragmento.toString();
	}
	
	public ILista req5(String punto)
	{
		String codigo= (String) nombrecodigo.get(punto);
		ILista lista= (ILista) landingidtabla.get(codigo);
		
		ILista countries= new ArregloDinamico<>(1);
		try 
		{
			Country paisoriginal=(Country) paises.get(((Landing) ((Vertex)lista.getElement(1)).getInfo()).getPais());
			countries.insertElement(paisoriginal, countries.size() + 1);
		} 
		catch (PosException | VacioException | NullException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(int i=1; i<= lista.size(); i++)
		{
			try 
			{
				Vertex vertice= (Vertex) lista.getElement(i);
				ILista arcos= vertice.edges();
				
				for(int j=1; j<= arcos.size(); j++)
				{
					Vertex vertice2= ((Edge) arcos.getElement(j)).getDestination();
					
					Country pais=null;
					if (vertice2.getInfo().getClass().getName().equals("model.data_structures.Landing"))
					{
						Landing landing= (Landing) vertice2.getInfo();
						pais= (Country) paises.get(landing.getPais());
						countries.insertElement(pais, countries.size() + 1);
						
						float distancia= distancia(pais.getLongitude(), pais.getLatitude(), landing.getLongitude(), landing.getLatitude());
							
						pais.setDistlan(distancia);
					}
					else
					{
						pais=(Country) vertice2.getInfo();
					}
				}
				
			} catch (PosException | VacioException | NullException e) 
			{
				e.printStackTrace();
			}
		}
		
		ILista unificado= unificar(countries, "Country");
		
		Comparator<Country> comparador=null;

		Ordenamiento<Country> algsOrdenamientoEventos=new Ordenamiento<Country>();

		comparador= new ComparadorXKm();

		try 
		{

			if (lista!=null)
			{
				algsOrdenamientoEventos.ordenarMergeSort(unificado, comparador, true);
			}	
		}
		catch (PosException | VacioException| NullException  e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return unificado;
		
		
	}
	
	public String req5String(String punto)
	{
		ILista afectados= req5(punto);
		
		String fragmento="La cantidad de paises afectados es: " + afectados.size() + "\n Los paises afectados son: ";
	
		for(int i=1; i<=afectados.size(); i++)
		{
			try {
				fragmento+= "\n Nombre: " + ((Country) afectados.getElement(i)).getCountryName() + "\n Distancia al landing point: " + ((Country) afectados.getElement(i)).getDistlan();
			} catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return fragmento;
		
		
	}
	
	public ILista unificar(ILista lista, String criterio)
	{

		ILista lista2=new ArregloDinamico(1);

		if(criterio.equals("Vertice"))
		{
			Comparator<Vertex<String, Landing>> comparador=null;

			Ordenamiento<Vertex<String, Landing>> algsOrdenamientoEventos=new Ordenamiento<Vertex<String, Landing>>();;

			comparador= new Vertex.ComparadorXKey();


			try 
			{

				if (lista!=null)
				{
					algsOrdenamientoEventos.ordenarMergeSort(lista, comparador, false);

					for(int i=1; i<=lista.size(); i++)
					{
						Vertex actual= (Vertex) lista.getElement(i);
						Vertex siguiente= (Vertex) lista.getElement(i+1);

						if(siguiente!=null)
						{
							if(comparador.compare(actual, siguiente)!=0)
							{
								lista2.insertElement(actual, lista2.size()+1);
							}
						}
						else
						{
							Vertex anterior= (Vertex) lista.getElement(i-1);

							if(anterior!=null)
							{
								if(comparador.compare(anterior, actual)!=0)
								{
									lista2.insertElement(actual, lista2.size()+1);
								}
							}
							else
							{
								lista2.insertElement(actual, lista2.size()+1);
							}
						}

					}
				}
			} 
			catch (PosException | VacioException| NullException  e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			Comparator<Country> comparador=null;

			Ordenamiento<Country> algsOrdenamientoEventos=new Ordenamiento<Country>();;

			comparador= new Country.ComparadorXNombre();

			try 
			{

				if (lista!=null)
				{
					algsOrdenamientoEventos.ordenarMergeSort(lista, comparador, false);
				}

					for(int i=1; i<=lista.size(); i++)
					{
						Country actual= (Country) lista.getElement(i);
						Country siguiente= (Country) lista.getElement(i+1);

						if(siguiente!=null)
						{
							if(comparador.compare(actual, siguiente)!=0)
							{
								lista2.insertElement(actual, lista2.size()+1);
							}
						}
						else
						{
							Country anterior= (Country) lista.getElement(i-1);

							if(anterior!=null)
							{
								if(comparador.compare(anterior, actual)!=0)
								{
									lista2.insertElement(actual, lista2.size()+1);
								}
							}
							else
							{
								lista2.insertElement(actual, lista2.size()+1);
							}
						}

					}
				}
			
			catch (PosException | VacioException| NullException  e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return lista2;
	}
	
	public ITablaSimbolos unificarHash(ILista lista)
	{

		Comparator<Vertex<String, Landing>> comparador=null;

		Ordenamiento<Vertex<String, Landing>> algsOrdenamientoEventos=new Ordenamiento<Vertex<String, Landing>>();;

		comparador= new Vertex.ComparadorXKey();
		
		ITablaSimbolos tabla= new TablaHashSeparteChaining<>(2);


		try 
		{

			if (lista!=null)
			{
				algsOrdenamientoEventos.ordenarMergeSort(lista, comparador, false);

				for(int i=1; i<=lista.size(); i++)
				{
					Vertex actual= (Vertex) lista.getElement(i);
					Vertex siguiente= (Vertex) lista.getElement(i+1);

					if(siguiente!=null)
					{
						if(comparador.compare(actual, siguiente)!=0)
						{
							tabla.put(actual.getId(), actual);
						}
					}
					else
					{
						Vertex anterior= (Vertex) lista.getElement(i-1);

						if(anterior!=null)
						{
							if(comparador.compare(anterior, actual)!=0)
							{
								tabla.put(actual.getId(), actual);
							}
						}
						else
						{
							tabla.put(actual.getId(), actual);
						}
					}

				}
			}
		} 
		catch (PosException | VacioException| NullException  e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tabla;
	}

	public void cargar() throws IOException {
		grafo = new GrafoListaAdyacencia(2);
		paises = new TablaHashLinearProbing<>(2);
		points = new TablaHashLinearProbing<>(2);
		landingidtabla = new TablaHashSeparteChaining<>(2);
		nombrecodigo = new TablaHashSeparteChaining<>(2);

		CountryLoader countryLoader = new CountryLoader();
		countryLoader.loadCountries(grafo, paises);

		LandingPointLoader landingPointLoader = new LandingPointLoader();
		landingPointLoader.loadLandingPoints(points);

		ConnectionLoader connectionLoader = new ConnectionLoader();
		connectionLoader.loadConnections(grafo, points, nombrecodigo, landingidtabla);
	}
	
	private static float distancia(double lon1, double lat1, double lon2, double lat2) 
	{

		double earthRadius = 6371; // km

		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		double dlon = (lon2 - lon1);
		double dlat = (lat2 - lat1);

		double sinlat = Math.sin(dlat / 2);
		double sinlon = Math.sin(dlon / 2);

		double a = (sinlat * sinlat) + Math.cos(lat1)*Math.cos(lat2)*(sinlon*sinlon);
		double c = 2 * Math.asin (Math.min(1.0, Math.sqrt(a)));

		double distance = earthRadius * c;

		return (int) distance;

	}


}
