package org.beansugar.oauth.services.signature;

import org.beansugar.oauth.exceptions.OAuthSignatureException;
import org.beansugar.oauth.services.encoder.EncoderFactory;
import org.beansugar.oauth.utils.OAuthEncoder;
import org.scriptonbasestar.tool.core.check.Check;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * HMAC-SHA1 implementation of {@link SignatureService}
 *
 * @author Pablo Fernandez
 */
public class HMACSha1SignatureService implements SignatureService {
	private static final String EMPTY_STRING = "";
	private static final String CARRIAGE_RETURN = "\r\n";
	private static final String UTF8 = "UTF-8";
	private static final String HMAC_SHA1 = "HmacSHA1";
	private static final String METHOD = "HMAC-SHA1";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSignature(String baseString, String apiSecret, String tokenSecret) {
		try {
			Check.notNullOrEmptyString(baseString, "Base string cant be null or empty string");
			Check.notNullOrEmptyString(apiSecret, "Api secret cant be null or empty string");
			return doSign(baseString, OAuthEncoder.encode(apiSecret) + '&' + OAuthEncoder.encode(tokenSecret));
		} catch (Exception e) {
			throw new OAuthSignatureException(baseString, e);
		}
	}

	private String doSign(String toSign, String keyString) throws Exception {
		SecretKeySpec key = new SecretKeySpec((keyString).getBytes(UTF8), HMAC_SHA1);
		Mac mac = Mac.getInstance(HMAC_SHA1);
		mac.init(key);
		byte[] bytes = mac.doFinal(toSign.getBytes(UTF8));
		return bytesToBase64String(bytes).replace(CARRIAGE_RETURN, EMPTY_STRING);
	}

	private String bytesToBase64String(byte[] bytes) {
		return EncoderFactory.getInstance().getEncoder().encode(bytes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSignatureMethod() {
		return METHOD;
	}
}
