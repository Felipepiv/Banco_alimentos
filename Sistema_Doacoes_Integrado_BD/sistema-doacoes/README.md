# Sistema de Doações de Alimentos

Sistema em Java Desktop (Swing) com controle integrado de Doações e Lotes.

## Banco de dados

O sistema grava no MariaDB/MySQL (banco `banco_de_alimentos_ldm`).

1. Importe `banco_de_alimentos_ldm.sql` (phpMyAdmin/XAMPP) sem alterações.
2. Abra o projeto como Maven (o driver `mariadb-java-client` já está no `pom.xml`) e execute `App`.

Conexão padrão: `jdbc:mariadb://localhost:3306/banco_de_alimentos_ldm`, usuário `root`, senha vazia.
Para mudar, use `-Ddb.url`, `-Ddb.user`, `-Ddb.password` ou as variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD`.

Mapeamento: Doação → `doacao` + `doador`; lote gerado → `lote` (Numero_Lote = `LOTE-<id>`) + `produto`
(reaproveitado por categoria + unidade). O ID da doação deve ser numérico.
