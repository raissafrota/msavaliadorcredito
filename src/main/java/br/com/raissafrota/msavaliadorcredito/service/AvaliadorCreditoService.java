package br.com.raissafrota.msavaliadorcredito.service;

import br.com.raissafrota.msavaliadorcredito.entity.Cartao;
import br.com.raissafrota.msavaliadorcredito.entity.CartaoAprovado;
import br.com.raissafrota.msavaliadorcredito.entity.CartaoCliente;
import br.com.raissafrota.msavaliadorcredito.entity.DadosCliente;
import br.com.raissafrota.msavaliadorcredito.entity.DadosSolicitacaoEmissaoCartao;
import br.com.raissafrota.msavaliadorcredito.entity.ProtocoloSolicitacaoCartao;
import br.com.raissafrota.msavaliadorcredito.entity.RetornoAvaliacaoCliente;
import br.com.raissafrota.msavaliadorcredito.entity.SituacaoCliente;
import br.com.raissafrota.msavaliadorcredito.exception.DadosClienteNotFoundException;
import br.com.raissafrota.msavaliadorcredito.exception.ErroComunicacaoMicroservicesException;
import br.com.raissafrota.msavaliadorcredito.exception.ErroSolicitacaoCartaoException;
import br.com.raissafrota.msavaliadorcredito.feignClients.CartoesResourceClient;
import br.com.raissafrota.msavaliadorcredito.feignClients.ClienteResouceClient;
import br.com.raissafrota.msavaliadorcredito.rabbitmq.SolicitacaoEmissaoCartaoPublisher;
import feign.FeignException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResouceClient clienteClient;
    private final CartoesResourceClient cartoesClient;
    private final SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;

    // 1) obterDadosCliente - msclientes
    // 2) obterDadosCartaoDoCliente - mscartoes
    public SituacaoCliente obterSituacaoCliente(String cpf)  throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {
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
            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }
    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda)
            throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException{
        try{
            ResponseEntity<DadosCliente> dadosClienteResponse = clienteClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesClient.getCartoesRendaAteh(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();
            var listaCartoesAprovados = cartoes.stream().map(cartao -> {

                DadosCliente dadosCliente = dadosClienteResponse.getBody();

                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
                var fator = idadeBD.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado aprovado = new CartaoAprovado();
                aprovado.setCartao(cartao.getNome());
                aprovado.setBandeira(cartao.getBandeira());
                aprovado.setLimiteAprovado(limiteAprovado);

                return aprovado;
            }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(listaCartoesAprovados);

        }catch (FeignException.FeignClientException e){
            int status = e.status();
            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados){
        try{
            emissaoCartaoPublisher.solicitarCartao(dados);
            var protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        }catch (Exception e){
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }

}
