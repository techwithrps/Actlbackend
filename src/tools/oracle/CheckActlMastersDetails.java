package tools.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckActlMastersDetails {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//144.24.138.129:1521/pdb1.sub06121018360.prodvcn.oraclevcn.com";
        String user = "SPJLIVE";
        String pass = "SPjlive_0112#";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("=== ACTL.COMPANY_MASTER ===");
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM ACTL.COMPANY_MASTER ORDER BY COMPANY_ID")) {
                int cols = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getMetaData().getColumnName(i) + " | ");
                }
                System.out.println();
                while (rs.next()) {
                    for (int i = 1; i <= cols; i++) {
                        System.out.print(rs.getString(i) + " | ");
                    }
                    System.out.println();
                }
            } catch (Exception e) {
                System.out.println("Error reading COMPANY_MASTER: " + e.getMessage());
            }

            System.out.println("\n=== ACTL.TERMINAL_MASTER ===");
            try (ResultSet rs = stmt.executeQuery("SELECT TERMINAL_ID, TERMINAL_NAME, TERMINAL_CODE, STATE, GSTIN FROM ACTL.TERMINAL_MASTER ORDER BY TERMINAL_ID")) {
                while (rs.next()) {
                    System.out.println(rs.getInt("TERMINAL_ID") + " | " + 
                                       rs.getString("TERMINAL_NAME") + " | " + 
                                       rs.getString("TERMINAL_CODE") + " | " +
                                       rs.getString("STATE") + " | " +
                                       rs.getString("GSTIN"));
                }
            } catch (Exception e) {
                System.out.println("Error reading TERMINAL_MASTER: " + e.getMessage());
            }

            System.out.println("\n=== ACTL INVOICE COMPANY_ID & TERMINAL_ID DISTRIBUTION ===");
            try (ResultSet rs = stmt.executeQuery("SELECT COMPANY_ID, TERMINAL_ID, COUNT(*) cnt FROM ACTL.IMP_INVOICE GROUP BY COMPANY_ID, TERMINAL_ID ORDER BY cnt DESC")) {
                while (rs.next()) {
                    System.out.println("Company: " + rs.getString("COMPANY_ID") + " | Terminal: " + rs.getString("TERMINAL_ID") + " | Count: " + rs.getInt("cnt"));
                }
            }

            System.out.println("\n=== ANY OTHER INVOICE TABLES IN ACTL? ===");
            try (ResultSet rs = stmt.executeQuery("SELECT table_name FROM all_tables WHERE owner='ACTL' AND table_name LIKE '%INV%'")) {
                while (rs.next()) {
                    System.out.println("Table: " + rs.getString(1));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
