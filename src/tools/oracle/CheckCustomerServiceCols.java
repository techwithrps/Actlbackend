package tools.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckCustomerServiceCols {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//144.24.138.129:1521/pdb1.sub06121018360.prodvcn.oraclevcn.com";
        String user = "SPJLIVE";
        String pass = "SPjlive_0112#";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("=== ACTL.CUSTOMER_MASTER columns ===");
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM ACTL.CUSTOMER_MASTER WHERE ROWNUM <= 1")) {
                int cols = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getMetaData().getColumnName(i) + " | ");
                }
                System.out.println();
            }

            System.out.println("=== ACTL.SERVICE_MASTER columns ===");
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM ACTL.SERVICE_MASTER WHERE ROWNUM <= 1")) {
                int cols = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getMetaData().getColumnName(i) + " | ");
                }
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
