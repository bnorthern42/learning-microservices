package io.jbmovie.moviecatalogservice.resources;



import com.netflix.discovery.DiscoveryClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.jbmovie.moviecatalogservice.models.CatalogItem;
import io.jbmovie.moviecatalogservice.models.Movie;
import io.jbmovie.moviecatalogservice.models.Rating;
import io.jbmovie.moviecatalogservice.models.UserRating;
import io.jbmovie.moviecatalogservice.services.MovieInfo;
import io.jbmovie.moviecatalogservice.services.UserRatingInfo;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;


import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    MovieInfo movieinfo;

    @Autowired
    UserRatingInfo userRatingInfo;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
        //get all rated movie ID's
        UserRating userRating = userRatingInfo.getUserRating(userId);
        return userRating.getRatings().stream()
                .map(rating -> movieinfo.getCatalogItem(rating))
                .collect(Collectors.toList());
    }


}

//New Way of doing this, we will have to do this, or find a way to use VueJS instead of React. VueJS is more fun IMO.
        /*like a promise of a thing <- MONO
        return ratings.stream().map(rating -> {
            Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
            return new CatalogItem(movie.getName(), "desc", rating.getRating());
        }).collect(Collectors.toList());

         */



