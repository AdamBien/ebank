package airhacks.ebank.reporting.control;

import static airhacks.ebank.accounting.entity.Account.tableName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import io.helidon.integrations.langchain4j.Ai;

import airhacks.ebank.logging.control.EBLog;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
@Ai.Tool
public class AccountQuery {
    
    @Inject
    DataSource dataSource;

    @Inject
    EBLog log;

    String sql = """
        SELECT iban
        FROM %s
        """.formatted(tableName());


    @Tool("Returns a list of IBAN numbers of existing accounts.")
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
            throw new WebApplicationException(error, 500);
        }      
        return ibans;
    }
}
