package i321172.core;

import oracle.jdbc.pool.OracleConnectionCacheImpl;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.pool.OracleOCIConnectionPool;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;

import i321172.dao.dataSource.SmartDataOracleSourceImp;

@Configuration
@ImportResource("classpath*:properties-config.xml")
public class AppConfig
{
    @Bean(name = "oracleSource", destroyMethod = "closeAllConnections")
    public DataSource getOracleSource() throws SQLException, IOException
    {
        InputStream is = AppConfig.class.getResourceAsStream("/jdbc.properties");
        Properties p = new Properties();
        p.load(is);
        String user = p.getProperty("oracle.user");
        String password = p.getProperty("oracle.password");
        String url = p.getProperty("oracle.url");

        OracleDataSource dataSource = new SmartDataOracleSourceImp();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setURL(url);
        return dataSource;
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate getJdbcTemplate(@Qualifier("oracleSource") DataSource source)
    {
        return new JdbcTemplate(source);
    }
}
