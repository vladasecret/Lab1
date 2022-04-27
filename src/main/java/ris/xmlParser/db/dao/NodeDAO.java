package ris.xmlParser.db.dao;

import ris.generated.osmSchema.Node;
import ris.xmlParser.db.ConnectionManager;

import java.sql.*;
import java.util.stream.Collectors;

public class NodeDAO {

    private static final String INSERT_NODE_PREPARED = "INSERT INTO nodes values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_NODE = "INSERT INTO nodes values(%s);";

    private final Connection connection;
    private final Statement statement;
    private PreparedStatement preparedStatement = null;


    public NodeDAO() throws SQLException {
        connection = ConnectionManager.getConnection();
        statement = connection.createStatement();
    }

    public String toSQLString(Node node){
        return String.format("%d, %f, %f, '%s', %d, %s, %d, %d, %s",
                node.getId(),
                node.getLat(),
                node.getLon(),
                node.getUser().replace("'", "''"),
                node.getUid(),
                node.isVisible().toString(),
                node.getVersion(),
                node.getChangeset(),
                Timestamp.from(node.getTimestamp().toGregorianCalendar().toInstant()));

    }

    public void saveNode(Node node) throws SQLException {
        String sql = String.format(INSERT_NODE, toSQLString(node));
        statement.executeUpdate(sql);
    }

    public void saveNodePrepared(Node node) throws SQLException {
        initPreparedStatement();
        setNodeValues(preparedStatement, node);
        preparedStatement.executeUpdate();
    }

    public void saveNodePreparedWithBatch(Node node) throws SQLException {
        long start = System.nanoTime();
        setNodeValues(preparedStatement, node);
        preparedStatement.addBatch();
    }

    public void executeBatch() throws SQLException {
        preparedStatement.executeBatch();
    }


    private void setNodeValues(PreparedStatement statement, Node node) throws SQLException {
        statement.setLong(1, node.getId().longValue());
        statement.setDouble(2, node.getLat());
        statement.setDouble(3, node.getLon());
        statement.setString(4, node.getUser().replace("'", "''"));
        statement.setLong(5, node.getUid().longValue());
        statement.setBoolean(6, node.isVisible());
        statement.setLong(7, node.getVersion().longValue());
        statement.setLong(8, node.getChangeset().longValue());
        statement.setTimestamp(9, new Timestamp(
                node.getTimestamp().toGregorianCalendar().getTimeInMillis()));
    }

    private void initPreparedStatement() throws SQLException {
        if (preparedStatement == null){
            preparedStatement = connection.prepareStatement(INSERT_NODE_PREPARED);
        }
    }

}
