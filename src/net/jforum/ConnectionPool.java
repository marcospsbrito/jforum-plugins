package net.jforum;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

/** 
 * Every query and connection come from here.
 * Before you can get some connection from the pool, you must
 * call init() to initialize the connections. After that, 
 * to get a conneciton, simple use
 *
 * <blockquote><pre>
 * 		Connection con = ConnectionPool.getPool().getConnection();
 * </pre></blockquote>
 *
 * The name of the query is associated with a Prepared Statement string, which is inside 
 * of a properties file, called [dbtype].sql, where the type is specified inside of 
 * database.properties.<br>
 * <br>
 * Also manages the connection to the database. The configuration is a config file which
 * is read at the first <code>init()</code> call. You must init it before using the pool.<p>
 *
 * <code>ConnectionPool</code> is for now a singleton.
 *
 * @author Paulo Silveira
 * */

public class ConnectionPool 
{
	private static ConnectionPool pool;
	private static boolean isDatabaseUp;
	
	private int minConnections, maxConnections, timeout;
	private String connectionString;
	private String dbType;

	/**
	* It is the connection pool
	*/
	private LinkedList connections = new LinkedList();
    
	/**
	 * It has all the connections, even the ones in use.
	 * This way, the garbage collector does not get them.
	 */
	private LinkedList allConnections = new LinkedList();

	// for sinalizing a release
	private Object releaseSignal = new Object();
	
	private static final boolean debug = false;
	
	/**
	 * Private constructor that loads the driver and set the configuration from
	 * the properties file. It will also initialize the Database driver.
	 * 
	 * @param dbConfigFile The full path plus the filename to the file which contains database specifc parameters
	 * @throws IOException
	 * @throws Exception
	*/
	private ConnectionPool(String dbConfigFile) throws IOException, SQLException
	{
		Properties config = new Properties();        
		config.load(new FileInputStream(dbConfigFile));

		try {
			Class.forName(config.getProperty("database.connection.driver"));
			
			this.minConnections = Integer.parseInt(config.getProperty("database.connection.pool.min"));
			this.maxConnections = Integer.parseInt(config.getProperty("database.connection.pool.max"));
			this.timeout = Integer.parseInt(config.getProperty("database.connection.pool.timeout"));
			this.dbType = config.getProperty("database.type");

			this.connectionString = config.getProperty("database.connection.string");
			
			if (debug) {
				System.err.println("*********************************************");
				System.err.println("******** STARTING CONNECTION POOL ***********");
				System.err.println("*********************************************");
				System.err.println("database.connection.driver = "+ config.getProperty("database.connection.driver"));
				System.err.println("minConnections = "+ this.minConnections);
				System.err.println("maxConnections = "+ this.maxConnections);
				System.err.println("timeout = "+ this.timeout);
				System.err.println("*********************************************");
			}

			for (int i = 0; i < this.minConnections; i++) {
				Connection conn = DriverManager.getConnection(this.connectionString);
				this.connections.addLast(conn);
				this.allConnections.add(conn);
				
				if (debug) {
					Date now = new Date();
					System.err.println(now.toString() + " openning connection "+ (i + 1));
				}
			}
			
			isDatabaseUp = true;
		}
		catch (ClassNotFoundException e) {
			System.err.println("Ouch... Cannot find database driver: "+ config.getProperty("database.connection.driver"));
		}
    }

    /**
     * Inits ConnectionPool. 
	 * Init Conenction pool with a config file that will be used to load the driver 
     * configuration and queries.<p>
     * If the pool was already initialized, this action will take no effect.
	 *
	 * @param configFile The configuration file filename, including full PATH
	 * @throws Exception
	 * @throws IOException
     */
    public static void init(String configFile) throws SQLException, IOException
    {
        if (pool == null || !isDatabaseUp) {
            pool = new ConnectionPool(configFile);
        }
    }
    
    public static boolean isDatabaseUp()
    {
    	return isDatabaseUp;
    }
    
	/**
	 *  Gets the ConnectionPool singleton instance. 
	 * 
	 * @return <code>ConnectionPool</code> object
	 * @throws java.sql.SQLException
	 **/
	public static ConnectionPool getPool() throws SQLException
	{
		if (pool == null)
			throw new SQLException("ConnectionPool was not initialized yet. You need to call init() first.");
		
		return pool;
	}

	/**
	 * Gets a connection to the database.<p> 
	 * 
	 * So you need to release it, after use. It will not be a huge problem if you do not
	 * release it, but this way you will get a better performance.<p>
	 * Thread safe.
	 *
	 * @return <code>Connection</code> object
	 * @throws java.sql.SQLException	 
	 */
	public synchronized Connection getConnection() throws SQLException 
	{
		Connection conn = null;

		// if there is enought Connections
		if (this.connections.size() != 0) {
			synchronized (this.connections) {
				conn = (Connection) this.connections.removeFirst();
			}

			// take a look if the connection has died!
			try {
				if (conn.isClosed()) {
					synchronized (this.allConnections) {
						this.allConnections.remove(conn);
						conn = DriverManager.getConnection(this.connectionString);
						this.allConnections.add(conn);
					}
				}
			}
			catch (SQLException e) {
				if (debug) {
					System.err.println("Cannot reconnect a closed connection:" + this.connectionString + e);
				}
				
				throw e;
			}

			return conn;
		}
        // Otherwise, create a new one if the Pool is now full
		else {
			if (this.allConnections.size() < this.maxConnections) {
				try {
					conn = DriverManager.getConnection(this.connectionString);
				}
				catch (SQLException e) {
					if (debug) {
						System.err.println("Cannot stabilish a NEW connection to the database:" + this.connectionString + e);
					}
					
                    throw e;
				}
				
				// registering the new connection
				synchronized (this.allConnections) {
					this.allConnections.add(conn);
				}
				
				return conn;
			}
		}

        /*
         * Trying to get some Connections stuck inside some Queries.
         * The Query.finalize method will release them.
         * We need to wait sometime, so the GC will get the Connections for us
         */
		System.gc();

		synchronized (this.releaseSignal) {
			/*
			 * Not inside a while, since we are giving it a maximum timeout, 
			 * and this method is already SYNC, there is no way that we will loose
			 * the state if we receive a signal
			 */
			if (this.connections.size() == 0) {
				try {
					this.releaseSignal.wait(this.timeout);
				}
				catch (InterruptedException e) {
					if (debug)
						System.err.println("Problems while waiting for connection. "+ e);
				}
			}

			if (this.connections.size() == 0) {
				// TIMED OUT!!!!
				if (debug) {
					System.err.println( "Pool is empty, and th waiting for one timed out!"
						+ "If this is happening too much, your code is probably not releasing the Connections."
						+ "If you cant solve this, set your 'database.connection.pool.timeout' to a bigger number.");
				}
			}
			else {
				synchronized (this.connections) {
					conn = (Connection) this.connections.removeFirst();
				}
				
				return conn;
			}
		}
		
		return conn;
	}

	/**
	 * Releases a connection, making it available to the pool once more.
	 *
	 * @param conn <code>Connection</code> object to release
	 * @throws java.sql.SQLException
	 */
	public void releaseConnection(Connection conn) throws SQLException 
	{
		if (conn == null) {
			if (debug)
			System.err.println("Cannot release a NULL connection!");
				
			return;
		}

		/*
		 * Sync because collection.contains() uses the fail fast iterator!
		 */
		synchronized (this.allConnections) {
			if (!this.allConnections.contains(conn) && debug) {
				System.err.println("Cannot release a connection that is not from this pool!");
				
				return;
			}
			
			try {
				if (conn.isClosed()) {
					this.allConnections.remove(conn);
					
					return;
				}
			}
			catch (SQLException e) {
				if (debug) {
					System.err.println("Cannot get info about the conn: "+ e);
				}
			}
		}
		
		synchronized (this.releaseSignal) {
			synchronized (this.connections) {
				this.connections.addLast(conn);
			}
			
			this.releaseSignal.notify();
		}
		
		if (debug) {
			System.err.println("Releasing connection...");
		}
	}

	/**
	 * Returns the status
	 * 
	 * @return The status
	 */
	public synchronized String getStatus() 
	{
		StringBuffer status = new StringBuffer();
		int i = 0;

		Iterator it = this.allConnections.iterator();
		while (it.hasNext()) {
			i++;
			status.append("Connection " + i + ": ");

			Connection c = (Connection) it.next();
			if (c != null) {
				try {
                    status.append(c + " closed: " + c.isClosed());
				}
				catch (SQLException e) {
                    status.append(e);
				}
			}
			else {
				status.append("NULL!!!");
			}

			status.append("\n");
		}

		status.append("\nPOOL:\n\n");
		i = 0;

		it = this.connections.iterator();
		while (it.hasNext()) {
			i++;
			status.append("Connection " + i + ": ");

			Connection c = (Connection) it.next();
			if (c != null) {
				try {
					status.append(c + " closed: " + c.isClosed());
				}
				catch (SQLException e) {
					status.append(e);
				}
			}
			else
				status.append("NULL!!!");

			status.append("\n");
		}
        
		return status.toString();
	}
	
	/**
	 * Pega o total de registors retornados por uma instrucao SELECT.
	 * 
	 * @param rs Referencia para um objeto <code>ResultSet</code> que contem o sql executado
	 * @return Numero total de registros
	 * @throws Exception
	 * */
	public static int getRowCount(ResultSet rs) throws SQLException
	{
		int total = 0;
		
		rs.last();
		total = rs.getRow();
		rs.beforeFirst();
		
		return total;
	}
}
