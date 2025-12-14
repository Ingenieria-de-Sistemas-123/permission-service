package com.snippetsearcher.permission;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.snippetsearcher.permission.config.GlobalExceptionHandler;
import com.snippetsearcher.permission.web.errors.ConflictException;
import com.snippetsearcher.permission.web.errors.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

  @RestController
  static class BoomController {
    @GetMapping("/boom/notfound")
    String nf() {
      throw new NotFoundException("nope");
    }

    @GetMapping("/boom/conflict")
    String cf() {
      throw new ConflictException("dup");
    }

    @GetMapping("/boom/other")
    String other() {
      throw new IllegalStateException("kaboom");
    }
  }

  private final MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new BoomController())
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @Test
  void notFound_maps_to_404_with_error_message() throws Exception {
    mvc.perform(get("/boom/notfound"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("nope"));
  }

  @Test
  void conflict_maps_to_409_with_error_message() throws Exception {
    mvc.perform(get("/boom/conflict"))
        .andExpect(status().isConflict())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("dup"));
  }

  @Test
  void otherException_maps_to_500_with_expected_payload() throws Exception {
    mvc.perform(get("/boom/other"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("Unexpected error"))
        .andExpect(jsonPath("$.exception").value("IllegalStateException"));
  }
}
