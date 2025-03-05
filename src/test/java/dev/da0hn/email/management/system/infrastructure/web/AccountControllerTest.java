package dev.da0hn.email.management.system.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.da0hn.email.management.system.core.ports.api.AccountService;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.infrastructure.web.error.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private AccountService accountService;

  @BeforeEach
  void setUp() {
      this.accountService = mock(AccountService.class);
      this.objectMapper = new ObjectMapper();
      this.mockMvc = MockMvcBuilders
        .standaloneSetup(new AccountController(accountService))
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void shouldCreateNewAccount() throws Exception {
      final var jsonContent = """
          {
            "name": "John Doe",
            "credentials": {
              "email": "john@example.com",
              "password": "password123"
            },
            "connectionDetails": {
              "host": "smtp.example.com",
              "port": 587,
              "protocol": "smtp"
            }
          }
          """;

      mockMvc.perform(
          post("/api/v1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonContent)
        )
        .andExpect(status().isCreated());

      verify(accountService).createAccount(any(NewAccountInput.class));
  }

  @Test
  void shouldReturnErrorWhenEmailIsInvalid() throws Exception {
      final var jsonContent = """
          {
            "name": "John Doe",
            "credentials": {
              "email": "invalid-email",
              "password": "password123"
            },
            "connectionDetails": {
              "host": "smtp.example.com",
              "port": 587,
              "protocol": "smtp"
            }
          }
          """;

      mockMvc.perform(
          post("/api/v1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonContent)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Validation Error")))
        .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
        .andExpect(jsonPath("$.fieldErrors[0].field", is("credentials.email")))
        .andExpect(jsonPath("$.fieldErrors[0].message", is("Invalid email format")));
  }

  @Test
  void shouldReturnErrorWhenPasswordIsTooShort() throws Exception {
      final var jsonContent = """
          {
            "name": "John Doe",
            "credentials": {
              "email": "john@example.com",
              "password": "123"
            },
            "connectionDetails": {
              "host": "smtp.example.com",
              "port": 587,
              "protocol": "smtp"
            }
          }
          """;

      mockMvc.perform(
          post("/api/v1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonContent)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Validation Error")))
        .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
        .andExpect(jsonPath("$.fieldErrors[0].field", is("credentials.password")))
        .andExpect(jsonPath("$.fieldErrors[0].message", is("Password must be at least 8 characters long")));
  }

  @Test
  void shouldReturnErrorWhenPortIsInvalid() throws Exception {
      final var jsonContent = """
          {
            "name": "John Doe",
            "credentials": {
              "email": "john@example.com",
              "password": "password123"
            },
            "connectionDetails": {
              "host": "smtp.example.com",
              "port": 70000,
              "protocol": "smtp"
            }
          }
          """;

      mockMvc.perform(
          post("/api/v1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonContent)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Validation Error")))
        .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
        .andExpect(jsonPath("$.fieldErrors[0].field", is("connectionDetails.port")))
        .andExpect(jsonPath("$.fieldErrors[0].message", is("Port must be between 1 and 65535")));
  }

  @Test
  void shouldReturnErrorWhenProtocolIsInvalid() throws Exception {
      final var jsonContent = """
          {
            "name": "John Doe",
            "credentials": {
              "email": "john@example.com",
              "password": "password123"
            },
            "connectionDetails": {
              "host": "smtp.example.com",
              "port": 587,
              "protocol": "invalid"
            }
          }
          """;

      mockMvc.perform(
          post("/api/v1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonContent)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Validation Error")))
        .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
        .andExpect(jsonPath("$.fieldErrors[0].field", is("connectionDetails.protocol")))
        .andExpect(jsonPath("$.fieldErrors[0].message", is("Protocol must be one of: smtp, imap, pop3")));
  }

  @Test
  void shouldReturnErrorWhenRequiredFieldsAreMissing() throws Exception {
      final var jsonContent = """
          {
            "credentials": {
              "password": "password123"
            },
            "connectionDetails": {
              "port": 587
            }
          }
          """;

      mockMvc.perform(
          post("/api/v1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonContent)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Validation Error")))
        .andExpect(jsonPath("$.fieldErrors", hasSize(4)))
        .andExpect(jsonPath("$.fieldErrors[*].field").exists())
        .andExpect(jsonPath("$.fieldErrors[*].message").exists());
  }


}
