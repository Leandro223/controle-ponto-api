package com.baracho.pontointeligente.api.utils;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtilsTest {
	
	private static final String SENHA = "123456";
	
	private final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
	
	@Test
	public void testSenhaNula() throws Exception {
		assertNull(PasswordUtils.gerarBcrypt(null));
	}
	
	@Test
	public void testGerarHasSenha() throws Exception {
		String hash = PasswordUtils.gerarBcrypt(SENHA);
		
		assertTrue(bCryptEncoder.matches(SENHA, hash));
		
	}
	
	
	

}
