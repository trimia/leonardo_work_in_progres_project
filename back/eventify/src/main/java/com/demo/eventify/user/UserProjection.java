package com.demo.eventify.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/*
    projection are used to give to front some data form specific class
    request like /?projection=pippo
 */
//@Projection()
@Projection(name="pippo",types={UserEntity.class})
public interface UserProjection {
    /*
        creo i getter nel nome del metodo ci deve essere
         il nome della variabile da reperire e lo fanno in automatico
     */
    String getFirstName();

    @Value("#{target.UserEntity.Event.name}")
    String getname();
    /*
        ? after key expand next key only if previous exist

        hide from final jason complete list and create in default a custom jason to receive
     */
//    @Value("#{target.UserEntity.Event?.tag}")
//    @JsonIgnore
//    List<EventTag> getpippo();
//
//    default int gethowmanytag(){
//        List<EventTag> tag = getpippo();
//        //like custom query you can manipulate data and obtain in json only what you want
//        return tag.size;
//    }
}
