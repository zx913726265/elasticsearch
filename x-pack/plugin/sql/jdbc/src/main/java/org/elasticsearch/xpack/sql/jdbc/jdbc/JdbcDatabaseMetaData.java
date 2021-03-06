/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.sql.jdbc.jdbc;

import org.elasticsearch.xpack.sql.client.ObjectUtils;
import org.elasticsearch.xpack.sql.client.Version;
import org.elasticsearch.xpack.sql.jdbc.JdbcSQLException;
import org.elasticsearch.xpack.sql.jdbc.net.client.Cursor;
import org.elasticsearch.xpack.sql.jdbc.net.protocol.ColumnInfo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.JDBCType.INTEGER;
import static java.sql.JDBCType.SMALLINT;

/**
 * Implementation of {@link DatabaseMetaData} for Elasticsearch. Draws inspiration
 * from <a href="https://www.postgresql.org/docs/9.0/static/information-schema.html">
 * PostgreSQL</a>. Virtual/synthetic tables are not supported so the client returns
 * empty data instead of creating a query.
 */
class JdbcDatabaseMetaData implements DatabaseMetaData, JdbcWrapper {

    private final JdbcConnection con;

    JdbcDatabaseMetaData(JdbcConnection con) {
        this.con = con;
    }

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return true;
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return true;
    }

    @Override
    public String getURL() throws SQLException {
        return con.getURL();
    }

    @Override
    public String getUserName() throws SQLException {
        return con.getUserName();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return true;
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        // missing/null values are sorted (by default) last
        return true;
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        return "Elasticsearch";
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return Version.CURRENT.toString();
    }

    @Override
    public String getDriverName() throws SQLException {
        return "Elasticsearch JDBC Driver";
    }

    @Override
    public String getDriverVersion() throws SQLException {
        return Version.CURRENT.major + "." + Version.CURRENT.minor;
    }

    @Override
    public int getDriverMajorVersion() {
        return Version.CURRENT.major;
    }

    @Override
    public int getDriverMinorVersion() {
        return Version.CURRENT.minor;
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        return true;
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        //TODO: is the javadoc accurate
        return false;
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return "\"";
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        // TODO: sync this with the grammar
        return "";
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        // TODO: sync this with the grammar
        return "";
    }

    @Override
    public String getStringFunctions() throws SQLException {
        // TODO: sync this with the grammar
        return "";
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        // TODO: sync this with the grammar
        return "";
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return "";
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        return "";
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        //TODO: add Convert
        return false;
    }

    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        return false;
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return true;
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        return "schema";
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        return "procedure";
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        return "clusterName";
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        return ":";
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false;
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxConnections() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        return 0;
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return true;
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxStatements() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        return Connection.TRANSACTION_NONE == level;
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    // https://www.postgresql.org/docs/9.0/static/infoschema-routines.html
    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        return emptySet(con.cfg,
                     "ROUTINES",
                     "PROCEDURE_CAT",
                     "PROCEDURE_SCHEM",
                     "PROCEDURE_NAME",
                     "NUM_INPUT_PARAMS", INTEGER,
                     "NUM_OUTPUT_PARAMS", INTEGER,
                     "NUM_RESULT_SETS", INTEGER,
                     "REMARKS",
                     "PROCEDURE_TYPE", SMALLINT,
                     "SPECIFIC_NAME");
    }

    @Override
    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
            throws SQLException {
        return emptySet(con.cfg,
                     "PARAMETERS",
                     "PROCEDURE_CAT",
                     "PROCEDURE_SCHEM",
                     "PROCEDURE_NAME",
                     "COLUMN_NAME",
                     "COLUMN_TYPE", SMALLINT,
                     "DATA_TYPE", INTEGER,
                     "TYPE_NAME",
                     "PRECISION", INTEGER,
                     "LENGTH", INTEGER,
                     "SCALE", SMALLINT,
                     "RADIX", SMALLINT,
                     "NULLABLE", SMALLINT,
                     "REMARKS",
                     "COLUMN_DEF",
                     "SQL_DATA_TYPE", INTEGER,
                     "SQL_DATETIME_SUB", INTEGER,
                     "CHAR_OCTET_LENGTH", INTEGER,
                     "ORDINAL_POSITION", INTEGER,
                     "IS_NULLABLE",
                     "SPECIFIC_NAME");
    }

    // return the cluster name as the catalog (database)
    // helps with the various UIs
    private String defaultCatalog() throws SQLException {
        return con.client.serverInfo().cluster;
    }

    private boolean isDefaultCatalog(String catalog) throws SQLException {
        // null means catalog info is irrelevant
        // % means return all catalogs
        // "" means return those without a catalog
        return catalog == null || catalog.equals("") || catalog.equals("%") || catalog.equals(defaultCatalog());
    }

    private boolean isDefaultSchema(String schema) {
        // null means schema info is irrelevant
        // % means return all schemas`
        // "" means return those without a schema
        return schema == null || schema.equals("") || schema.equals("%");
    }

    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        String statement = "SYS TABLES CATALOG LIKE ? LIKE ?";

        if (types != null && types.length > 0) {
            statement += " TYPE ?";

            if (types.length > 1) {
                for (int i = 1; i < types.length; i++) {
                    statement += ", ?";
                }
            }
        }

        PreparedStatement ps = con.prepareStatement(statement);
        ps.setString(1, catalog != null ? catalog.trim() : "%");
        ps.setString(2, tableNamePattern != null ? tableNamePattern.trim() : "%");

        if (types != null && types.length > 0) {
            for (int i = 0; i < types.length; i++) {
                ps.setString(3 + i, types[i]);
            }
        }

        return ps.executeQuery();
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        Object[][] data = { { "", defaultCatalog() } };
        return memorySet(con.cfg, columnInfo("SCHEMATA",
                                    "TABLE_SCHEM",
                                    "TABLE_CATALOG"), data);
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        List<ColumnInfo> info = columnInfo("SCHEMATA",
                                           "TABLE_SCHEM",
                                           "TABLE_CATALOG");
        if (!isDefaultCatalog(catalog) || !isDefaultSchema(schemaPattern)) {
            return emptySet(con.cfg, info);
        }
        Object[][] data = { { "", defaultCatalog() } };
        return memorySet(con.cfg, info, data);
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        return con.createStatement().executeQuery("SYS CATALOGS");
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        return con.createStatement().executeQuery("SYS TABLE TYPES");
    }

    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        PreparedStatement ps = con.prepareStatement("SYS COLUMNS CATALOG ? TABLE LIKE ? LIKE ?");
        // TODO: until passing null works, pass an empty string
        ps.setString(1, catalog != null ? catalog.trim() : "");
        ps.setString(2, tableNamePattern != null ? tableNamePattern.trim() : "%");
        ps.setString(3, columnNamePattern != null ? columnNamePattern.trim() : "%");
        return ps.executeQuery();
    }

    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        throw new SQLFeatureNotSupportedException("Privileges not supported");
    }

    @Override
    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        throw new SQLFeatureNotSupportedException("Privileges not supported");
    }

    @Override
    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        throw new SQLFeatureNotSupportedException("Row identifiers not supported");
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        throw new SQLFeatureNotSupportedException("Version column not supported yet");
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        throw new SQLFeatureNotSupportedException("Primary keys not supported");
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        throw new SQLFeatureNotSupportedException("Imported keys not supported");
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        throw new SQLFeatureNotSupportedException("Exported keys not supported");
    }

    @Override
    public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog,
            String foreignSchema, String foreignTable) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cross reference not supported");
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        return con.createStatement().executeQuery("SYS TYPES");
    }

    @Override
    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        throw new SQLFeatureNotSupportedException("Indicies not supported");
    }

    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY == type;
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY == type && ResultSet.CONCUR_READ_ONLY == concurrency;
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        return emptySet(con.cfg,
                    "USER_DEFINED_TYPES",
                    "TYPE_CAT",
                    "TYPE_SCHEM",
                    "TYPE_NAME",
                    "CLASS_NAME",
                    "DATA_TYPE", INTEGER,
                    "REMARKS",
                    "BASE_TYPE", SMALLINT);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return con;
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        return emptySet(con.cfg,
                     "SUPER_TYPES",
                     "TYPE_CAT",
                     "TYPE_SCHEM",
                     "TYPE_NAME",
                     "SUPERTYPE_CAT",
                     "SUPERTYPE_SCHEM",
                     "SUPERTYPE_NAME",
                     "BASE_TYPE");
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        return emptySet(con.cfg, "SUPER_TABLES",
                     "TABLE_CAT",
                     "TABLE_SCHEM",
                     "TABLE_NAME",
                     "SUPERTABLE_NAME");
    }

    @Override
    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern)
            throws SQLException {
        return emptySet(con.cfg,
                     "ATTRIBUTES",
                     "TYPE_CAT",
                     "TYPE_SCHEM",
                     "TYPE_NAME",
                     "ATTR_NAME",
                     "DATA_TYPE", INTEGER,
                     "ATTR_TYPE_NAME",
                     "ATTR_SIZE", INTEGER,
                     "DECIMAL_DIGITS", INTEGER,
                     "NUM_PREC_RADIX", INTEGER,
                     "NULLABLE", INTEGER,
                     "REMARKS",
                     "ATTR_DEF",
                     "SQL_DATA_TYPE", INTEGER,
                     "SQL_DATETIME_SUB", INTEGER,
                     "CHAR_OCTET_LENGTH", INTEGER,
                     "ORDINAL_POSITION", INTEGER,
                     "IS_NULLABLE",
                     "SCOPE_CATALOG",
                     "SCOPE_SCHEMA",
                     "SCOPE_TABLE",
                     "SOURCE_DATA_TYPE", SMALLINT);
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT == holdability;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return con.esInfoMajorVersion();
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return con.esInfoMinorVersion();
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return Version.jdbcMajorVersion();
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return Version.jdbcMinorVersion();
    }

    @Override
    public int getSQLStateType() throws SQLException {
        return DatabaseMetaData.sqlStateSQL;
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return false;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        throw new SQLException("Client info not implemented yet");
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        return emptySet(con.cfg,
                     "FUNCTIONS",
                     "FUNCTION_CAT",
                     "FUNCTION_SCHEM",
                     "FUNCTION_NAME",
                     "REMARKS",
                     "FUNCTION_TYPE", SMALLINT,
                     "SPECIFIC_NAME");
    }

    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern)
            throws SQLException {
        return emptySet(con.cfg,
                     "FUNCTION_COLUMNS",
                     "FUNCTION_CAT",
                     "FUNCTION_SCHEM",
                     "FUNCTION_NAME",
                     "COLUMN_NAME",
                     "DATA_TYPE", INTEGER,
                     "TYPE_NAME",
                     "PRECISION", INTEGER,
                     "LENGTH", INTEGER,
                     "SCALE", SMALLINT,
                     "RADIX", SMALLINT,
                     "NULLABLE", SMALLINT,
                     "REMARKS",
                     "CHAR_OCTET_LENGTH", INTEGER,
                     "ORDINAL_POSITION", INTEGER,
                     "IS_NULLABLE",
                     "SPECIFIC_NAME");
    }

    @Override
    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        return emptySet(con.cfg,
                     "PSEUDO_COLUMNS",
                     "TABLE_CAT",
                     "TABLE_SCHEM",
                     "TABLE_NAME",
                     "COLUMN_NAME",
                     "DATA_TYPE", INTEGER,
                     "COLUMN_SIZE", INTEGER,
                     "DECIMAL_DIGITS", INTEGER,
                     "NUM_PREC_RADIX", INTEGER,
                     "REMARKS",
                     "COLUMN_USAGE",
                     "IS_NULLABLE");
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return false;
    }

    private static List<ColumnInfo> columnInfo(String tableName, Object... cols) throws JdbcSQLException {
        List<ColumnInfo> columns = new ArrayList<>();

        for (int i = 0; i < cols.length; i++) {
            Object obj = cols[i];
            if (obj instanceof String) {
                String name = obj.toString();
                JDBCType type = JDBCType.VARCHAR;
                if (i + 1 < cols.length) {
                    // check if the next item it's a type
                    if (cols[i + 1] instanceof JDBCType) {
                        type = (JDBCType) cols[i + 1];
                        i++;
                    }
                    // it's not, use the default and move on
                }
                columns.add(new ColumnInfo(name, type, tableName, "INFORMATION_SCHEMA", "", "", 0));
            }
            else {
                throw new JdbcSQLException("Invalid metadata schema definition");
            }
        }
        return columns;
    }

    private static ResultSet emptySet(JdbcConfiguration cfg, String tableName, Object... cols) throws JdbcSQLException {
        return new JdbcResultSet(cfg, null, new InMemoryCursor(columnInfo(tableName, cols), null));
    }

    private static ResultSet emptySet(JdbcConfiguration cfg, List<ColumnInfo> columns) {
        return memorySet(cfg, columns, null);
    }

    private static ResultSet memorySet(JdbcConfiguration cfg, List<ColumnInfo> columns, Object[][] data) {
        return new JdbcResultSet(cfg, null, new InMemoryCursor(columns, data));
    }

    static class InMemoryCursor implements Cursor {

        private final List<ColumnInfo> columns;
        private final Object[][] data;

        private int row = -1;

        InMemoryCursor(List<ColumnInfo> info, Object[][] data) {
            this.columns = info;
            this.data = data;
        }

        @Override
        public List<ColumnInfo> columns() {
            return columns;
        }

        @Override
        public boolean next() {
            if (!ObjectUtils.isEmpty(data) && row < data.length - 1) {
                row++;
                return true;
            }
            return false;
        }

        @Override
        public Object column(int column) {
            return data[row][column];
        }

        @Override
        public int batchSize() {
            return data.length;
        }

        @Override
        public void close() throws SQLException {
            // this cursor doesn't hold any resource - no need to clean up
        }
    }
}
