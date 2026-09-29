package airhacks.ebank.reporting.control;

import static airhacks.ebank.accounting.entity.Account.tableName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import airhacks.ebank.Control;
import airhacks.ebank.logging.control.EBLog;
import jakarta.inject.Inject;

/// Read-only access to `accounting`'s persisted accounts — deliberately via
/// plain JDBC instead of the entities, so reporting never joins the JPA
/// persistence context and cannot mutate state. `Account.tableName()` keeps
/// the SQL aligned with the entity mapping.
@Control
public class AccountQuery {
    
    @Inject
    DataSource dataSource;

    @Inject
    EBLog log;

    String sql = """
        SELECT iban
        FROM %s
        """.formatted(tableName());


    /// @throws AccountQueryException on database failure — a boundary maps it
    ///                               to its protocol (the JAX-RS runtime yields
    ///                               HTTP 500)
    public List<String> asIBANs(){
        var ibans = new ArrayList<String>();
        try (var con = this.dataSource.getConnection();
             var stmt = con.prepareStatement(sql);
             var rs = stmt.executeQuery();) {
            
            while (rs.next()) {
                ibans.add(rs.getString("iban"));
            }
        } catch (SQLException e) {
            var error = "Database access error " + e.getMessage();
            this.log.error(error,e);
            throw new AccountQueryException(error, e);
        }      
        return ibans;
    }
}
