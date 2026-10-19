package tools.oracle;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GenerateActlMastersData {
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ").trim();
    }

    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//144.24.138.129:1521/pdb1.sub06121018360.prodvcn.oraclevcn.com";
        String user = "SPJLIVE";
        String pass = "SPjlive_0112#";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            System.out.println("1. Generating ACTL companyMasters.json...");
            StringBuilder compJson = new StringBuilder("{\n  \"companies\": [\n");
            try (ResultSet rs = stmt.executeQuery("SELECT COMPANY_ID, COMPANY_CODE, COMPANY_NAME, ADDRESS, CITY, STATE, STATE_CODE, GSTIN, PAN_NO FROM ACTL.COMPANY_MASTER ORDER BY COMPANY_ID")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) compJson.append(",\n");
                    first = false;
                    int id = rs.getInt("COMPANY_ID");
                    String code = escape(rs.getString("COMPANY_CODE"));
                    String name = escape(rs.getString("COMPANY_NAME"));
                    String gstin = escape(rs.getString("GSTIN"));
                    String city = escape(rs.getString("CITY"));
                    String state = rs.getString("STATE_CODE") != null && rs.getString("STATE_CODE").equals("06") ? "HARYANA" : "DELHI";
                    String addr = escape(rs.getString("ADDRESS"));

                    compJson.append(String.format(
                        "    {\n      \"id\": %d,\n      \"companyId\": %d,\n      \"code\": \"%s\",\n      \"name\": \"%s\",\n      \"gstin\": \"%s\",\n      \"director\": \"Management\",\n      \"city\": \"%s\",\n      \"state\": \"%s\",\n      \"address\": \"%s\"\n    }",
                        id, id, code, name, gstin, city, state, addr
                    ));
                }
            }
            compJson.append("\n  ],\n");

            // Company Terminals
            compJson.append("  \"companyTerminals\": {\n");
            compJson.append("    \"1\": [{\"id\": 1, \"terminalId\": 1, \"name\": \"ASSOCIATED CONTAINER TERMINALS LIMITED\", \"code\": \"MATP\"}],\n");
            compJson.append("    \"2\": [{\"id\": 2, \"terminalId\": 2, \"name\": \"ASSOCIATED CONTAINER TERMINALS LIMITED DELHI\", \"code\": \"ACTL\"}],\n");
            compJson.append("    \"3\": [{\"id\": 6, \"terminalId\": 6, \"name\": \"R.R. JOSHI SHIPPING & FORWARDING PVT LTD\", \"code\": \"RRJ\"}]\n");
            compJson.append("  },\n");

            // Company Customers (Top customers per company/entity)
            compJson.append("  \"companyCustomers\": {\n");
            compJson.append("    \"1\": [\n");
            try (ResultSet rs = stmt.executeQuery(
                "SELECT c.CUSTOMER_ID, c.CUSTOMER_NAME FROM (" +
                "  SELECT BILL_TO, COUNT(*) cnt FROM ACTL.IMP_INVOICE GROUP BY BILL_TO ORDER BY cnt DESC" +
                ") b JOIN ACTL.CUSTOMER_MASTER c ON b.BILL_TO = c.CUSTOMER_ID WHERE ROWNUM <= 100")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) compJson.append(",\n");
                    first = false;
                    compJson.append(String.format("      {\"id\": %d, \"customerId\": %d, \"name\": \"%s\"}",
                        rs.getInt("CUSTOMER_ID"), rs.getInt("CUSTOMER_ID"), escape(rs.getString("CUSTOMER_NAME"))));
                }
            }
            compJson.append("\n    ]\n");
            compJson.append("  },\n");

            // TriMatrix
            compJson.append("  \"triMatrix\": []\n");
            compJson.append("}\n");

            try (FileWriter fw = new FileWriter("/Users/iamrps/Desktop/actl/backend/src/data/companyMasters.json")) {
                fw.write(compJson.toString());
            }
            System.out.println("companyMasters.json written successfully!");

            // 2. Generating ACTL masters.json
            System.out.println("2. Generating ACTL masters.json...");
            StringBuilder mastersJson = new StringBuilder("{\n  \"customers\": [\n");
            try (ResultSet rs = stmt.executeQuery(
                "SELECT DISTINCT CUSTOMER_ID, CUSTOMER_CODE, CUSTOMER_NAME, CITY, STATE, GSTIN " +
                "FROM ACTL.CUSTOMER_MASTER WHERE CUSTOMER_NAME IS NOT NULL AND ROWNUM <= 3000 ORDER BY CUSTOMER_NAME")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) mastersJson.append(",\n");
                    first = false;
                    mastersJson.append(String.format(
                        "    {\"id\": %d, \"name\": \"%s\", \"code\": \"%s\", \"city\": \"%s\", \"state\": \"%s\", \"gstin\": \"%s\"}",
                        rs.getInt("CUSTOMER_ID"),
                        escape(rs.getString("CUSTOMER_NAME")),
                        escape(rs.getString("CUSTOMER_CODE")),
                        escape(rs.getString("CITY")),
                        escape(rs.getString("STATE")),
                        escape(rs.getString("GSTIN"))
                    ));
                }
            }
            mastersJson.append("\n  ],\n  \"services\": [\n");
            try (ResultSet rs = stmt.executeQuery(
                "SELECT SERVICE_ID, SERVICE_CODE, SERVICE_NAME, SERVICE_TYPE_CODE FROM ACTL.SERVICE_MASTER WHERE ROWNUM <= 200 ORDER BY SERVICE_NAME")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) mastersJson.append(",\n");
                    first = false;
                    mastersJson.append(String.format(
                        "    {\"id\": %d, \"code\": \"%s\", \"name\": \"%s\", \"type\": \"%s\"}",
                        rs.getInt("SERVICE_ID"),
                        escape(rs.getString("SERVICE_CODE")),
                        escape(rs.getString("SERVICE_NAME")),
                        escape(rs.getString("SERVICE_TYPE_CODE"))
                    ));
                }
            }
            mastersJson.append("\n  ]\n}\n");

            try (FileWriter fw = new FileWriter("/Users/iamrps/Desktop/actl/backend/src/data/masters.json")) {
                fw.write(mastersJson.toString());
            }
            System.out.println("masters.json written successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
