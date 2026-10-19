package tools.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckActlProcs {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//144.24.138.129:1521/pdb1.sub06121018360.prodvcn.oraclevcn.com";
        String user = "SPJLIVE";
        String pass = "SPjlive_0112#";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Checking ACTL procedures...");
            try (ResultSet rs = stmt.executeQuery("SELECT owner, object_name, object_type, status FROM all_objects WHERE owner='ACTL' AND object_type IN ('PROCEDURE', 'PACKAGE') ORDER BY object_name")) {
                while (rs.next()) {
                    System.out.printf("%s | %s | %s | %s\n",
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
