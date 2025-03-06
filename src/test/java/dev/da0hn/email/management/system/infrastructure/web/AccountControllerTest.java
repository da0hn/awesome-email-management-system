package dev.da0hn.email.management.system.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaOperator;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaType;
import dev.da0hn.email.management.system.core.ports.api.AccountService;
import dev.da0hn.email.management.system.core.ports.api.dto.MoveRuleInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewAccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleCriteriaInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleInput;
import dev.da0hn.email.management.system.core.ports.api.dto.NewRuleOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.RuleCriteriaOutput;
import dev.da0hn.email.management.system.infrastructure.web.error.GlobalExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
  @DisplayName("Deve criar uma nova conta com sucesso")
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

      final var accountId = UUID.randomUUID();
      final var now = LocalDateTime.now();
      final var expectedOutput = new NewAccountOutput(
          accountId,
          "John Doe",
          "john@example.com",
          now,
          now,
          0
      );

      when(accountService.createAccount(any(NewAccountInput.class))).thenReturn(expectedOutput);

      mockMvc.perform(
          post("/api/v1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonContent)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(accountId.toString()))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"))
        .andExpect(jsonPath("$.totalRules").value(0));

      verify(accountService).createAccount(any(NewAccountInput.class));
  }

  @Test
  @DisplayName("Deve retornar erro quando o email é inválido")
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
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Validation Error"))
        .andExpect(jsonPath("$.message").value("Invalid request parameters"))
        .andExpect(jsonPath("$.errors", hasSize(1)))
        .andExpect(jsonPath("$.errors[0].message").value("Invalid email format"));
  }

  @Test
  @DisplayName("Deve retornar erro quando a senha é muito curta")
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
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Validation Error"))
        .andExpect(jsonPath("$.message").value("Invalid request parameters"))
        .andExpect(jsonPath("$.errors", hasSize(1)))
        .andExpect(jsonPath("$.errors[0].message").value("Password must be at least 8 characters long"));
  }

  @Test
  @DisplayName("Deve retornar erro quando a porta é inválida")
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
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Validation Error"))
        .andExpect(jsonPath("$.message").value("Invalid request parameters"))
        .andExpect(jsonPath("$.errors", hasSize(1)))
        .andExpect(jsonPath("$.errors[0].message").value("Port must be between 1 and 65535"));
  }

  @Test
  @DisplayName("Deve retornar erro quando o protocolo é inválido")
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
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Validation Error"))
        .andExpect(jsonPath("$.message").value("Invalid request parameters"))
        .andExpect(jsonPath("$.errors", hasSize(1)))
        .andExpect(jsonPath("$.errors[0].message").value("Protocol must be one of: smtp, imap, pop3"));
  }

  @Test
  @DisplayName("Deve criar uma regra de movimentação com sucesso")
  void shouldCreateMoveRuleSuccessfully() throws Exception {
      final var accountId = UUID.randomUUID();
      final var input = new NewRuleInput(
          accountId,
          "Move to Archive",
          "Move emails to archive folder",
          RuleAction.MOVE,
          Set.of(new NewRuleCriteriaInput(
              "test@email.com",
              RuleCriteriaType.FROM,
              RuleCriteriaOperator.EQUALS
          )),
          new MoveRuleInput("INBOX", "ARCHIVE")
      );

      final var output = new NewRuleOutput(
          UUID.randomUUID(),
          input.name(),
          input.description(),
          input.action(),
          input.criteria().stream()
              .map(criteria -> new RuleCriteriaOutput(
                  UUID.randomUUID(),
                  criteria.value(),
                  criteria.type(),
                  criteria.operator()
              ))
              .collect(Collectors.toSet())
      );

      when(accountService.createRule(any())).thenReturn(output);

      mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(input)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").exists())
          .andExpect(jsonPath("$.name").value(input.name()))
          .andExpect(jsonPath("$.description").value(input.description()))
          .andExpect(jsonPath("$.action").value(input.action().name()))
          .andExpect(jsonPath("$.criteria", hasSize(1)));

      verify(accountService).createRule(input);
  }

  @Test
  @DisplayName("Deve criar uma regra de exclusão com sucesso")
  void shouldCreateDeleteRuleSuccessfully() throws Exception {
      final var accountId = UUID.randomUUID();
      final var input = new NewRuleInput(
          accountId,
          "Delete Old Emails",
          "Delete emails older than 30 days",
          RuleAction.DELETE,
          Set.of(new NewRuleCriteriaInput(
              "2024-01-01T00:00:00",
              RuleCriteriaType.RECEIVED_AT,
              RuleCriteriaOperator.GREATER_THAN
          )),
          null
      );

      final var output = new NewRuleOutput(
          UUID.randomUUID(),
          input.name(),
          input.description(),
          input.action(),
          input.criteria().stream()
              .map(criteria -> new RuleCriteriaOutput(
                  UUID.randomUUID(),
                  criteria.value(),
                  criteria.type(),
                  criteria.operator()
              ))
              .collect(Collectors.toSet())
      );

      when(accountService.createRule(any())).thenReturn(output);

      mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(input)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").exists())
          .andExpect(jsonPath("$.name").value(input.name()))
          .andExpect(jsonPath("$.description").value(input.description()))
          .andExpect(jsonPath("$.action").value(input.action().name()))
          .andExpect(jsonPath("$.criteria", hasSize(1)));

      verify(accountService).createRule(input);
  }

  @Test
  @DisplayName("Deve criar uma regra de arquivamento com sucesso")
  void shouldCreateArchiveRuleSuccessfully() throws Exception {
      final var accountId = UUID.randomUUID();
      final var input = new NewRuleInput(
          accountId,
          "Archive Old Emails",
          "Archive emails older than 30 days",
          RuleAction.ARCHIVE,
          Set.of(new NewRuleCriteriaInput(
              "2024-01-01T00:00:00",
              RuleCriteriaType.RECEIVED_AT,
              RuleCriteriaOperator.GREATER_THAN
          )),
          null
      );

      final var output = new NewRuleOutput(
          UUID.randomUUID(),
          input.name(),
          input.description(),
          input.action(),
          input.criteria().stream()
              .map(criteria -> new RuleCriteriaOutput(
                  UUID.randomUUID(),
                  criteria.value(),
                  criteria.type(),
                  criteria.operator()
              ))
              .collect(Collectors.toSet())
      );

      when(accountService.createRule(any())).thenReturn(output);

      mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(input)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").exists())
          .andExpect(jsonPath("$.name").value(input.name()))
          .andExpect(jsonPath("$.description").value(input.description()))
          .andExpect(jsonPath("$.action").value(input.action().name()))
          .andExpect(jsonPath("$.criteria", hasSize(1)));

      verify(accountService).createRule(input);
  }

  @Test
  @DisplayName("Deve retornar erro quando a conta não é encontrada")
  void shouldReturnErrorWhenAccountNotFound() throws Exception {
      final var accountId = UUID.randomUUID();
      final var input = new NewRuleInput(
          accountId,
          "Archive Old Emails",
          "Archive emails older than 30 days",
          RuleAction.ARCHIVE,
          Set.of(new NewRuleCriteriaInput(
              "2024-01-01T00:00:00",
              RuleCriteriaType.RECEIVED_AT,
              RuleCriteriaOperator.GREATER_THAN
          )),
          null
      );

      when(accountService.createRule(any())).thenThrow(new EntityNotFoundException("Account not found"));

      mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(input)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.error").value("Not Found"))
          .andExpect(jsonPath("$.message").value("Resource not found"))
          .andExpect(jsonPath("$.errors", hasSize(1)))
          .andExpect(jsonPath("$.errors[0].message").value("Account not found"));
  }

  @Test
  @DisplayName("Deve retornar erro quando campos obrigatórios da regra estão faltando")
  void shouldReturnErrorWhenRuleRequiredFieldsAreMissing() throws Exception {
      final var accountId = UUID.randomUUID();
      final var jsonContent = """
          {
            "accountId": "%s"
          }""".formatted(accountId);

      mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(jsonContent))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.error").value("Validation Error"))
          .andExpect(jsonPath("$.message").value("Invalid request parameters"))
          .andExpect(jsonPath("$.errors", hasSize(4)))
          .andExpect(jsonPath("$.errors[*].message").value(containsInAnyOrder(
              "Rule name is required",
              "Rule description is required",
              "Rule action is required",
              "At least one criteria is required"
          )));
  }

  @Test
  @DisplayName("Deve retornar erro quando o critério é inválido")
  void shouldReturnErrorWhenCriteriaIsInvalid() throws Exception {
      final var accountId = UUID.randomUUID();
      final var input = new NewRuleInput(
          accountId,
          "Move to Archive",
          "Move emails to archive folder",
          RuleAction.MOVE,
          Set.of(new NewRuleCriteriaInput(
              "",  // empty value
              RuleCriteriaType.FROM,
              RuleCriteriaOperator.EQUALS
          )),
          new MoveRuleInput("INBOX", "ARCHIVE")
      );

      mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(input)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.error").value("Validation Error"))
          .andExpect(jsonPath("$.message").value("Invalid request parameters"))
          .andExpect(jsonPath("$.errors", hasSize(1)))
          .andExpect(jsonPath("$.errors[0].message").value("Criteria value is required"));
  }

  @Test
  @DisplayName("Deve retornar erro quando a configuração da regra de movimentação está faltando")
  void shouldReturnErrorWhenMoveRuleConfigurationIsMissing() throws Exception {
      final var accountId = UUID.randomUUID();
      final var input = new NewRuleInput(
          accountId,
          "Move to Archive",
          "Move emails to archive folder",
          RuleAction.MOVE,
          Set.of(new NewRuleCriteriaInput(
              "test@email.com",
              RuleCriteriaType.FROM,
              RuleCriteriaOperator.EQUALS
          )),
          null
      );

      mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(input)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.error").value("Validation Error"))
          .andExpect(jsonPath("$.message").value("Invalid request parameters"))
          .andExpect(jsonPath("$.errors", hasSize(1)))
          .andExpect(jsonPath("$.errors[0].message").value("Move rule configuration is required for MOVE action"));
  }



}
