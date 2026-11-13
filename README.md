<div align="center">
 

<div align="center">

**Felipe Piva da Silva – 25054343-2**

**Nicollas Azalim – 25013758-2**

**Nivea Sofia da Cas – 25004647-2**

**Roberto Müller Sanches – 25224962-2**

**Yasmin Fernanda de Carvalho – 25061121-2**

</div>

<br><br><br><br>

# SISTEMA DE DOAÇÕES DE ALIMENTOS

### Gestão integrada de doações e lotes com persistência em banco de dados

<br><br><br><br>

**[Londrina]**
**2026**

</div>

---

<div align="center">

**FELIPE [Silva]**

<br><br><br>

# SISTEMA DE DOAÇÕES DE ALIMENTOS

### Gestão integrada de doações e lotes com persistência em banco de dados

<br>

</div>

> Trabalho apresentado à disciplina de [NOME DA DISCIPLINA], do curso de [NOME DO CURSO], da [NOME DA INSTITUIÇÃO], como requisito parcial de avaliação.
>
> Orientador(a): Prof(a). [NOME DO ORIENTADOR]

<div align="center">

<br>

**[Londrina]**
**2026**

</div>

---

## RESUMO

Este trabalho apresenta o desenvolvimento de um sistema desktop para o gerenciamento de doações de alimentos em um banco de alimentos. A aplicação, escrita em linguagem Java com interface gráfica Swing, permite cadastrar, consultar, atualizar e excluir doações, gerando automaticamente um lote de estoque para cada doação registrada. O sistema também controla a retirada de itens dos lotes, aplicando regras de negócio como a proibição de distribuir produtos vencidos e a verificação de estoque suficiente, além de sinalizar visualmente lotes vencidos ou próximos do vencimento. Os dados são gravados em banco relacional MariaDB por meio de JDBC, com operações realizadas dentro de transações. A organização do código segue uma arquitetura em camadas (interface, serviço, repositório e modelo), o que facilita a manutenção e a evolução do sistema.

**Palavras-chave:** Banco de alimentos. Doações. Controle de estoque. Java. JDBC. MariaDB.

---

## ABSTRACT

This paper presents the development of a desktop system for managing food donations in a food bank. The application, written in Java with a Swing graphical interface, allows users to create, query, update and delete donations, automatically generating a stock batch for each registered donation. The system also controls the withdrawal of items from batches, enforcing business rules such as forbidding the distribution of expired products and checking for sufficient stock, and visually flags expired batches or batches close to their expiration date. Data is stored in a MariaDB relational database through JDBC, with operations executed inside transactions. The code follows a layered architecture (interface, service, repository and model), which makes maintenance and future evolution easier.

**Keywords:** Food bank. Donations. Stock control. Java. JDBC. MariaDB.

---

## SUMÁRIO

1. [INTRODUÇÃO](#1-introdução)
   - 1.1 [Contextualização](#11-contextualização)
   - 1.2 [Objetivos](#12-objetivos)
   - 1.3 [Justificativa](#13-justificativa)
2. [FUNDAMENTAÇÃO TECNOLÓGICA](#2-fundamentação-tecnológica)
3. [DESENVOLVIMENTO](#3-desenvolvimento)
   - 3.1 [Requisitos do sistema](#31-requisitos-do-sistema)
   - 3.2 [Arquitetura e estrutura do projeto](#32-arquitetura-e-estrutura-do-projeto)
   - 3.3 [Modelo de domínio](#33-modelo-de-domínio)
   - 3.4 [Banco de dados](#34-banco-de-dados)
   - 3.5 [Regras de negócio](#35-regras-de-negócio)
   - 3.6 [Fluxo de cadastro de uma doação](#36-fluxo-de-cadastro-de-uma-doação)
   - 3.7 [Interface gráfica](#37-interface-gráfica)
4. [INSTALAÇÃO E EXECUÇÃO](#4-instalação-e-execução)
5. [CONSIDERAÇÕES FINAIS](#5-considerações-finais)
6. [REFERÊNCIAS](#referências)

---

## 1 INTRODUÇÃO

### 1.1 Contextualização

Bancos de alimentos recebem doações de diferentes origens, como supermercados, produtores e pessoas físicas, e precisam redistribuí-las a instituições e famílias atendidas. Como grande parte dos alimentos é perecível, o controle da validade e da quantidade disponível em estoque é essencial para evitar desperdícios e garantir que apenas produtos próprios para consumo sejam distribuídos.

### 1.2 Objetivos

**Objetivo geral**

Desenvolver um sistema desktop que integre o registro de doações ao controle de lotes de estoque de um banco de alimentos, com armazenamento persistente em banco de dados.

**Objetivos específicos**

- Cadastrar, listar, atualizar e excluir doações por meio de uma interface gráfica;
- Gerar automaticamente um lote de estoque a cada doação cadastrada;
- Permitir a retirada (distribuição) de itens de um lote, respeitando regras de validade e de quantidade;
- Sinalizar lotes vencidos e lotes que vencem em até sete dias;
- Persistir os dados em um banco relacional MariaDB, com uso de transações;
- Organizar o código em camadas para facilitar a manutenção.

### 1.3 Justificativa

O controle manual de doações e de validade é sujeito a erros e dificulta a visão do que está disponível. Um sistema que associe cada doação a um lote e que aplique as regras de negócio de forma automática reduz falhas operacionais, melhora a rastreabilidade dos alimentos e apoia a tomada de decisão sobre o que deve ser distribuído primeiro.

---

## 2 FUNDAMENTAÇÃO TECNOLÓGICA

| Tecnologia | Versão | Finalidade no projeto |
|---|---|---|
| Java | 17 | Linguagem de programação principal |
| Swing | (JDK) | Construção da interface gráfica desktop |
| JDBC | (JDK) | API de acesso ao banco de dados |
| MariaDB | 10.4 (XAMPP) | Sistema gerenciador de banco de dados relacional |
| `mariadb-java-client` | 3.4.1 | Driver JDBC para o MariaDB |
| Apache Maven | — | Gerenciamento de dependências e build |

O projeto utiliza codificação UTF-8 e identificador Maven `br.com.sistemadoacoes:sistema-doacoes:1.0-SNAPSHOT`.

---

## 3 DESENVOLVIMENTO

### 3.1 Requisitos do sistema

**Requisitos funcionais**

| Código | Descrição |
|---|---|
| RF01 | O sistema deve permitir cadastrar uma doação informando ID, doador, categoria, validade, quantidade e unidade de medida. |
| RF02 | O sistema deve gerar automaticamente um lote (`LOTE-<id>`) ao cadastrar uma doação. |
| RF03 | O sistema deve listar as doações cadastradas. |
| RF04 | O sistema deve permitir atualizar e excluir uma doação. |
| RF05 | O sistema deve listar os lotes com quantidade disponível, unidade, validade e status. |
| RF06 | O sistema deve permitir retirar uma quantidade de itens de um lote. |
| RF07 | O sistema deve indicar se o lote está válido, vence em breve ou está vencido. |

**Requisitos não funcionais**

| Código | Descrição |
|---|---|
| RNF01 | Os dados devem ser persistidos em banco MariaDB/MySQL. |
| RNF02 | Cada operação de escrita deve ocorrer em transação, com *rollback* em caso de falha. |
| RNF03 | A conexão com o banco deve ser configurável sem alterar o código. |
| RNF04 | O código deve seguir separação em camadas. |

### 3.2 Arquitetura e estrutura do projeto

O sistema adota uma arquitetura em camadas, em que cada camada tem uma responsabilidade bem definida e conhece apenas a camada imediatamente abaixo.

```
Interface (ui)  →  Serviço (service)  →  Repositório (repository)  →  Banco de dados
                          ↓
                    Modelo (model)
```

Estrutura de diretórios:

```
sistema-doacoes/
├── pom.xml
├── README.md
└── src/main/java/
    ├── App.java                         # Ponto de entrada da aplicação
    ├── db/
    │   └── Conexao.java                 # Fábrica de conexões JDBC
    ├── exception/
    │   ├── RegraNegocioException.java
    │   └── RegistroNaoEncontradoException.java
    ├── model/                           # Entidades e regras do domínio
    │   ├── Registro.java                # Classe abstrata base (ID)
    │   ├── Validavel.java               # Interface de validação
    │   ├── Doacao.java
    │   ├── Lote.java
    │   ├── CategoriaAlimento.java
    │   ├── Beneficiario.java
    │   ├── PedidoDistribuicao.java
    │   ├── Agendamento.java
    │   ├── Transporte.java
    │   └── (enums de status e tipo)
    ├── repository/
    │   ├── CrudRepository.java          # Contrato genérico de CRUD
    │   ├── DoacaoRepositoryJdbc.java    # Implementação com MariaDB
    │   └── DoacaoRepositoryMemoria.java # Implementação em memória
    ├── service/
    │   └── DoacaoService.java           # Orquestra regras e persistência
    └── ui/
        └── DoacaoFrame.java             # Janela principal (Swing)
```

| Camada | Classe principal | Responsabilidade |
|---|---|---|
| Interface | `DoacaoFrame` | Exibir formulários e tabelas, ler dados digitados e mostrar mensagens. |
| Serviço | `DoacaoService` | Intermediar a interface e o repositório, acionando as validações. |
| Repositório | `DoacaoRepositoryJdbc` | Executar as instruções SQL e controlar as transações. |
| Modelo | `Doacao`, `Lote` | Representar as entidades e aplicar as regras de negócio. |
| Infraestrutura | `Conexao` | Fornecer conexões com o banco de dados. |

**Conceitos de orientação a objetos aplicados:** herança (`Doacao` e `Lote` estendem `Registro`), interface (`Validavel`, `CrudRepository`), encapsulamento, polimorfismo (duas implementações de `CrudRepository`) e tratamento de exceções próprias.

### 3.3 Modelo de domínio

**Doacao** representa uma doação recebida. Atributos principais: identificador, doador, categoria, data de validade, quantidade, unidade de medida e o lote associado.

**Lote** representa uma quantidade de itens em estoque, originada de uma doação. Atributos: identificador, quantidade, unidade de medida e data de validade.

**CategoriaAlimento** é uma enumeração com os valores `GRAOS`, `PERECIVEIS`, `ENLATADOS`, `BEBIDAS`, `HORTIFRUTI` e `OUTROS`.

O pacote `model` contém ainda as classes `Beneficiario`, `PedidoDistribuicao`, `Agendamento` e `Transporte`, com seus respectivos tipos e status. Elas já implementam validações e regras, como a finalização de pedidos com baixa automática de estoque, mas **ainda não estão integradas à interface nem ao banco de dados**, constituindo a base para evoluções futuras (seção 5).

### 3.4 Banco de dados

O sistema utiliza o banco `banco_de_alimentos_ldm`. O mapeamento entre os objetos Java e as tabelas é o seguinte:

| Objeto Java | Tabela / coluna |
|---|---|
| `Doacao.id` | `doacao.id_Doacao` |
| `Doacao.doador` | `doador.Nome` (reaproveitado se já existir, senão criado) |
| `Doacao.categoria` | `doacao.tipo_Entrada` e `produto.Categoria` |
| `Doacao.unidade` | `produto.Unidade_Medida` (produto reaproveitado por categoria + unidade) |
| `Lote.id` | `lote.Numero_Lote` (ex.: `LOTE-10`) |
| `Lote.quantidade` | `lote.Quantidade` |
| `Lote.dataValidade` | `lote.Data_validade` |

**Tabelas envolvidas:** `doacao`, `doador`, `produto` e `lote`. Ao excluir uma doação, os lotes relacionados são removidos em cascata pelo próprio banco.

**Observações de implementação**

- As chaves primárias das tabelas não são `AUTO_INCREMENT`; por isso, os identificadores de doador, produto e lote são calculados com `MAX(coluna) + 1` pelo método `proximoId()`. O ID da doação é informado pelo usuário e deve ser numérico.
- Todas as operações usam `PreparedStatement`, o que protege contra injeção de SQL.
- O método `executarEmTransacao()` desativa o *auto-commit*, confirma (`commit`) em caso de sucesso e desfaz (`rollback`) em caso de erro.

### 3.5 Regras de negócio

| Regra | Onde é aplicada |
|---|---|
| O doador é obrigatório. | `Doacao.validar()` |
| A data de validade não pode estar vencida no cadastro. | `Doacao.validar()` |
| A quantidade da doação deve ser maior que zero. | `Doacao.validar()` |
| Não é permitido cadastrar duas doações com o mesmo ID. | `DoacaoRepositoryJdbc.salvar()` |
| A quantidade, a unidade e a validade do lote são obrigatórias, e a quantidade não pode ser negativa. | `Lote.validar()` |
| Lote vencido não pode ser distribuído. | `Lote.retirar()` |
| A quantidade retirada deve ser positiva e não pode exceder o estoque. | `Lote.retirar()` |
| Lote que vence em até 7 dias recebe o alerta "Vence em breve". | `Lote.gerarAlertaVencimento()` |

### 3.6 Fluxo de cadastro de uma doação

1. O usuário preenche o formulário e clica em **Cadastrar Doação**.
2. `DoacaoFrame.cadastrarDoacao()` lê os campos, converte a validade para data e a quantidade para número e cria o objeto `Doacao`.
3. `DoacaoService.cadastrarDoacao()` chama `Doacao.cadastrar()`, que valida os dados e cria o lote `LOTE-<id>`.
4. Se a validação for bem-sucedida, `DoacaoRepositoryJdbc.salvar()` abre uma transação, verifica se o ID já existe, obtém ou cria o doador, insere a doação, obtém ou cria o produto e insere o lote.
5. A interface atualiza as tabelas, limpa o formulário e exibe a mensagem de sucesso.
6. Em caso de erro, uma janela de aviso mostra a mensagem correspondente e nada é gravado.

O fluxo de retirada de itens é semelhante: `DoacaoFrame.efetuarBaixaLote()` chama `DoacaoService.retirarDoLote()`, que busca o lote, executa `Lote.retirar()` e grava a nova quantidade com `atualizarQuantidadeLote()`.

### 3.7 Interface gráfica

A janela principal, "Banco de Alimentos - Gestão de Doações e Lotes", possui duas abas:

**Aba "Cadastro de Doações"**
- Formulário com os campos ID da Doação, Doador, Categoria, Validade (AAAA-MM-DD), Quantidade e Unidade de Medida.
- Botões **Cadastrar Doação**, **Atualizar**, **Excluir** e **Limpar**.
- Tabela com as colunas ID Doação, Doador, Categoria, Validade, Qtd e Unidade.

**Aba "Produtos e Lotes Gerados"**
- Painel "Retirada / Distribuição de Itens do Lote", com os campos ID do Lote e Qtd a Retirar e o botão **Confirmar Retirada**. Ao selecionar uma linha da tabela, o ID do lote é preenchido automaticamente.
- Tabela com as colunas ID Lote, Qtd Disponível, Unidade Medida, Validade e Status/Alerta, cujos valores possíveis são *OK (Válido)*, *Vence em breve* e *VENCIDO*.

> Sugestão: inserir aqui as capturas de tela das duas abas, com legenda e fonte (ex.: "Figura 1 – Aba de cadastro de doações. Fonte: elaborado pelo autor, 2026.").

---

## 4 INSTALAÇÃO E EXECUÇÃO

**Pré-requisitos**

- JDK 17 ou superior;
- Apache Maven;
- MariaDB/MySQL (por exemplo, via XAMPP) em execução.

**Passo a passo**

1. Importe o arquivo `banco_de_alimentos_ldm.sql` no MariaDB (pelo phpMyAdmin ou pela linha de comando), sem alterações.
2. Clone o repositório e abra o projeto como projeto Maven.
3. Compile e execute a classe `App`:

```bash
mvn compile
mvn exec:java -Dexec.mainClass=App
```

**Configuração da conexão**

Por padrão, o sistema usa o endereço `jdbc:mariadb://127.0.0.1:3306/banco_de_alimentos_ldm` e o usuário `root`. Os valores podem ser alterados, sem modificar o código, por propriedades da JVM ou por variáveis de ambiente:

| Propriedade da JVM | Variável de ambiente | Conteúdo |
|---|---|---|
| `-Ddb.url` | `DB_URL` | URL JDBC do banco |
| `-Ddb.user` | `DB_USER` | Usuário do banco |
| `-Ddb.password` | `DB_PASSWORD` | Senha do banco |

Exemplo:

```bash
java -Ddb.user=root -Ddb.password=SUA_SENHA -cp target/classes App
```

> **Atenção:** a senha do banco não deve ser gravada no código-fonte nem publicada no repositório. Informe-a sempre por propriedade ou variável de ambiente.

---

## 5 CONSIDERAÇÕES FINAIS

O sistema desenvolvido atende ao objetivo de integrar o registro de doações ao controle de lotes, aplicando regras de validade e de estoque e persistindo os dados em banco relacional com controle transacional. A separação em camadas e o uso de interfaces tornaram o código organizado e permitiram, inclusive, manter uma implementação alternativa do repositório em memória.

**Limitações identificadas**

- O cálculo de identificadores por `MAX + 1` pode gerar conflito em cenários de acesso simultâneo; o uso de `AUTO_INCREMENT` seria mais seguro.
- Na tabela de doações, a quantidade exibida é a soma dos lotes atuais, e não a quantidade originalmente doada.
- O formulário exige a data no formato AAAA-MM-DD, sem seletor de calendário.
- Não há autenticação de usuários.

**Trabalhos futuros**

- Integrar à interface e ao banco as classes `Beneficiario`, `PedidoDistribuicao`, `Agendamento` e `Transporte`, que já existem no modelo;
- Adicionar relatórios de estoque e de lotes próximos do vencimento;
- Incluir seletor de data e validações mais amigáveis na interface;
- Implementar controle de acesso por perfil de usuário;
- Criar testes automatizados para as regras de negócio.

---

## REFERÊNCIAS

ASSOCIAÇÃO BRASILEIRA DE NORMAS TÉCNICAS. **ABNT NBR 6023**: informação e documentação: referências: elaboração. Rio de Janeiro: ABNT, 2018.

APACHE SOFTWARE FOUNDATION. **Apache Maven Project**. [S. l.]: Apache Software Foundation, [2026]. Disponível em: https://maven.apache.org/. Acesso em: 12 nov. 2026.

MARIADB FOUNDATION. **MariaDB Server Documentation**. [S. l.]: MariaDB Foundation, [2026]. Disponível em: https://mariadb.com/kb/en/. Acesso em: 12 nov. 2026.

ORACLE. **Java Platform, Standard Edition Documentation**. [S. l.]: Oracle, [2026]. Disponível em: https://docs.oracle.com/en/java/. Acesso em: 12 nov. 2026.

SILVA, Felipe. **Banco_alimentos**. [S. l.]: GitHub, 2026. Disponível em: https://github.com/Felipepiv/Banco_alimentos. Acesso em: 12 nov. 2026.
