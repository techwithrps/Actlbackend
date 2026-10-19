package tools.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckProcSource {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//144.24.138.129:1521/pdb1.sub06121018360.prodvcn.oraclevcn.com";
        String user = "SPJLIVE";
        String pass = "SPjlive_0112#";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            
            try (ResultSet rs = stmt.executeQuery("SELECT text FROM all_source WHERE owner='ACTL' AND name='SP_PORTAL_LIVE_ANALYTICS' ORDER BY line")) {
                while (rs.next()) {
                    System.out.print(rs.getString("text"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
