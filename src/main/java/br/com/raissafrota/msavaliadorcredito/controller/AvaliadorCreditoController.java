package br.com.raissafrota.msavaliadorcredito.controller;

import br.com.raissafrota.msavaliadorcredito.entity.SituacaoCliente;
import br.com.raissafrota.msavaliadorcredito.exception.DadosClienteNotFoundException;
import br.com.raissafrota.msavaliadorcredito.exception.ErroComunicacaoMicroservicesException;
import br.com.raissafrota.msavaliadorcredito.service.AvaliadorCreditoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/avaliacoes-credito")
@RequiredArgsConstructor
public class AvaliadorCreditoController {

    private final AvaliadorCreditoService avaliadorCreditoService;

    @GetMapping
    public String status(){
        return "Aplicação subiu! Está tudo ok!";
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity consultarSituacaoCliente(@RequestParam("cpf") String cpf){
        try {
        SituacaoCliente situacaoCliente = avaliadorCreditoService.obterSituacaoCliente(cpf);
        return ResponseEntity.ok(situacaoCliente);
    } catch (DadosClienteNotFoundException e) {
        return ResponseEntity.notFound().build();
    } catch (ErroComunicacaoMicroservicesException e) {
        return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
        }
    }
}
