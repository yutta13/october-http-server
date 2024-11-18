package ru.otus.moneysrv.http.server.db;

public class Db_queries {
    public static String getCardnoQuery() {
        return CARDNO_QUERY;
    }

    public static String getCardPresentQuery(String card_no) {
        return CARDPRESENT_CHECK;
    }

    public static String getCardHolderQuery() {
        return CARDHOLDER_QUERY;
    }

    public static String getAcctQuery() {
        return ACCT_QUERY;
    }

    public static String getBalanceforcardQuery(String card_no) {
        return BALANCEFORCARD_QUERY;
    }

    public static String getCWDforcardQuery(int transamount, String card_no) {
        return CASWITHDRAWAL_QUERY;
    }

    public static String getAddfundsQuery(int transamount, String card_no) {
        return ADDFUNDS_QUERY;
    }

    public static String getAddCustomerQuery( String name, String surname) {
        return ADDCUSTOMER_QUERY;
    }
    public static String getAddcrefQuery( String card_no, int exp_dt, int cardholder_id, int p_offset) {
        return ADDCREF_QUERY;
    }
    public static String getAddacctrubQuery(String card_no, String acct_num) {
        return ADDACCTRUB_QUERY;
    }
    public static String getCheckpinQuery(String card_no) {
        return CHECKPIN;
    }

    private static final String CARDNO_QUERY = "SELECT * from cref_tab;";
    private static final String ACCT_QUERY = "SELECT * from acct_tab;";
    private static final String CARDHOLDER_QUERY = "SELECT * from cardholder_tab;";
    private static final String BALANCEFORCARD_QUERY = "SELECT abal as \"balance\" \n" +
            "FROM acct_tab act \n" +
            "INNER JOIN public.card_acct_tab ON act.acct_num = card_acct_tab.acct_num \n" +
            "INNER JOIN public.cref_tab ON card_acct_tab.cref_no = cref_tab.cref_no \n" +
            "WHERE cref_tab.cref_no = ? ;";

    private static final String CASWITHDRAWAL_QUERY = "UPDATE acct_tab \n" +
            "SET abal = abal - ? \n" +
            "FROM card_acct_tab \n" +
            "INNER JOIN cref_tab ON card_acct_tab.cref_no = cref_tab.cref_no \n" +
            "WHERE acct_tab.acct_num = card_acct_tab.acct_num \n" +
            "AND cref_tab.cref_no = ? ;";

    private static final String ADDFUNDS_QUERY = "UPDATE acct_tab \n" +
            "SET abal = abal + ? \n" +
            "FROM card_acct_tab \n" +
            "INNER JOIN cref_tab ON card_acct_tab.cref_no = cref_tab.cref_no \n" +
            "WHERE acct_tab.acct_num = card_acct_tab.acct_num \n" +
            "AND cref_tab.cref_no = ? ;";

     private static final String ADDCUSTOMER_QUERY =
        "INSERT INTO public.cardholder_tab(name, surname)\n" +
                "VALUES (?, ?)\n" +
                "RETURNING id ;\n";

    private static final String ADDCREF_QUERY =
                    "INSERT INTO cref_tab (cref_no, exp_dt, cardholder_id, p_offset)\n" +
                    "VALUES (?, ?, ?, ?);\n";

    private static final String ADDACCTRUB_QUERY = "INSERT INTO acct_tab (acct_num, abal, currency) \n" +
            "VALUES (?, 0, 810);\n" +
            "INSERT INTO card_acct_tab (cref_no, acct_num) \n" +
            "VALUES (?, ?); \n";

    private static final String CARDPRESENT_CHECK = "SELECT card_no as \"card_number\" \n" +
            "from cref_tab c\n" +
            "where c.cref_no = ? ;";

    private static final String CHECKPIN = "SELECT p_offset as \"p_offset\" \n" +
            "from cref_tab c\n" +
            "where c.cref_no = ? ;";

}
