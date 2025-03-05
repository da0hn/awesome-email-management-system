# Sistema de Gerenciamento de E-mails

## Visão Geral do Projeto
Esta API foi projetada para gerenciar e-mails através de um sistema sofisticado baseado em regras. Os usuários podem criar regras personalizadas que são executadas de forma assíncrona usando um agendador dinâmico. O sistema ajuda a organizar, filtrar e processar e-mails automaticamente com base em critérios definidos pelo usuário, como remetente, assunto, conteúdo ou tags personalizadas.

## Funcionalidades Principais
* **Gerenciamento de Contas**: A API inclui um sistema de registro de contas onde os usuários podem fornecer seu e-mail e senha. Todas as senhas são armazenadas de forma segura e criptografada no banco de dados.
* **Agendamento Dinâmico de Regras**: As regras agendadas são executadas de acordo com períodos específicos para otimizar a eficiência do processamento e evitar operações desnecessárias.
* **Processamento Assíncrono**: Todas as operações de gerenciamento de e-mail são tratadas de forma assíncrona através de um sistema de agendamento dinâmico.

## Arquitetura Técnica
* API RESTful Backend
* Criptografia segura de senhas
* Sistema de agendamento dinâmico
* Capacidades de processamento assíncrono

## Primeiros Passos

### Pré-requisitos
* Java 17 ou superior
* Maven 3.8+
* Banco de dados PostgreSQL 13+
* Mínimo de 2GB de RAM
* Acesso a servidor SMTP para processamento de e-mail

### Instalação
1. Clone o repositório:
   ```bash
   git clone https://github.com/yourusername/awesome-email-management-system.git
   ```
2. Navegue até o diretório do projeto:
   ```bash
   cd awesome-email-management-system
   ```
3. Instale as dependências:
   ```bash
   mvn install
   ```
4. Configure as configurações do banco de dados no arquivo `application.properties`
5. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```

## Como Usar

### Criando Regras de E-mail
1. Acesse o endpoint da API `/api/v1/rules`
2. Defina os critérios da regra (ex: e-mail do remetente, padrões de assunto, palavras-chave no conteúdo)
3. Configure as ações da regra (ex: mover para pasta, encaminhar, excluir)
4. Especifique os parâmetros de agendamento (frequência, janelas de tempo)

### Gerenciando Contas de E-mail
1. Registre sua conta de e-mail através de `/api/v1/accounts`
2. Verifique seu endereço de e-mail
3. Configure as configurações SMTP
4. Comece a criar e gerenciar regras

A documentação detalhada da API e mais exemplos de uso serão adicionados conforme o projeto se desenvolve.

## Considerações de Segurança
* Todas as credenciais de usuário são criptografadas
* Sistema de autenticação seguro
* Endpoints de API protegidos
* Atualizações e patches de segurança regulares

## Desenvolvimento Futuro
A implementação inicial concentra-se na funcionalidade da API. Uma interface frontend será desenvolvida no futuro para melhorar a experiência do usuário e a acessibilidade.

## Contribuindo
Contribuições são bem-vindas! Sinta-se à vontade para enviar um Pull Request.

