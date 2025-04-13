package dev.da0hn.email.management.system.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.da0hn.email.management.system.core.domain.RuleAction;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaOperator;
import dev.da0hn.email.management.system.core.domain.RuleCriteriaType;
import dev.da0hn.email.management.system.core.ports.api.AccountService;
import dev.da0hn.email.management.system.core.ports.api.dto.AccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.DetailedAccountOutput;
import dev.da0hn.email.management.system.core.ports.api.dto.DetailedRuleOutput;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest {

    private static final int BAD_REQUEST_STATUS = 400;

    private static final int NOT_FOUND_STATUS = 404;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        this.accountService = mock(AccountService.class);
        this.objectMapper = new ObjectMapper();
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(new AccountController(this.accountService))
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

        when(this.accountService.createAccount(any(NewAccountInput.class))).thenReturn(expectedOutput);

        this.mockMvc.perform(
                post("/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonContent)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(accountId.toString()))
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john@example.com"))
            .andExpect(jsonPath("$.totalRules").value(0));

        verify(this.accountService).createAccount(any(NewAccountInput.class));
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

        this.mockMvc.perform(
                post("/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonContent)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(BAD_REQUEST_STATUS))
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

        this.mockMvc.perform(
                post("/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonContent)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(BAD_REQUEST_STATUS))
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

        this.mockMvc.perform(
                post("/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonContent)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(BAD_REQUEST_STATUS))
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

        this.mockMvc.perform(
                post("/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonContent)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(BAD_REQUEST_STATUS))
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

        when(this.accountService.createRule(any())).thenReturn(output);

        this.mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(this.objectMapper.writeValueAsString(input)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value(input.name()))
            .andExpect(jsonPath("$.description").value(input.description()))
            .andExpect(jsonPath("$.action").value(input.action().name()))
            .andExpect(jsonPath("$.criteria", hasSize(1)));

        verify(this.accountService).createRule(input);
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

        when(this.accountService.createRule(any())).thenReturn(output);

        this.mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(this.objectMapper.writeValueAsString(input)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value(input.name()))
            .andExpect(jsonPath("$.description").value(input.description()))
            .andExpect(jsonPath("$.action").value(input.action().name()))
            .andExpect(jsonPath("$.criteria", hasSize(1)));

        verify(this.accountService).createRule(input);
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

        when(this.accountService.createRule(any())).thenReturn(output);

        this.mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(this.objectMapper.writeValueAsString(input)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value(input.name()))
            .andExpect(jsonPath("$.description").value(input.description()))
            .andExpect(jsonPath("$.action").value(input.action().name()))
            .andExpect(jsonPath("$.criteria", hasSize(1)));

        verify(this.accountService).createRule(input);
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

        when(this.accountService.createRule(any())).thenThrow(new EntityNotFoundException("Account not found"));

        this.mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(this.objectMapper.writeValueAsString(input)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(NOT_FOUND_STATUS))
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

        this.mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(jsonContent))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(BAD_REQUEST_STATUS))
            .andExpect(jsonPath("$.error").value("Validation Error"))
            .andExpect(jsonPath("$.message").value("Invalid request parameters"))
            .andExpect(jsonPath("$.errors", hasSize(4)))
            .andExpect(jsonPath("$.errors[*].message").value(containsInAnyOrder(
                "Nome da regra é obrigatório",
                "Descrição da regra é obrigatória",
                "Ação da regra é obrigatória",
                "Pelo menos um critério é obrigatório"
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
                "",
                RuleCriteriaType.FROM,
                RuleCriteriaOperator.EQUALS
            )),
            new MoveRuleInput("INBOX", "ARCHIVE")
        );

        this.mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(this.objectMapper.writeValueAsString(input)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(BAD_REQUEST_STATUS))
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

        this.mockMvc.perform(post("/api/v1/accounts/{accountId}/rules", accountId)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(this.objectMapper.writeValueAsString(input)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(BAD_REQUEST_STATUS))
            .andExpect(jsonPath("$.error").value("Validation Error"))
            .andExpect(jsonPath("$.message").value("Invalid request parameters"))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message").value("Configuração de movimentação é obrigatória para ação MOVE"));
    }

    @Test
    @DisplayName("Deve retornar lista de todas as contas")
    void shouldReturnListOfAllAccounts() throws Exception {
        final var now = LocalDateTime.now();
        final var account1 = new AccountOutput(
            UUID.randomUUID(),
            "Account 1",
            "account1@test.com",
            "imap.test.com",
            993,
            "imap",
            now,
            now,
            0
        );
        final var account2 = new AccountOutput(
            UUID.randomUUID(),
            "Account 2",
            "account2@test.com",
            "imap.test.com",
            993,
            "imap",
            now,
            now,
            2
        );
        when(this.accountService.findAll()).thenReturn(List.of(account1, account2));

        this.mockMvc.perform(get("/api/v1/accounts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(account1.id().toString()))
            .andExpect(jsonPath("$[0].name").value(account1.name()))
            .andExpect(jsonPath("$[0].email").value(account1.email()))
            .andExpect(jsonPath("$[0].host").value(account1.host()))
            .andExpect(jsonPath("$[0].port").value(account1.port()))
            .andExpect(jsonPath("$[0].protocol").value(account1.protocol()))
            .andExpect(jsonPath("$[0].totalRules").value(account1.totalRules()))
            .andExpect(jsonPath("$[1].id").value(account2.id().toString()))
            .andExpect(jsonPath("$[1].name").value(account2.name()))
            .andExpect(jsonPath("$[1].email").value(account2.email()))
            .andExpect(jsonPath("$[1].host").value(account2.host()))
            .andExpect(jsonPath("$[1].port").value(account2.port()))
            .andExpect(jsonPath("$[1].protocol").value(account2.protocol()))
            .andExpect(jsonPath("$[1].totalRules").value(account2.totalRules()));
    }

    @Test
    @DisplayName("Deve retornar conta quando encontrada pelo id")
    void shouldReturnAccountWhenFoundById() throws Exception {
        final var id = UUID.randomUUID();
        final var now = LocalDateTime.now();
        final var rule = new DetailedRuleOutput(
            UUID.randomUUID(),
            "Test Rule",
            "Test Description",
            RuleAction.MOVE,
            Set.of(new RuleCriteriaOutput(
                UUID.randomUUID(),
                "test@example.com",
                RuleCriteriaType.FROM,
                RuleCriteriaOperator.EQUALS
            )),
            "INBOX",
            "Archive"
        );
        final var account = new DetailedAccountOutput(
            id,
            "Test Account",
            "test@test.com",
            "imap.test.com",
            993,
            "imap",
            now,
            now,
            Set.of(rule)
        );
        when(this.accountService.findById(id)).thenReturn(account);

        this.mockMvc.perform(get("/api/v1/accounts/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(account.id().toString()))
            .andExpect(jsonPath("$.name").value(account.name()))
            .andExpect(jsonPath("$.email").value(account.email()))
            .andExpect(jsonPath("$.host").value(account.host()))
            .andExpect(jsonPath("$.port").value(account.port()))
            .andExpect(jsonPath("$.protocol").value(account.protocol()))
            .andExpect(jsonPath("$.rules").isArray())
            .andExpect(jsonPath("$.rules", hasSize(1)))
            .andExpect(jsonPath("$.rules[0].id").value(rule.id().toString()))
            .andExpect(jsonPath("$.rules[0].name").value(rule.name()))
            .andExpect(jsonPath("$.rules[0].description").value(rule.description()))
            .andExpect(jsonPath("$.rules[0].action").value(rule.action().toString()))
            .andExpect(jsonPath("$.rules[0].sourceFolder").value(rule.sourceFolder()))
            .andExpect(jsonPath("$.rules[0].targetFolder").value(rule.targetFolder()))
            .andExpect(jsonPath("$.rules[0].criteria").isArray())
            .andExpect(jsonPath("$.rules[0].criteria", hasSize(1)))
            .andExpect(jsonPath("$.rules[0].criteria[0].value").value("test@example.com"))
            .andExpect(jsonPath("$.rules[0].criteria[0].type").value("FROM"))
            .andExpect(jsonPath("$.rules[0].criteria[0].operator").value("EQUALS"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando conta não for encontrada pelo id")
    void shouldReturn404WhenAccountNotFoundById() throws Exception {
        final var id = UUID.randomUUID();
        when(this.accountService.findById(id)).thenThrow(new EntityNotFoundException("Account not found"));

        this.mockMvc.perform(get("/api/v1/accounts/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(NOT_FOUND_STATUS))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Resource not found"))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message").value("Account not found"));
    }

    @Test
    @DisplayName("Deve remover regra de uma conta com sucesso")
    void shouldDeleteRuleSuccessfully() throws Exception {
        final var accountId = UUID.randomUUID();
        final var ruleId = UUID.randomUUID();

        this.mockMvc.perform(delete("/api/v1/accounts/{accountId}/rules/{ruleId}", accountId, ruleId))
            .andExpect(status().isNoContent());

        verify(this.accountService).deleteRule(accountId, ruleId);
    }

    @Test
    @DisplayName("Deve retornar 404 quando conta não for encontrada ao remover regra")
    void shouldReturn404WhenAccountNotFoundOnDeleteRule() throws Exception {
        final var accountId = UUID.randomUUID();
        final var ruleId = UUID.randomUUID();

        org.mockito.Mockito.doThrow(new EntityNotFoundException("Account not found"))
            .when(this.accountService).deleteRule(accountId, ruleId);

        this.mockMvc.perform(delete("/api/v1/accounts/{accountId}/rules/{ruleId}", accountId, ruleId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(NOT_FOUND_STATUS))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Resource not found"))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message").value("Account not found"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando regra não for encontrada ao remover regra")
    void shouldReturn404WhenRuleNotFoundOnDeleteRule() throws Exception {
        final var accountId = UUID.randomUUID();
        final var ruleId = UUID.randomUUID();

        org.mockito.Mockito.doThrow(new EntityNotFoundException("Rule not found"))
            .when(this.accountService).deleteRule(accountId, ruleId);

        this.mockMvc.perform(delete("/api/v1/accounts/{accountId}/rules/{ruleId}", accountId, ruleId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(NOT_FOUND_STATUS))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Resource not found"))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message").value("Rule not found"));
    }

}
