package com.ptdam.mobile.redis;

import org.apache.log4j.Logger;

import org.apache.commons.codec.binary.Base64;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author Felix Pikatan Setyoaji
 *
 */
public class JedisFactory {
	private static Logger logger = Logger.getLogger(JedisFactory.class);

	private static String host;
	private static int port;
	private static int connectionTimeout;
	private static int poolSize;
	private static int schemaUsed;
	private static String password;

	public static JedisPool JedisConfig() {
		JedisPool jedisPool = null;
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(getPoolSize());

		// get database schema used
		int schemaUsed = getSchemaUsed();

		String jedisPwd = getPassword();
		if (jedisPwd.trim().length() > 0) {
			System.out.println("Initiating Redis Using Password");
			jedisPool = new JedisPool(poolConfig, getHost(), getPort(), getConnectionTimeout(), decrypt(getPassword()),
					schemaUsed);

		} else {
			jedisPool = new JedisPool(poolConfig, getHost(), getPort(), getConnectionTimeout(), null, schemaUsed);
		}
		return jedisPool;
	}

	public static String decrypt(String encrypted) {
		String decoded = "";

		try {
			byte[] password = encrypted.getBytes();
			decoded = new String(Base64.decodeBase64(password));
		} catch (Exception e) {

		}

		return decoded;
	}

	public static String get(String key) throws Exception {
		String result = "";
		JedisPool jedis;
		jedis = JedisConfig();

		try {

			try {
				byte[] t = key.getBytes();
				key = new String(Base64.encodeBase64(t));
				result = jedis.getResource().get(key);
			} catch (Exception e) {
				logger.info("Failed Safe Encoder : " + e);
			}
		} catch (Exception e) {
			logger.info(e);
			logger.info("Jedis is null");
		}

		finally {
			jedis.destroy();
		}

		String res = null;
		String decode = null;
		if (result != null) {
			res = result.toString();
			decode = decrypt(res);
		}
		return decode;
	}

	public static String setex(String key, int TTL, String value) throws Exception {
		String result = null;
		String key1 = "";
		String value1 = "";
		JedisPool jedis = JedisConfig();

		try {
			byte[] keyAsBytes = key.getBytes();
			key1 = new String(Base64.encodeBase64(keyAsBytes));
			byte[] valueAsBytes = value.getBytes();
			value1 = new String(Base64.encodeBase64(valueAsBytes));
			result = jedis.getResource().setex(key1, TTL, value1);
		} catch (Exception e) {
			logger.info("Failed Safe Encoder : " + e);
		}

		jedis.destroy();

		return result;
	}

	public static String set(String key, String value) throws Exception {
		String result = "";
		JedisPool jedis = JedisConfig();

		byte[] keyAsBytes = key.getBytes();
		String key1 = new String(Base64.encodeBase64(keyAsBytes));
		byte[] valueAsBytes = value.getBytes();
		String value1 = new String(Base64.encodeBase64(valueAsBytes));
		result = jedis.getResource().set(key1, value1);

		jedis.destroy();
		return result;
	}

	public static String hset(String key, String field, String value) throws Exception {
		long result = (long) -1;
		JedisPool jedis = JedisConfig();

		byte[] keyAsBytes = key.getBytes();
		String key1 = new String(Base64.encodeBase64(keyAsBytes));
		byte[] fieldAsBytes = field.getBytes();
		String field1 = new String(Base64.encodeBase64(fieldAsBytes));
		byte[] valueAsBytes = value.getBytes();
		String value1 = new String(Base64.encodeBase64(valueAsBytes));
		result = jedis.getResource().hset(key1, field1, value1);
		String res = Long.toString(result);

		jedis.destroy();
		return res;
	}

	public static String hget(String key, String field) throws Exception {
		String result = "";
		String decode = "";
		JedisPool jedis = JedisConfig();

		byte[] keyAsBytes = key.getBytes();
		String key1 = new String(Base64.encodeBase64(keyAsBytes));
		byte[] fieldAsbytes = field.getBytes();
		String field1 = new String(Base64.encodeBase64(fieldAsbytes));

		result = jedis.getResource().hget(key1, field1);
		decode = decrypt(result);

		jedis.destroy();
		return decode;
	}

	public static String hdel(String key, String field) throws Exception {
		String status = "";
		JedisPool jedis = JedisConfig();
		try {
			byte[] keyAsBytes = key.getBytes();
			String key1 = new String(Base64.encodeBase64(keyAsBytes));
			byte[] fieldAsbytes = field.getBytes();
			String field1 = new String(Base64.encodeBase64(fieldAsbytes));

			jedis.getResource().hdel(key1, field1);

			status = "SUCCESS";
		} catch (Exception e) {
			status = "FAILED";
			logger.info("Failed Safe Encoder : " + e);
		}

		jedis.destroy();
		return status;
	}

	public static String delete(String key) throws Exception {
		String status = "";
		JedisPool jedis = JedisConfig();

		try {
			byte[] keyAsBytes = key.getBytes();
			keyAsBytes = Base64.encodeBase64(keyAsBytes);

			jedis.getResource().del(keyAsBytes);

			status = "SUCCESS";
		} catch (Exception e) {
			status = "FAILED";
			logger.info("Failed Safe Encoder : " + e);
		}
		jedis.destroy();

		return status;
	}

	public static String expire(String key, int timeout) throws Exception {
		Long set_timeout = (long) 0;
		JedisPool jedis = JedisConfig();

		try {
			byte[] keyAsBytes = key.getBytes();
			String key1 = new String(Base64.encodeBase64(keyAsBytes));
			set_timeout = jedis.getResource().expire(key1, timeout);
		} catch (Exception e) {
			logger.info("Failed Safe Encoder : " + e);
		}

		jedis.destroy();

		Long result = set_timeout;
		return result.toString();
	}

	public static String ping() throws Exception {
		String result = null;
		JedisPool jedis = JedisConfig();

		try {
			result = jedis.getResource().ping();

		} catch (Exception e) {
			logger.info("Failed to Ping !" + e);
		}

		jedis.destroy();
		return result;
	}

	public static String Destroy(String key) throws Exception {
		String result = null;
		JedisPool jedis = JedisConfig();

		jedis.destroy();

		return result;
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		JedisFactory.host = host;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		JedisFactory.port = port;
	}

	public static int getConnectionTimeout() {
		return connectionTimeout;
	}

	public static void setConnectionTimeout(int connectionTimeout) {
		JedisFactory.connectionTimeout = connectionTimeout;
	}

	public static int getPoolSize() {
		return poolSize;
	}

	public static void setPoolSize(int poolSize) {
		JedisFactory.poolSize = poolSize;
	}

	public static int getSchemaUsed() {
		return schemaUsed;
	}

	public static void setSchemaUsed(int schemaUsed) {
		JedisFactory.schemaUsed = schemaUsed;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		JedisFactory.password = password;
	}

}
