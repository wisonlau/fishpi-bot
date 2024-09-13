package io.dannio.fishpi.api;

import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@Path("api")
@Controller
public class Api {

    @GET
    @Path("/generate_204")
    public Response foo() {

        return Response.status(NO_CONTENT).build();
    }
}
