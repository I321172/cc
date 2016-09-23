package i321172.dao.dataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.SmartDataSource;

import oracle.jdbc.pool.OracleDataSource;

public class SmartDataOracleSourceImp extends OracleDataSource implements SmartDataSource
{
    public SmartDataOracleSourceImp() throws SQLException
    {
        super();
        // TODO Auto-generated constructor stub
    }

    private Logger           logger     = Logger.getLogger(SmartDataOracleSourceImp.class);
    private int              point      = 0;
    private int              count      = 0;
    private int              maxCount   = 100;
    boolean                  isCloseAll = false;
    Map<Integer, Connection> conns      = new HashMap<Integer, Connection>();

    public Connection getConnection() throws SQLException
    {
        Connection con = null;
        if (count < maxCount)
        {
            con = super.getConnection();
            conns.put(count++, con);
        }
        point = point % maxCount;
        con = conns.get(point++);
        return con;
    }

    public void closeAllConnections()
    {
        logger.info("Close All Connections! Count = " + count);
        for (int i : conns.keySet())
        {
            try
            {
                if (!conns.get(i).isClosed())
                {
                    conns.get(i).close();
                }
            } catch (Exception e)
            {
                logger.error("Close Connection error", e);
            }
        }
        toEmptyPool();
    }

    public void toEmptyPool()
    {
        conns.clear();
        point = 0;
        count = 0;
        this.logger.info("To Empty Connection Pool!");
    }

    private static final long serialVersionUID = 1L;

    public int getCurrentCount()
    {
        return count < maxCount ? count : maxCount + 1;
    }

    public int getCount()
    {
        return count;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean shouldClose(Connection con)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public int getMaxCount()
    {
        return maxCount;
    }

    public void setMaxCount(int maxCount)
    {
        this.maxCount = maxCount;
    }

}