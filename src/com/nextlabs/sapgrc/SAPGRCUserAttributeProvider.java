/**
 * 
 */
package com.nextlabs.sapgrc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.expressions.EvalValue;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.pf.domain.destiny.serviceprovider.ISubjectAttributeProvider;
import com.bluejungle.pf.domain.destiny.serviceprovider.ServiceProviderException;
import com.bluejungle.pf.domain.destiny.subject.IDSubject;
import com.nextlabs.sapgrc.helper.GRCConstants;
import com.nextlabs.sapgrc.helper.PropertyLoader;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * @author tduong
 * 
 */
public class SAPGRCUserAttributeProvider implements ISubjectAttributeProvider {

	private static final Log LOG = LogFactory
			.getLog(SAPGRCUserAttributeProvider.class);
	private static Properties allProps;
	private static String DEST = "DEST";
	private static String SERV = "SERV";

	/*
	 * 
	 * global default values which has to be assigned during the initiation of
	 * 
	 * the service
	 */
	public static ThreadLocal<HashMap<String, Object>> userObject = new ThreadLocal<HashMap<String, Object>>() {
		@Override
		protected HashMap<String, Object> initialValue() {
			return null;
		}
	};

	public void init() throws Exception {

		LOG.info("SAPGRCPlugin init() started.");
		allProps = PropertyLoader
				.loadProperties(GRCConstants.USERSERVICE_UA_PROPERTIES);
		if (allProps == null) {
			LOG.error("SAPGRCPlugin init() Cannot load properties file at "
					+ GRCConstants.USERSERVICE_UA_PROPERTIES
					+ ". The plugin will not work as expected");
		}
		LOG.info("SAPGRCPlugin :: Initializing cache...");
		if (!CacheManager.getInstance().cacheExists("GRCCache")) {
			CacheManager.getInstance().addCache("GRCCache");
		}
		LOG.info("SAPGRCPlugin init() completed.");

	}

	@Override
	public IEvalValue getAttribute(IDSubject arg0, String arg1)
			throws ServiceProviderException {
		long lCurrentTime = System.nanoTime();

		if (arg0 == null || arg1 == null) {
			LOG.error("SAPGRCPlugin getAttribute(): null parameters received");
			return null;
		}

		String id = arg0.getUid();
		if (LOG.isDebugEnabled()) {
			LOG.info("SAPGRCPlugin getAttribute() : userid:" + id);
			LOG.info("SAPGRCPlugin getAttribute() : attribute:" + arg1);
		}

		IEvalValue value = IEvalValue.NULL;

		if (arg1.equalsIgnoreCase("riskflag")) {
			String userId = arg0.getUid();
			IEvalValue itcode = arg0.getAttribute("tcode");
			if (itcode == null) {
				LOG.error("SAPGRCPlugin getAttribute(): Cannot get tcode");
				return value;
			}

			String transactionCode = (String) itcode.getValue();

			IEvalValue ikey = arg0.getAttribute("grckey");
			if (ikey == null) {
				LOG.error("SAPGRCPlugingetAttribute() : Cannot get grckey");
				return value;
			}
			String uniqueKey = (String) ikey.getValue();

			LOG.info("SAPGRCPlugin getAttribute() :: Arguments: " + userId
					+ " - " + transactionCode + " - " + uniqueKey);

			if (userId == null || transactionCode == null || uniqueKey == null) {
				LOG.info("SAPGRCPlugin getAttribute(): Cannot get all arguments.");
			} else {
				value = getRiskFlag(userId, transactionCode, uniqueKey);
			}

		}

		LOG.debug("SAPGRCPlugin getAttribute() completed. " + arg1 + ": "
				+ value.getValue() + " Time spent: "
				+ ((System.nanoTime() - lCurrentTime) / 1000000.00) + "ms");
		return value;
	}

	/**
	 * Get risk flag from GRC system
	 * 
	 * @param userId User id
	 * 
	 * @param transactionCode transaction code
	 * 
	 * @return IEValValue containing risk flag value
	 */
	private IEvalValue getRiskFlag(String userId, String transactionCode,
			String uniqueKey) {
		IEvalValue result = IEvalValue.NULL;
		try {

			String serverString = allProps.getProperty("server_prefix");

			if (serverString == null) {
				LOG.error("SAPGRCPlugin getRiskFlag() : Cannot get server_prefix from properties file");
				return result;
			}

			LOG.info("SAPGRCPlugin getRiskFlag() : server_prefix = "
					+ serverString);

			String[] serv_prefixes = serverString.split(";");

			if (serv_prefixes.length == 0) {
				LOG.error("SAPGRCPlugin getRiskFlag() : Invalid server_prefix");
				return result;
			}

			return EvalValue.build(callSAPFunction(serv_prefixes[0], userId,
					transactionCode, uniqueKey));

		} catch (Exception e) {
			LOG.info("SAPGRCPlugin getRiskFlag() :: Exception: "
					+ e.getMessage());
		}
		return result;
	}

	/**
	 * Return risk flag and put risk mitigation into cache for SAPJavaSDK's use
	 * 
	 * @param serverName GRC server name
	 * 
	 * @param userId user id
	 * 
	 * @param transactionCode transaction code
	 * 
	 * @param uniqueKey Cache unique key
	 * 
	 * @return A string with risk flag value
	 */
	private String callSAPFunction(String serverName, String userId,
			String transactionCode, String uniqueKey) {
		String flag = "";
		try {
			LOG.info("SAPGRCPlugin :: callSAPFucntion started.");
			JCoDestination destination = JCoDestinationManager
					.getDestination(serverName);
			LOG.info("SAPGRCPlugin callSAPFunction() :: Finished getting Destination Provider \""
					+ serverName + "\".");
			JCoFunction function = destination.getRepository().getFunction(
					allProps.getProperty("query_risk_handler"));
			if (function == null)
				throw new RuntimeException(
						allProps.getProperty("query_risk_handler")
								+ " not found in SAP.");

			// setting input parameters
			function.getImportParameterList().setValue(
					GRCConstants.IV_USER_ID_KEY, userId);
			function.getImportParameterList().setValue(
					GRCConstants.IV_T_CODE_KEY, transactionCode.toCharArray());

			// calling SAP function
			function.execute(destination);

			// retrieving responses
			JCoParameterList results = function.getExportParameterList();
			flag = new String("" + results.getChar(GRCConstants.EV_RISK_KEY));

			JCoTable table = function.getTableParameterList().getTable(
					GRCConstants.TABLE_NAME);
			for (int i = 0; i < table.getNumRows(); i++) {
				table.setRow(i);
			}

			ArrayList<HashMap<String, String>> obligationDetails = new ArrayList<HashMap<String, String>>();

			table.firstRow();
			for (int i = 0; i < table.getNumRows(); i++, table.nextRow()) {

				HashMap<String, String> obligationRow = new HashMap<String, String>();
				for (String column : GRCConstants.MITIGATION_COLUMNS) {
					String value = table.getString(column);
					obligationRow.put(column, value);
					LOG.info("SAPGRCPlugin callSAPFunction() :: Mitigation "
							+ i + ":" + column + ":" + value);
				}
				obligationDetails.add(obligationRow);
			}

			// putting responses into cache

			LOG.info("SAPGRCPlugin callSAPFunction() :: Putting obligation details into cache...");
			Cache grcCache = CacheManager.getInstance().getCache("GRCCache");
			Element element = new Element(uniqueKey, obligationDetails);
			grcCache.put(element);

			LOG.info("SAPGRCPlugin :: callSAPFucntion finished.");
		} catch (Exception e) {
			LOG.info("SAPGRCPlugin callSAPFunction() :: Exception: "
					+ e.getMessage());
		}
		return flag;
	}
}
