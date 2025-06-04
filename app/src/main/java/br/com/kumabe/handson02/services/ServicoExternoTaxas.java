package br.com.kumabe.handson02.services;

import br.com.kumabe.handson02.exceptions.ServicoExternoIndisponivelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ServicoExternoTaxas {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicoExternoTaxas.class);
    private final Random random = new Random();
    private final AtomicInteger contadorChamadas = new AtomicInteger(0);

    // Simula a obtenção de uma taxa de prêmio de um serviço externo
    public double obterTaxaPremio(String tipoSeguro) {
        int chamadaAtual = contadorChamadas.incrementAndGet();
        LOGGER.info("Tentativa {} de chamar o serviço externo de taxas para o seguro: {}", chamadaAtual, tipoSeguro);

        // Simula falhas intermitentes
        if (random.nextInt(10) < 6) { // 60% de chance de falha
            LOGGER.warn("Serviço externo de taxas indisponível na tentativa {}.", chamadaAtual);
            throw new ServicoExternoIndisponivelException("Falha ao comunicar com o serviço de taxas externo.");
        }

        LOGGER.info("Serviço externo de taxas respondeu com sucesso na tentativa {}.", chamadaAtual);
        // Simula uma lógica de cálculo de taxa
        return switch (tipoSeguro.toLowerCase()) {
            case "auto" -> 0.05;
            case "vida" -> 0.02;
            case "residencial" -> 0.03;
            default -> 0.1;
        };
    }
}
