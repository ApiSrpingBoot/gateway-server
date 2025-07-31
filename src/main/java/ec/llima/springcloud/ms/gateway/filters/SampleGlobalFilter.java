package ec.llima.springcloud.ms.gateway.filters;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /*
         * filrtros para modificar antesde  la respuesta
         */

        logger.info("LOG: Ejecutando el flitro antes del request");
        exchange.getRequest().mutate().headers(h -> h.add("token", "dedededed"));
        // Reemplazar el request con el nuevo header
        /*
         * filrtros para modificar la respuesta
         */
        return chain.filter(exchange).then(Mono.fromRunnable(()->{
            logger.info("LOG: Ejecutando el flitro POST response");
            //String token = exchange.getRequest().getHeaders().get("token").get(0);
            String token = exchange.getRequest().getHeaders().get("token") != null ? exchange.getRequest().getHeaders().get("token").get(0) : "no-token";
            logger.info("LOG: token -> "+token );

            Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(tokenv ->{
                logger.info("LOG: tokenv -> "+tokenv );
                exchange.getResponse().getHeaders().add("token", tokenv);
            });;

            exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());
            //este era un ejemplo para devolver un texto plano en la respuesta en lugar de JSON
            //exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        }));
    }
    @Override
    public int getOrder() {
        return 100;
    }


}
