 package uk.co.aipainappserver.config;

 import uk.co.aipainappserver.users.infrastructure_layer.AuthInterceptor;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
 import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

 
     @Configuration
     public class WebConfig implements WebMvcConfigurer {

         @Override
         public void addInterceptors(InterceptorRegistry registry) {
             registry.addInterceptor(new AuthInterceptor());
         }

 }
