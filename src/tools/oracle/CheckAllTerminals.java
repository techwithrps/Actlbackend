package tools.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckAllTerminals {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//144.24.138.129:1521/pdb1.sub06121018360.prodvcn.oraclevcn.com";
        String user = "SPJLIVE";
        String pass = "SPjlive_0112#";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            
            try (ResultSet rs = stmt.executeQuery("SELECT TERMINAL_ID, TERMINAL_CODE, TERMINAL_NAME, STATE_CODE, ADDRESS FROM ACTL.TERMINAL_MASTER ORDER BY TERMINAL_ID")) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("TERMINAL_ID") + " | Code: " + rs.getString("TERMINAL_CODE") + " | Name: " + rs.getString("TERMINAL_NAME") + " | State: " + rs.getString("STATE_CODE"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
