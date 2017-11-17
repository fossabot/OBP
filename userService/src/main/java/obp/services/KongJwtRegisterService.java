package obp.services;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import obp.dao.KongConsumerResponse;
import obp.security.SerializableUser;

// TODO: see: https://atlasrv02.saicwebhost.net/display/OBP/Kong+JWT+Issues
@Service
public class KongJwtRegisterService {
	
	@Value("${security.oauth2.resource.jwt.key-value}")
  private String signingKey;
  
//  TODO: un-comment when Kong is updated
//	see: https://atlasrv02.saicwebhost.net/display/OBP/Kong+JWT+Issues
//	private static String KONG_ADMIN_BASE = "http://obp-02.esl.saic.com:8001";
//
//
//	public SerializableUser createKongJwtCredential(SerializableUser user, KongConsumerResponse consumerResponse) throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
//		String url = KONG_ADMIN_BASE + "/consumers/" + consumerResponse.getId() +"/jwt";
//				
//		RestTemplate rt = new RestTemplate();
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Host", "obp-02.esl.saic.com");
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		
//		KongJwtCredentialRequest credentialRequest = new KongJwtCredentialRequest();
//		// these names from kong are very confusing
//		credentialRequest.setSecret(signingKey);
//		HttpEntity<KongJwtCredentialRequest> entity = new HttpEntity<>(credentialRequest, headers);
//		
//		HttpEntity<KongJwtCredentialResponse> response = rt.postForEntity(url, entity, KongJwtCredentialResponse.class);
//		
//		KongJwtCredentialResponse credentialResponse = response.getBody();
//		user.setClient_secret(credentialResponse.getKey());
//		return user;
//	}
//	
//	public KongConsumerResponse createKongConsumer(SerializableUser user) throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
//		String url = KONG_ADMIN_BASE + "/consumers";
//
//		RestTemplate rt = new RestTemplate();
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Host", "obp-02.esl.saic.com");
//		headers.setContentType(MediaType.APPLICATION_JSON);
//
//		KongConsumerRequest consumer = new KongConsumerRequest();
//		consumer.setUsername(user.getUsername());
//		consumer.setCustom_id(user.getId());
//
//		HttpEntity<KongConsumerRequest> entity = new HttpEntity<>(consumer, headers);
//
//		ResponseEntity<KongConsumerResponse> response = rt.postForEntity(url, entity, KongConsumerResponse.class);
//		return response.getBody();
//	}
// TODO: delete below here
	
  private static String iss = "4b2336ee3273448999b8e5579dc43b95";
	
	public SerializableUser createKongJwtCredential(SerializableUser user, KongConsumerResponse consumerResponse) throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
		user.setClient_secret(iss);
		return user;
	}
	
	public KongConsumerResponse createKongConsumer(SerializableUser user) throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
		KongConsumerResponse response = new KongConsumerResponse();
		return response;
	}
}
