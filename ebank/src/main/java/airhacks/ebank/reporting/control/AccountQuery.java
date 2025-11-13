package airhacks.ebank.reporting.control;

import static airhacks.ebank.accounting.entity.Account.tableName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import airhacks.ebank.Control;
import airhacks.ebank.logging.control.EBLog;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;

/**
 * Queries account data for reporting purposes.
 * Provides read-only access to account information without modifying state.
 */
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


    /**
     * Retrieves all account identifiers for reporting and listing purposes.
     *
     * @return list of IBANs from all accounts in the system
     * @throws InternalServerErrorException if database access fails
     */
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
            throw new InternalServerErrorException(error);
        }      
        return ibans;
    }
}
