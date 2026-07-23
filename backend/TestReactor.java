import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public class TestReactor {
    public static void main(String[] args) {
        Mono<String> m = Flux.empty()
            .next()
            .flatMap(x -> Mono.just("Found " + x))
            .defaultIfEmpty("Non assigné")
            .map(Object::toString); // Make sure type matches
            
        System.out.println("Result: " + m.block());
    }
}
