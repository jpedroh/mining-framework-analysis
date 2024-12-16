package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.stereotype.Controller;


@Controller
public class FakeApiController implements FakeApi {
    public FakeApiController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public FakeApiController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private final ObjectMapper objectMapper;
}