package com.nextlabs.sapgrc.helper;

public class GRCConstants {
	public static final String USERSERVICE_UA_PROPERTIES = "/jservice/config/SAPGRCUserAttributePlugin.properties";
	public static final String IV_USER_ID_KEY = "IV_USERID";
	public static final String IV_T_CODE_KEY = "IV_ACTION";
	public static final String TABLE_NAME = "ET_RISKDTLS";

	public static final String EV_RISK_KEY = "EV_RISK";
	// grc mitigation columns
	public static final String[] MITIGATION_COLUMNS = { "RISKID", "RISKDESC",
			"ACCONTROLID", "ACCONTROLDESC", "MITMONITOR", "MITCOMMU" };

}
