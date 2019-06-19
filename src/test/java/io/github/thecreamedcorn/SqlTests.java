package io.github.thecreamedcorn;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hsqldb.Server;
import org.hsqldb.jdbc.JDBCConnection;
import org.hsqldb.persist.HsqlProperties;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;

@RunWith(Suite.class)
@SuiteClasses({})
public class SqlTests {
    @ClassRule
    public static final TestDatabase DB = new TestDatabase();

    private static class TestDatabase extends ExternalResource {
        private Server server = new Server();
        private HsqlProperties properties = new HsqlProperties();
        private File sqlToolRcFileHomeDir = null;
        private File sqlToolRcFileTmp = null;

        private TestDatabase() {
            super();
        }

        public Connection getConnection() {
            try {
                Class.forName("org.hsqldb.jdbc.JDBCDriver" );
            } catch (Exception e) {
                System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
                throw new RuntimeException(e);
            }

            try {
                return DriverManager.getConnection("jdbc:hsqldb:mem:test_db");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void before() throws Throwable {
            //move this program's sqltool.rc to home directory and temporarily relocate current one (if exists)
            File homeDir = new File(System.getenv("user.home"));
            if ((sqlToolRcFileHomeDir = new File(homeDir, "sqltool.rc")).exists()) {
                sqlToolRcFileTmp = Files.createTempFile("sqltool", ".rc.tmp").toFile();
                FileUtils.copyFile(sqlToolRcFileHomeDir, sqlToolRcFileTmp);
            }
            sqlToolRcFileHomeDir.createNewFile();
            IOUtils.copy(
                    TestDatabase.class.getClassLoader().getResourceAsStream("sqltool.rc"),
                    new FileOutputStream(sqlToolRcFileHomeDir));

            //start the database
            properties = new HsqlProperties();
            properties.setProperty("server.database.0", "mem:test_db;" +
                    "sql.syntax_ora=true;" +
                    "shutdown=true");
            properties.setProperty("server.dbname.0", "test_db");

            server.setProperties(properties);
            server.setLogWriter(new PrintWriter(new File("./src/test/resources/hsqldb-2.5.0/hsqldb.log")));
            server.setErrWriter(new PrintWriter(new File("./src/test/resources/hsqldb-2.5.0/hsqldb.err.log")));
            server.start();

            //TODO add fake sql data

        };

        @Override
        protected void after() {
            //stop the database
            server.shutdownCatalogs(1);

            //delete or move back original sqltool.rc in home directory
            if (sqlToolRcFileTmp != null) {
                try {
                    FileUtils.copyFile(sqlToolRcFileTmp, sqlToolRcFileHomeDir);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                sqlToolRcFileHomeDir.delete();
            }
        }
    }

}
