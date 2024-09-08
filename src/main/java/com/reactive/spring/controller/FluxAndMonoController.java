package com.reactive.spring.controller;

import com.mongodb.MongoNodeIsRecoveringException;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> flux(){
        System.out.println("Hey i am in flux:-");
        return Flux.just(1,2,3,4)
                .log();
    }

    @GetMapping("/mono")
    public Mono<String> mono(){
        System.out.println("Hey i am in Mono:-");
        return Mono.just("Amit")
                .log();
    }
    @GetMapping(value = "/stream" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> stream(){
        System.out.println("Hey i am in stream:-");
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }


}//class
