package tools.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckActlServices {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//144.24.138.129:1521/pdb1.sub06121018360.prodvcn.oraclevcn.com";
        String user = "SPJLIVE";
        String pass = "SPjlive_0112#";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("ACTL.SERVICE_MASTER sample:");
            try (ResultSet rs = stmt.executeQuery("SELECT SERVICE_ID, SERVICE_CODE, SERVICE_NAME, SERVICE_TYPE_CODE FROM ACTL.SERVICE_MASTER WHERE ROWNUM <= 15")) {
                while (rs.next()) {
                    System.out.printf("%d | %s | %s | %s\n",
                        rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
