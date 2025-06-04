package br.com.kumabe.handson02.services;

import br.com.kumabe.handson02.exceptions.ParametroInvalidoException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CotacaoSeguroService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CotacaoSeguroService.class);
    private final ServicoExternoTaxas servicoExternoTaxas;

    public CotacaoSeguroService(ServicoExternoTaxas servicoExternoTaxas) {
        this.servicoExternoTaxas = servicoExternoTaxas;
    }

    @CircuitBreaker(name = "cotacaoSeguroService", fallbackMethod = "calcularCotacaoFallback")
    @Retry(name = "cotacaoSeguroService") // Pode usar o mesmo nome ou um diferente
    public double calcularCotacao(String tipoSeguro, double valorBem) {
        LOGGER.info("Iniciando cálculo de cotação para seguro '{}' com valor '{}'", tipoSeguro, valorBem);

        if (valorBem <= 0) {
            throw new ParametroInvalidoException("Valor do bem deve ser positivo.");
        }

        // Chama o serviço externo que pode falhar
        double taxa = servicoExternoTaxas.obterTaxaPremio(tipoSeguro);
        double premio = valorBem * taxa;

        LOGGER.info("Cotação calculada com sucesso: Prêmio = {}", premio);
        return premio;
    }

    // Método Fallback para o Circuit Breaker
    @SuppressWarnings("unused") // Usado pelo Resilience4j via reflexão
    public double calcularCotacaoFallback(String tipoSeguro, double valorBem, Throwable t) {
        LOGGER.warn("Fallback acionado para cotação do seguro '{}'. Causa: {}", tipoSeguro, t.getMessage());
        // Lógica de fallback: pode ser um valor padrão, uma estimativa, ou lançar uma exceção específica.
        // Aqui, retornamos um valor fixo como exemplo ou uma estimativa baseada no tipo.
        return switch (tipoSeguro.toLowerCase()) {
            case "auto" -> valorBem * 0.08; // Taxa de fallback mais alta
            case "vida" -> valorBem * 0.03;
            case "residencial" -> valorBem * 0.04;
            default -> valorBem * 0.15; // Uma taxa genérica de fallback
        };
    }

    // Exemplo de método que pode ser usado para simular um serviço sempre indisponível
    @CircuitBreaker(name = "servicoSempreIndisponivel", fallbackMethod = "sempreIndisponivelFallback")
    @Retry(name = "servicoSempreIndisponivel", fallbackMethod = "sempreIndisponivelRetryFallback") // Retry também pode ter fallback
    public String servicoSempreIndisponivel() {
        LOGGER.info("Tentando chamar o serviço que está sempre indisponível...");
        throw new RuntimeException("Simulação de serviço permanentemente indisponível. Chamado em: " + LocalDateTime.now());
    }

    @SuppressWarnings("unused")
    private String sempreIndisponivelFallback(Throwable t) {
        LOGGER.warn("Fallback do Circuit Breaker para 'servicoSempreIndisponivel' acionado: {}", t.getMessage());
        return "Serviço indisponível no momento (Fallback CircuitBreaker). Tente mais tarde.";
    }

    @SuppressWarnings("unused")
    private String sempreIndisponivelRetryFallback(Throwable t) {
        LOGGER.warn("Fallback do Retry para 'servicoSempreIndisponivel' acionado após todas as tentativas: {}", t.getMessage());
        return "Serviço indisponível no momento (Fallback Retry). Todas as tentativas falharam.";
    }
}