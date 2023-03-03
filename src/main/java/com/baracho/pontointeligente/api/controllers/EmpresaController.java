package com.baracho.pontointeligente.api.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baracho.pontointeligente.api.dtos.EmpresaDto;
import com.baracho.pontointeligente.api.entities.Empresa;
import com.baracho.pontointeligente.api.response.Response;
import com.baracho.pontointeligente.api.services.EmpresaServices;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {
	
	private static final Logger log = LoggerFactory.getLogger(EmpresaController.class);
	
	@Autowired
	EmpresaServices empresaService;
	
	public EmpresaController() {}
	
	/**
	 * 
	 * Retorna uma empresa dado um cnpj
	 * @return ResponseEntity<Response<EmpresaDto>>
	 */
	
	@GetMapping(value = "/cnpj/{cnpj}")  
	public ResponseEntity<Response<EmpresaDto>> buscarPorCnpj(@PathVariable("cnpj") String cnpj){
		log.info("buscando empresa por cnpj: {}", cnpj);
		Response<EmpresaDto> response = new Response<EmpresaDto>();
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cnpj);
		
		if(!empresa.isPresent()) {
			log.info("Empresa não encontrada para o cnpj: {}", cnpj);
			response.getErrors().add("Empresa não encontrada para o cnpj " + cnpj);
			return ResponseEntity.badRequest().body(response);
		}
		
		response.setData(this.converterEmpresaEmEmpresaDto(empresa.get()));
		return ResponseEntity.ok(response);
		
		
	}
	
	/**
	 * Popula um DTO com os dados de uma empresa.
	 * 
	 * @param empresa
	 * @return EmpresaDto
	 */
	private EmpresaDto converterEmpresaEmEmpresaDto(Empresa empresa) {
		EmpresaDto empresaDto = new EmpresaDto();
		empresaDto.setId(empresa.getId());
		empresaDto.setCnpj(empresa.getCnpj());
		empresaDto.setRazaoSocial(empresa.getRazaoSocial());
		return empresaDto;
		
	}
}
