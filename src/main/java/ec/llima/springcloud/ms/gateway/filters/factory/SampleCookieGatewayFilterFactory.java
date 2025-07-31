package ec.llima.springcloud.ms.gateway.filters.factory;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/*
 * GatewayFilterFactory es una interfaz en Spring Cloud Gateway, un proyecto de Spring para crear gateways de 
 * API. Sirve para crear filtros personalizados que se aplican a las peticiones HTTP que pasan por el gateway. 
 * Estos filtros se ejecutan antes o después de enrutar la solicitud al servicio backend.
 * en el .yml se define el filtro con su nombre y sus argumentos,
 * como en este caso "SampleCookie" con los argumentos "cookieName", "cookieValue" y "message".
 * El filtro se aplica a las solicitudes que coinciden con las rutas definidas en el archivo de configuración.
 * El filtro puede modificar la solicitud o la respuesta, agregar cookies,
 * o realizar otras acciones antes de que la solicitud sea procesada por el servicio backend.
 * el filtro se pruede aplicar al microsservicio que deseamos, en este caso al servicio de productos.
 */
@Component
public class SampleCookieGatewayFilterFactory extends AbstractGatewayFilterFactory<SampleCookieGatewayFilterFactory.configurationCookie>{

    private final Logger logger = LoggerFactory.getLogger(SampleCookieGatewayFilterFactory.class);
    

    public SampleCookieGatewayFilterFactory() {
        super(configurationCookie.class);
    }

    @Override
    public GatewayFilter apply(configurationCookie config) {
        // el OrderedGatewayFilter permite definir el orden del filtro
        // el primer parametro es una funcion que recibe el exchange y el chain, 
        // y devuelve un Mono<Void> que representa la ejecución del filtro
        // el segundo parametro es el orden del filtro, en este caso 100
        return new OrderedGatewayFilter((exchange, chain) -> {
            logger.info("LOG: Ejecutando el filtro pre GatewayFilterFactory: {}", config);

            // Aquí puedes agregar la lógica del filtro
            // Por ejemplo, agregar una cookie a la respuesta
            //exchange.getResponse().addCookie(ResponseCookie.from("sampleCookie", "cookieValue").build());
            //return chain.filter(exchange);
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                Optional.ofNullable(config.getCookieName()).ifPresent(cookieName -> {
                    logger.info("LOG: Adding cookie with name: {}", cookieName);
                    exchange.getResponse().getCookies().add(cookieName, 
                        ResponseCookie.from(cookieName, config.getCookieValue()).build());
                });
                logger.info("LOG: Ejecutando el filtro post GatewayFilterFactory: {}", config);
            }));
        },100); // El número 100 define el orden del filtro, puedes ajustarlo según tus necesidades
    }

    /*
     * de esta manera se define el orden de los campos que se van a utilizar en el filtro
     * en el .yml se podria el filtro de la siguiente manera:
     * - SampleCookie=cookieName, cookieValue, cookieMessage
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("cookieName", "cookieValue", "cookieMessage");
    }

    /*
     * en el caso que se quiera definir un nombre para el filtro
     * se puede sobreescribir el metodo name, de esta manera se define el nombre del filtro
     * en el .yml se podria definir de la siguiente manera:
     * - SampleCookieNewName=cookieName, cookieValue, cookieMessage
     */
    @Override
    public String name() {
        //nombre igual que la clase 
        //return "SampleCookie";
        return "SampleCookieNewName";
    }


    public static class  configurationCookie {
        // Aquí puedes definir la configuración del filtro si es necesario
        private String cookieName;
        private String cookieValue;
        private String cookieMessage;
        public String getCookieName() {
            return cookieName;
        }
        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }
        public String getCookieValue() {
            return cookieValue;
        }
        public void setCookieValue(String cookieValue) {
            this.cookieValue = cookieValue;
        }
        public String getCookieMessage() {
            return cookieMessage;
        }
        public void setCookieMessage(String cookieMessage) {
            this.cookieMessage = cookieMessage;
        }
        
    }

}
