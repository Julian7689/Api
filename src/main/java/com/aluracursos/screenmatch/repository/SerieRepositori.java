package com.aluracursos.screenmatch.repository;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepositori extends JpaRepository<Serie,Long>{
    Optional<Serie>findByTituloContainsIgnoreCase(String nombreSerie);


    List<Serie> findTop5ByOrderByEvaluacionDesc();
    List<Serie> findByGenero(Categoria categoria);
    //List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int totalTemporadas, Double evaluacion);
    @Query( value = "SELECT *\n" + "FROM series\n" + "WHERE series.total_temporadas <= 6 AND series.evaluacion >= 7.5;", nativeQuery = true)
    List<Serie>seriesPortemporadaEvaluacion(int totalTemporadas, Double evaluacion);

}
