package com.reactive.spring.repository;

import com.reactive.spring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo( null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)//.log()
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll(){
        var moviesInfoRepo=movieInfoRepository.findAll().log();

        StepVerifier.create(moviesInfoRepo)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById(){
        var movieInfoMono=movieInfoRepository.findById("abc").log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void saveMovieInfo(){
        var movieInfo=new MovieInfo( null, "Khali",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2024-07-03"));

        var movieInfoMono1=movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(movieInfoMono1)
                .assertNext(movieInfo1 -> {
                    assertNotNull(movieInfo1.getMovieInfoId());
                    assertEquals("Khali",movieInfo1.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo(){
        var movieInfoMono=movieInfoRepository.findById("abc").block();
        movieInfoMono.setName("Khali");
        movieInfoMono.setYear(2024);

        var movieInfoMono1=movieInfoRepository.save(movieInfoMono).log();

        StepVerifier.create(movieInfoMono1)
                .assertNext(movieInfo1 -> {
                      assertEquals("Khali",movieInfo1.getName());
                })
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo(){
        movieInfoRepository.deleteById("abc").block();
        var movieInfoMono=movieInfoRepository.findAll().log();

        StepVerifier.create(movieInfoMono)
                .expectNextCount(2)
                .verifyComplete();
    }

}