package com.baracho.pontointeligente.api.services;

import java.util.Optional;

import com.baracho.pontointeligente.api.entities.Funcionario;

public interface FuncionarioService {
	
	/**
	 * Persiste um funcionario na base de dados  
	 * 
	 * @param funcionario
	 * @return Funcionario
	 */
	Funcionario persistir(Funcionario funcionario);
	
	/**
	 * Busca e retorna um funcionario dado um CPF.
	 * @param cpf
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> buscarPorCpf(String cpf);
	
	/**
	 * Busca e retorna um funcionario dado um email.
	 * 
	 * @param email
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> buscarEmail(String email);
	
	/**
	 * Busca e retorna um funcionario dado um ID.
	 * 
	 * @param id
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> buscarPorId(Long id);

}
