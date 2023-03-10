package com.baracho.pontointeligente.api.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baracho.pontointeligente.api.dtos.LancamentoDto;
import com.baracho.pontointeligente.api.entities.Funcionario;
import com.baracho.pontointeligente.api.entities.Lancamento;
import com.baracho.pontointeligente.api.enums.TipoEnum;
import com.baracho.pontointeligente.api.response.Response;
import com.baracho.pontointeligente.api.services.FuncionarioService;
import com.baracho.pontointeligente.api.services.LancamentoService;


@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {
	
	private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;
	
	public LancamentoController() {}
	
	/**
	 * Retorna a listagem de lancamentos de um funcionario  
	 * 
	 * @param funcionarioId
	 * @param pag
	 * @param ord
	 * @param dir
	 * @return ResponseEntity<Response<LancamentoDto>>
	 */
	@GetMapping(value = "/funcionario/{funcionarioId}")  
	public ResponseEntity<Response<Page<LancamentoDto>>> listarPorFuncionarioId(
			@PathVariable("funcionarioId") Long funcionarioId,
			@RequestParam(value = "pag", defaultValue = "0") int pag,
			@RequestParam(value = "ord", defaultValue = "id") String ord,
			@RequestParam(value = "dir", defaultValue = "DESC") String dir){
		
		log.info("buscando lancamentos por ID do funcionario: {}, pagina: {}", funcionarioId, pag);
		Response<Page<LancamentoDto>> response = new Response<Page<LancamentoDto>>();
		
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<Lancamento> lancamentos = this.lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest);
		Page<LancamentoDto> lancamentosDto = lancamentos.map(lancamento -> this.converterLancamentoParaLancamentoDto(lancamento));
		response.setData(lancamentosDto);
		
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Retorna um lan??amento por ID.
	 * @param id
	 * @return ResponseEntity<Response<LancamentoDto>>
	 */
	@GetMapping(value = "/{id}")  
	public ResponseEntity<Response<LancamentoDto>> listarPorId(@PathVariable("id") Long id){
		log.info("Buscando lan??amento por Id: {}", id);
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);
		
		if(!lancamento.isPresent()) {
			log.info("Lan??amento n??o encontrado para o Id: {}", id);
			response.getErrors().add("Lan??amento n??o encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
			
		}
		
		response.setData(this.converterLancamentoParaLancamentoDto(lancamento.get()));
		return ResponseEntity.ok(response);
	}
	/**
	 * Adiciona um novo lan??amento
	 * @param lancamentoDto
	 * @param result
	 * @return ResponseEntity<Response<LancamentoDto>>
	 * @throws ParseException
	 */
	@PostMapping
	public ResponseEntity<Response<LancamentoDto>> adicionar(@Valid @RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
		
		log.info("Adicionando lan??amento: {}", lancamentoDto.toString());
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		validarFuncionario(lancamentoDto, result);
		Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto, result);
		if(result.hasErrors()) {
			log.error("Erro validando lan??amentos: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		lancamento = this.lancamentoService.persistir(lancamento);
		response.setData(this.converterLancamentoParaLancamentoDto(lancamento));
		return ResponseEntity.ok(response);
		
	}
	
	/**
	 * Atualiza os dados de um lancamento.
	 * @param id
	 * @param lancamentoDto
	 * @param result
	 * @return ResponseEntity<Response<Lancamento>>
	 * @throws ParseException
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<LancamentoDto>> atualizar(@PathVariable("id") Long id, @Valid @RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
		
		log.info("Atualizando lan??amento: {}", lancamentoDto.toString());
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		validarFuncionario(lancamentoDto, result);
		lancamentoDto.setId(Optional.of(id));
		Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto, result);
		
		if(result.hasErrors()) {
			log.error("Erro validando lan??amento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		lancamento = this.lancamentoService.persistir(lancamento);
		response.setData(this.converterLancamentoParaLancamentoDto(lancamento));
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping(value = "/{id}") 
	public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id){
		log.info("Removendo lan??amento: {}", id); 
		Response<String> response = new Response<String>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);
		
		if(!lancamento.isPresent()){
			log.info("Erro ao remover devudo ao lan??amento Id: {} ser inv??lido", id);
			response.getErrors().add("Erro ao remover lancamento. Registro n??o encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
		}
		
		this.lancamentoService.remover(id);
		
		
		return ResponseEntity.ok(new Response<String>());
	}
	
	
	/**
	 * Converte uma lancamentoDto para uma entidade Lancamento
	 * @param lancamentoDto
	 * @param result
	 * @return Lancamento
	 * @throws ParseException
	 */
	private Lancamento converterDtoParaLancamento(LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
		Lancamento lancamento = new Lancamento();
		
		if(lancamentoDto.getId().isPresent()) {
			Optional<Lancamento> lanc = this.lancamentoService.buscarPorId(lancamentoDto.getId().get());
			if(lanc.isPresent()) {
				lancamento = lanc.get();
			}else {
				result.addError(new ObjectError("lancamento", "Lancamento n??o encontrado"));
			}
		}else {
			lancamento.setFuncionario(new Funcionario());
			lancamento.getFuncionario().setId(lancamentoDto.getFuncionarioId());
		}
		
		lancamento.setDescricao(lancamentoDto.getDescricao());
		lancamento.setLocalizacao(lancamentoDto.getLocalizacao());
		lancamento.setData(this.dateFormat.parse(lancamentoDto.getData()));
		
		if(EnumUtils.isValidEnum(TipoEnum.class, lancamentoDto.getTipo())) {
			lancamento.setTipo(TipoEnum.valueOf(lancamentoDto.getTipo()));
		}else {
			result.addError(new ObjectError("tipo", "Tipo inv??lido"));
		}
		
		return lancamento;
	}
	
	/**
	 * Valida um funcionario, verificando se ele ?? existente e valido no sistema.
	 * @param lancamentoDto
	 * @param result
	 */
	private void validarFuncionario(LancamentoDto lancamentoDto, BindingResult result) {
		if(lancamentoDto.getFuncionarioId() == null) {
			result.addError(new ObjectError("funcionario", "funcionario n??o informado"));
			return;
		}
		
		log.info("Validando funcion??rio id {}", lancamentoDto.getFuncionarioId()); 
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(lancamentoDto.getFuncionarioId()); 
		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionario n??o encontrado. ID inexistente"));
			
		}
	}
	/**
	 * Converte uma entidade lancamento para seu respectivo DTO.
	 * @param lancamento
	 * @return LancamentoDto
	 */
	private LancamentoDto converterLancamentoParaLancamentoDto(Lancamento lancamento) {
		LancamentoDto lancamentoDto = new LancamentoDto();
		lancamentoDto.setId(Optional.of(lancamento.getId()));
		lancamentoDto.setData(this.dateFormat.format(lancamento.getData()));
		lancamentoDto.setTipo(lancamento.getTipo().toString());
		lancamentoDto.setDescricao(lancamento.getDescricao()); 
		lancamentoDto.setLocalizacao(lancamento.getLocalizacao());
		lancamentoDto.setFuncionarioId(lancamento.getFuncionario().getId()); 
		
		
		return lancamentoDto;
	}
			
	

}
