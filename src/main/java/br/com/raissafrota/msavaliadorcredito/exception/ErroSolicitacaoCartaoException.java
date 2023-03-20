package br.com.raissafrota.msavaliadorcredito.exception;

public class ErroSolicitacaoCartaoException extends RuntimeException{
    public ErroSolicitacaoCartaoException(String message) {
        super(message);
    }
}