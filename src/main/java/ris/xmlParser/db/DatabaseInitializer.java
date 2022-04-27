package ris.xmlParser.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

    private static final String CREATE_NODES_TABLE = "IF NOT EXISTS " +
            "(SELECT * FROM sys.tables where sys.tables.name = 'nodes')\n" +
            "CREATE TABLE nodes( " +
            "id bigint NOT NULL primary key, " +
            "lat float(53) NULL, " +
            "lon float(53) NULL, " +
            "[user] nchar(50) NULL, " +
            "uid bigint NULL, " +
            "visible bit NULL, " +
            "version bigint NULL, " +
            "changeset bigint NULL, " +
            "timestamp timestamp NULL " +
            ") ";

    private static final String CREATE_TAGS_TABLE = "IF NOT EXISTS (SELECT * FROM sys.tables where sys.tables.name = 'tags')\n" +
            "CREATE TABLE tags( " +
            "id bigint NOT NULL primary key, " +
            "k varchar(20) NULL, " +
            "v nvarchar(50) NULL, " +
            "nodeId bigint foreign key references nodes(id)\n" +
            ")";

    private static final String DROP_TABLES = "IF EXISTS (SELECT * FROM sys.tables where sys.tables.name = 'tags') " +
            "drop table tags;\n" +
            "IF EXISTS (SELECT * FROM sys.tables where sys.tables.name = 'nodes') " +
            "drop table nodes;";


    private final Connection connection;

    public DatabaseInitializer() throws SQLException {
        connection = ConnectionManager.getConnection();
    }

    public void createTables() throws SQLException {
        try(Statement statement = connection.createStatement()){
            statement.execute(CREATE_NODES_TABLE);
            statement.execute(CREATE_TAGS_TABLE);
        }

    }

    public void dropTables() throws SQLException {
        try(Statement statement = connection.createStatement()){
            statement.execute(CREATE_NODES_TABLE);
            statement.execute(CREATE_TAGS_TABLE);
            //connection.commit();
        }
    }


}
