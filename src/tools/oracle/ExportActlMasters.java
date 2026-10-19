package tools.oracle;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ExportActlMasters {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//144.24.138.129:1521/pdb1.sub06121018360.prodvcn.oraclevcn.com";
        String user = "SPJLIVE";
        String pass = "SPjlive_0112#";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Reading ACTL.COMPANY_MASTER...");
            try (ResultSet rs = stmt.executeQuery("SELECT COMPANY_ID, COMPANY_CODE, COMPANY_NAME, ADDRESS, CITY, STATE, STATE_CODE, GSTIN, PAN_NO FROM ACTL.COMPANY_MASTER ORDER BY COMPANY_ID")) {
                while (rs.next()) {
                    System.out.printf("Company: %d | %s | %s | GSTIN: %s | State: %s\n",
                        rs.getInt("COMPANY_ID"),
                        rs.getString("COMPANY_CODE"),
                        rs.getString("COMPANY_NAME"),
                        rs.getString("GSTIN"),
                        rs.getString("STATE_CODE"));
                }
            }

            System.out.println("\nCounting ACTL.CUSTOMER_MASTER...");
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM ACTL.CUSTOMER_MASTER")) {
                if (rs.next()) System.out.println("Total customers: " + rs.getInt(1));
            }

            System.out.println("\nCounting ACTL.TERMINAL_MASTER...");
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM ACTL.TERMINAL_MASTER")) {
                if (rs.next()) System.out.println("Total terminals: " + rs.getInt(1));
            }

            System.out.println("\nChecking Invoice terminals in ACTL.IMP_INVOICE...");
            try (ResultSet rs = stmt.executeQuery(
                "SELECT t.TERMINAL_ID, t.TERMINAL_CODE, t.TERMINAL_NAME, COUNT(i.INVOICE_ID) as inv_count, SUM(i.GROSS_AMOUNT) as gross " +
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
