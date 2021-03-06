package rancher;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import rancher.common.Constant;
import rancher.common.Util;
import rancher.models.DataJsonModel;
import rancher.models.ResponseJsonModel;
import rancher.models.URIBuilder;

public class RancherAPI {

	private static final Logger LOGGER = Logger.getLogger(RancherAPI.class);

	public JsonNode getCurrentConfiguration(String serviceWithIdURL, String authToken) {
		String result = Util.connectToWebService(serviceWithIdURL, Constant.METHOD_GET,null, authToken);
		JsonNode node = null;
		try {
			node = new ObjectMapper().readTree(result);
		} catch (IOException e) {
			LOGGER.error("IOException: ", e);
		}
		return node.path("launchConfig");
	}
	
	public String findServiceIdByName(String rancherHost, int rancherPort, String projectId, String stackId,
			String serviceName, String authToken) {
		String requestURL = new URIBuilder(rancherHost, rancherPort, projectId, stackId, null, null).build().toString();
		ResponseJsonModel response = getDataFromAPI(requestURL, authToken);
		if (response == null) {
			try {
				throw new IOException("Services information is not available");
			} catch (IOException e) {
				LOGGER.error("IOException: ", e);
			}
		}

		List<DataJsonModel> services = response.getData();
		String serviceId = null;
		
		if (services.isEmpty()) {
			LOGGER.debug("No services in stack");
			return Constant.SERVICE_ID_NOT_EXISTED;
		}

		for (DataJsonModel service : response.getData()) {
			if (service.getName().equals(serviceName)) {
				LOGGER.debug("Found service id = " + service.getId() + " for " + serviceName);
				serviceId = service.getId();
				break;
			}
		}

		if (StringUtils.isEmpty(serviceId)) {
			LOGGER.debug("No such service with name: " + serviceName);
			return Constant.SERVICE_ID_NOT_EXISTED;
		}

		return serviceId;
	}

	public String findStackIdByName(String rancherHost, int rancherPort, String projectId, String stackName,
			String authToken) {
		String requestURL = new URIBuilder(rancherHost, rancherPort, projectId, Constant.QueryParam.NAME + stackName)
				.build().toString();
		ResponseJsonModel response = getDataFromAPI(requestURL, authToken);
		if (response == null || response.getData().isEmpty()) {
			try {
				throw new IOException("No such stack: " + stackName);
			} catch (IOException e) {
				LOGGER.error("IOException: ", e);
			}
		}
		DataJsonModel stack = response.getData().iterator().next();
		LOGGER.debug("Found stack id = " + stack.getId() + " for " + stackName);
		return stack.getId();
	}

	public String findProjectIdByName(String rancherHost, int rancherPort, String projectName, String authToken) {
		String requestURL = new URIBuilder(rancherHost, rancherPort,
				Constant.QueryParam.ALL + "true" + "&" + Constant.QueryParam.NAME + projectName).build().toString();
		ResponseJsonModel reponse = getDataFromAPI(requestURL, authToken);
		if (reponse == null || reponse.getData().isEmpty()) {
			try {
				throw new IOException("No such project: " + projectName);
			} catch (IOException e) {
				LOGGER.error("IOException: ", e);
			}
		}
		DataJsonModel project = reponse.getData().iterator().next();
		LOGGER.debug("Found project id = " + project.getId() + " for " + projectName);
		return project.getId();
	}

	private ResponseJsonModel getDataFromAPI(String requestURL, String authToken) {
		String jsonString = Util.connectToWebService(requestURL, Constant.METHOD_GET, null, authToken);
		if (StringUtils.isEmpty(jsonString)) {
			LOGGER.error("Failed to GET data from Rancher. URL = " + requestURL);
			return null;
		}
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			JsonNode node = mapper.readTree(jsonString);
			return mapper.treeToValue(node, ResponseJsonModel.class);
		} catch (IOException e) {
			LOGGER.error("IOException: " + e);
		}
		return null;
	}

}
