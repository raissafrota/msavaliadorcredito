package br.com.raissafrota.msavaliadorcredito.service;

import br.com.raissafrota.msavaliadorcredito.entity.CartaoCliente;
import br.com.raissafrota.msavaliadorcredito.entity.DadosCliente;
import br.com.raissafrota.msavaliadorcredito.entity.SituacaoCliente;
import br.com.raissafrota.msavaliadorcredito.feignClients.CartoesResourceClient;
import br.com.raissafrota.msavaliadorcredito.feignClients.ClienteResouceClient;
import feign.FeignException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResouceClient clienteClient;
    private final CartoesResourceClient cartoesClient;

    // 1) obterDadosCliente - msclientes
    // 2) obterDadosCartaoDoCliente - mscartoes
    public SituacaoCliente obterSituacaoCliente(String cpf){
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clienteClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesClient.getCartoesByCliente(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartoesResponse.getBody())
                    .build();

        }catch (FeignException.FeignClientException e){
            int status = e.status();
            return null;
        }
    }


}
