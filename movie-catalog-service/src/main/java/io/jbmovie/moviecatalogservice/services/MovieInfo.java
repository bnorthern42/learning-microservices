package io.jbmovie.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.jbmovie.moviecatalogservice.models.CatalogItem;
import io.jbmovie.moviecatalogservice.models.Movie;
import io.jbmovie.moviecatalogservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MovieInfo {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem",
            threadPoolKey = "movieInfoPool", // bulkheads
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "20"), //at most 20 threads in thread pool
                    @HystrixProperty(name = "maxQueueSize", value = "10") //at most 10 threads can wait
            },
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "CircuitBreaker.requestVolumeThreshold", value = "10"),
                    @HystrixProperty(name = "CircuitBreaker.errorThresholdPercentage", value = "50"),
                    @HystrixProperty(name = "CircuitBreaker.sleepWindowInMilliseconds", value = "5000")})
    public CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
        return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
    }

    public CatalogItem getFallbackCatalogItem(Rating rating) {
        return new CatalogItem("Movie Name not found", "", rating.getRating());


    }

}
/*            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "CircuitBreaker.requestVolumeThreshold", value = "10"),
                    @HystrixProperty(name = "CircuitBreaker.errorThresholdPercentage", value = "50"),
                    @HystrixProperty(name = "CircuitBreaker.sleepWindowInMilliseconds", value = "5000")}*/