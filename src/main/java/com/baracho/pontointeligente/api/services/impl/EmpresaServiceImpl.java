package com.baracho.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baracho.pontointeligente.api.entities.Empresa;
import com.baracho.pontointeligente.api.repositories.EmpresaRepository;
import com.baracho.pontointeligente.api.services.EmpresaServices;

@Service
public class EmpresaServiceImpl implements EmpresaServices{
	
	private static final Logger log = LoggerFactory.getLogger(EmpresaServiceImpl.class);
	
	@Autowired
	private EmpresaRepository empresaRepository;

	@Override
	public Optional<Empresa> buscarPorCnpj(String cnpj) {
		log.info("Buscando uma empresa para o CNPJ {}", cnpj);
		return Optional.ofNullable(empresaRepository.findByCnpj(cnpj));
	}

	@Override
	public Empresa persistir(Empresa empresa) {
		log.info("Persintindo empresa: {}", empresa);
		return this.empresaRepository.save(empresa);
	}

}
