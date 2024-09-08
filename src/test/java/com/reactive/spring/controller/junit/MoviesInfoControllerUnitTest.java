package com.reactive.spring.controller.junit;

import com.reactive.spring.controller.MoviesInfoController;
import com.reactive.spring.domain.MovieInfo;
import com.reactive.spring.services.MovieInfoServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private MovieInfoServices movieInfoServices;

    String MOVIE_INFO_URI="/v1/movieinfos";


    @Test
    void getAllMoviesInfo(){
        var movieinfos = List.of(new MovieInfo( null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        when(movieInfoServices.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieinfos));
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
    void getMovieInfoById(){
        var movieInfo= new MovieInfo(null, "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        var movieInfoId=" ";
        when(movieInfoServices.getMovieInfoById(any())).thenReturn(Mono.just(new MovieInfo("abc", "Kali",
                2024, List.of("Amitab Bachan", "Depika Padkon"), LocalDate.parse("2024-06-24"))));
        webTestClient
                .get()
                .uri(MOVIE_INFO_URI +"/{id}",movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                   var movieInfo1=movieInfoEntityExchangeResult.getResponseBody();
                     assertNotNull(movieInfo1);
              });
    }

    @Test
    void addMovieInfo(){

        var movieInfo= new MovieInfo("abc", "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        when(movieInfoServices.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(new MovieInfo("mockId", "Kali",
                2024, List.of("Amitab Bachan", "Depika Padkon"), LocalDate.parse("2024-06-24"))));

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
                    assertEquals("mockId",saveMovieInfo.getMovieInfoId());
                });
    }

    @Test
    void addMovieInfo_validation(){

        var movieInfo= new MovieInfo("abc", "Khali",
                2012, List.of("Amitab Bachan", "Depika Padkon"), LocalDate.parse("2012-07-20"));
       /* when(movieInfoServices.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(new MovieInfo("mockId", "Kali",
                2024, List.of("Amitab Bachan", "Depika Padkon"), LocalDate.parse("2024-06-24"))));*/

        webTestClient
                .post()
                .uri(MOVIE_INFO_URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var result=stringEntityExchangeResult.getResponseBody();
                    System.out.println("Response:- "+ result);
                    assert result !=null;
                })
                /*.isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var saveMovieInfo=movieInfoEntityExchangeResult.getResponseBody();
                    assert saveMovieInfo !=null;
                    assert saveMovieInfo.getMovieInfoId() !=null;
                    assertEquals("mockId",saveMovieInfo.getMovieInfoId());
                })*/;
    }

    @Test
    void updateMovieInfo() {
        var movieInfoId="abc";
        var movieInfo=new MovieInfo( movieInfoId, "Dark Knight Rises",
                2024, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2005-06-15"));

        when(movieInfoServices.updateMovieInfo(isA(MovieInfo.class),isA(String.class))).thenReturn(Mono.just(new MovieInfo(movieInfoId, "Kali",
                2024, List.of("Amitab Bachan", "Depika Padkon"), LocalDate.parse("2024-06-24"))));

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
    void deleteMovieInfo(){
        var movieInfoId="abc";
         when(movieInfoServices.deleteMovieInfo(movieInfoId)).thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(MOVIE_INFO_URI +"/{id}",movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
