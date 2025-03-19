package airhacks.ebank.reporting.control;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import static airhacks.ebank.accounting.entity.Account.tableName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class AccountQuery {
    
    @Inject
    DataSource dataSource;

    @Inject
    Logger logger;

    String sql = """
        SELECT iban
        FROM %s
        """.formatted(tableName());


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
            logger.log(Level.ERROR, e);
            throw new WebApplicationException(error, 500);
        }      
        return ibans;
    }
}
