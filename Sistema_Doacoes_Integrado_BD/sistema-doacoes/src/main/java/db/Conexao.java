package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexões com o banco banco_de_alimentos_ldm (MariaDB).
 *
 * Os valores padrão são os do XAMPP (root, sem senha). Para trocar sem mexer no código,
 * use propriedades (-Ddb.url=... -Ddb.user=... -Ddb.password=...) ou variáveis de
 * ambiente (DB_URL, DB_USER, DB_PASSWORD).
 */
public final class Conexao {
        private static final String URL = config("db.url", "DB_URL",
                "jdbc:mariadb://127.0.0.1:3306/banco_de_alimentos_ldm");
        private static final String USUARIO = config("db.user", "DB_USER", "root");
        private static final String SENHA = config("db.password", "DB_PASSWORD", "felip&@12");

    private Conexao() {
    }

    public static Connection obter() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    private static String config(String propriedade, String variavel, String padrao) {
        String valor = System.getProperty(propriedade);
        if (valor == null) valor = System.getenv(variavel);
        return valor != null ? valor : padrao;
    }
}
