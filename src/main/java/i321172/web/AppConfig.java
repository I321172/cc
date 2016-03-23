package i321172.web;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ImportResource("classpath*:properties-config.xml")
public class AppConfig
{
    private Logger logger = Logger.getLogger(getClass());

    @Bean(name = "oracleSource")
    public DataSource getOracleSource(@Value("${oracle.url}") String url, @Value("${oracle.user}") String user,
            @Value("${oracle.password}") String password)
    {
        try
        {
            OracleDataSource dataSource = new OracleDataSource();
            dataSource.setURL(url);;
            dataSource.setUser(user);
            dataSource.setPassword(password);
            return dataSource;
        } catch (SQLException e)
        {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate getJdbcTemplate(@Qualifier("oracleSource") DataSource source)
    {
        return new JdbcTemplate(source);
    }
}
