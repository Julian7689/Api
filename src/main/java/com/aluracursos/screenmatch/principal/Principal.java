package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepositori;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;



public class Principal {
    //variables globales
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6e25a63d"; // Asegúrate de usar el formato correcto
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();

    private SerieRepositori repositorio;
    private List<Serie> series;

    public Principal(SerieRepositori repositori) {
        this.repositorio = repositori;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar serie por titulo
                    5 - Top 5 series
                    6 - Buscar serie por categoria 
                    7 - Filtrar series
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;

                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:    
                    top5Series();

                    break;
                case 6:
                    buscarSeriesporcategoria();

                    break;
                case 7:
                    filtrarSeries();
                    
                    
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }




    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        // Construcción correcta de la URL
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie de la cual quieres ver los episodios");
        var nombreSerie = teclado.nextLine();


        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

                if (serie.isPresent()){
                    var serieEncontrada = serie.get();

                    List<DatosTemporadas> temporadas = new ArrayList<>();

                    for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                        // Construcción correcta de la URL con la temporada
                        var json = consumoApi.obtenerDatos(
                                URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY
                        );
                        DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                        temporadas.add(datosTemporada);
                    }
                    temporadas.forEach(System.out::println);
                    List<Episodio> episodios = temporadas.stream()
                            .flatMap(d ->d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                            .collect(Collectors.toList());
                    serieEncontrada.setEpisodios(episodios);
                    repositorio.save(serieEncontrada);

                }

                }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        //datosSeries.add(datos);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
         series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
    private void buscarSeriePorTitulo() {
        System.out.println("Escribe el nombre de la serie que quieres buscar");
        var nombreSerie = teclado.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);


        if (serieBuscada.isPresent()){
            System.out.println("La serie buscada es :  " + serieBuscada.get());
        }else {
            System.out.println("Serie no encontrada");
        }
    }
    private void top5Series() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s -> System.out.println("Serie: " + s.getTitulo() + "  Evaluacion-" + s.getEvaluacion()));
    }

    private void buscarSeriesporcategoria() {
        System.out.println("Escribe el genero / categoria de la serie que deseas buscar: ");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria); // Corregido typo
        System.out.println("Las series de la categoría " + genero + " son:");
        seriesPorCategoria.forEach(System.out::println);
    }
    private void filtrarSeries() {
        System.out.println("¿Filtrar series por numero de temporadas?" );
        var totalDetemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("¿Com evaluación apartir de cuál valor? ");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();
        List<Serie> filtrado = repositorio.
        System.out.println("** series filtradas **");
        filtrado.forEach(s -> System.out.println(s.getTitulo() + "  - evaluacion: " + s.getEvaluacion()));
    }

}




