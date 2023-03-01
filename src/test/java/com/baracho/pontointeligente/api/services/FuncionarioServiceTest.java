package com.baracho.pontointeligente.api.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.baracho.pontointeligente.api.entities.Funcionario;
import com.baracho.pontointeligente.api.repositories.FuncionarioRepository;

@SpringBootTest
@ActiveProfiles("test")
public class FuncionarioServiceTest {
	
	@MockBean
	private FuncionarioRepository funcionarioRepository;
	
	@Autowired
	FuncionarioService funcionarioService;
	
	@BeforeEach
	public void setup() throws Exception {
		BDDMockito.given(this.funcionarioRepository.save(Mockito.any(Funcionario.class))).willReturn(new Funcionario());
		BDDMockito.given(this.funcionarioRepository.findById(Mockito.anyLong())).willReturn(Optional.of(new Funcionario()));
		BDDMockito.given(this.funcionarioRepository.findByEmail(Mockito.anyString())).willReturn(new Funcionario());
		BDDMockito.given(this.funcionarioRepository.findByCpf(Mockito.anyString())).willReturn(new Funcionario());
	}
	
	@Test
	public void testPersistirFuncionario() {
		Funcionario funcionario = this.funcionarioService.persistir(new Funcionario());
		
		assertNotNull(funcionario);
		
	}
	
	@Test
	public void testeBuscarFuncionarioPorId() {
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(1L);
		
		assertTrue(funcionario.isPresent());
	}
	
	@Test
	public void testeBuscarFuncionarioPorEmail() {
		Optional<Funcionario> funcionario = this.funcionarioService.buscarEmail("email@email.com");
		
		assertTrue(funcionario.isPresent());
		
	}
	
	@Test
	public void testeBuscarFuncionarioPorCpf() {
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorCpf("5145524558");
		
		assertTrue(funcionario.isPresent());
	}

}
