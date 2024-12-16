package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.stereotype.Controller;


@Controller
public class UserApiController implements UserApi {
    public UserApiController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public UserApiController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private final ObjectMapper objectMapper;
}