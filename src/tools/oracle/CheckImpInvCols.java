package tools.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckImpInvCols {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//144.24.138.129:1521/pdb1.sub06121018360.prodvcn.oraclevcn.com";
        String user = "SPJLIVE";
        String pass = "SPjlive_0112#";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            
            try (ResultSet rs = stmt.executeQuery(
                "SELECT t.TERMINAL_ID, t.TERMINAL_CODE, t.TERMINAL_NAME, COUNT(i.INVOICE_ID) as inv_count, SUM(i.TOTAL_AMOUNT) as gross " +
                "FROM ACTL.IMP_INVOICE i " +
                "LEFT JOIN ACTL.TERMINAL_MASTER t ON i.TERMINAL_ID = t.TERMINAL_ID " +
                "GROUP BY t.TERMINAL_ID, t.TERMINAL_CODE, t.TERMINAL_NAME " +
                "ORDER BY inv_count DESC")) {
                while (rs.next()) {
                    System.out.printf("Terminal ID: %d | Code: %s | Name: %s | Invoices: %d | Gross: %.2f\n",
                        rs.getInt("TERMINAL_ID"),
                        rs.getString("TERMINAL_CODE"),
                        rs.getString("TERMINAL_NAME"),
                        rs.getInt("inv_count"),
                        rs.getDouble("gross"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
