package com.reactive.spring.controller.intg;

import com.reactive.spring.domain.MovieInfo;
import com.reactive.spring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    String MOVIE_INFO_URI="/v1/movieinfos";

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
    void addMovieInfo() {
        var movieInfo=new MovieInfo( null, "Kali",
                2005, List.of("Amitab Bachan", "Prabhash"), LocalDate.parse("2005-06-15"));

        webTestClient
                .post()
                .uri(MOVIE_INFO_URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                     var saveMovieInfo=movieInfoEntityExchangeResult.getResponseBody();
                     assert saveMovieInfo !=null;
                     assert saveMovieInfo.getMovieInfoId() !=null;

                        });
    }

    @Test
    void getAllMovieInfos(){
        webTestClient
                .get()
                .uri(MOVIE_INFO_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getAllMovieInfosByYear(){

        var uri=UriComponentsBuilder.fromUriString(MOVIE_INFO_URI)
                        .queryParam("year",2005)
                        .buildAndExpand().toUri();

        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getMovieInfoById(){
        var movieInfoId="abc";
        webTestClient
                .get()
                .uri(MOVIE_INFO_URI +"/{id}",movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
//                .expectBody(MovieInfo.class)
//                .consumeWith(movieInfoEntityExchangeResult -> {
//                   var movieInfo=movieInfoEntityExchangeResult.getResponseBody();
//                   assertNotNull(movieInfo);
//                });
    }

    @Test
    void updateMovieInfo() {
        var movieInfoId="abc";
        var movieInfo=new MovieInfo( null, "Kali",
                2024, List.of("Amitab Bachan", "Prabhash"), LocalDate.parse("2005-06-15"));

        webTestClient
                .put()
                .uri(MOVIE_INFO_URI +"/{id}",movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updateMovieInfo=movieInfoEntityExchangeResult.getResponseBody();
                    assert updateMovieInfo !=null;
                    assert updateMovieInfo.getMovieInfoId() !=null;
                    assertEquals("Kali",updateMovieInfo.getName());

                });
    }

    @Test
    void updateMovieInfo_notFound() {
        var movieInfoId="def";
        var movieInfo=new MovieInfo( null, "Kali",
                2024, List.of("Amitab Bachan", "Prabhash"), LocalDate.parse("2005-06-15"));

        webTestClient
                .put()
                .uri(MOVIE_INFO_URI +"/{id}",movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
               /* .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updateMovieInfo=movieInfoEntityExchangeResult.getResponseBody();
                    assert updateMovieInfo !=null;
                    assert updateMovieInfo.getMovieInfoId() !=null;
                    assertEquals("Kali",updateMovieInfo.getName());

                });*/
    }

    @Test
    void  findByYear(){

        var movieInfoFlux=movieInfoRepository.findByYear(2005).log();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo(){
        var movieInfoId="abc";

        webTestClient
                .delete()
                .uri(MOVIE_INFO_URI +"/{id}",movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}