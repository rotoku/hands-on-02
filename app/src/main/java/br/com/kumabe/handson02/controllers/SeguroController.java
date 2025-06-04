package br.com.kumabe.handson02.controllers;

import br.com.kumabe.handson02.exceptions.ParametroInvalidoException;
import br.com.kumabe.handson02.services.CotacaoSeguroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/seguros")
public class SeguroController {

    private final CotacaoSeguroService cotacaoSeguroService;

    public SeguroController(CotacaoSeguroService cotacaoSeguroService) {
        this.cotacaoSeguroService = cotacaoSeguroService;
    }

    @GetMapping("/cotacao")
    public ResponseEntity<String> obterCotacao(
            @RequestParam("tipoSeguro") String tipoSeguro,
            @RequestParam("valorBem") double valorBem) {
        try {
            double premio = cotacaoSeguroService.calcularCotacao(tipoSeguro, valorBem);
            return ResponseEntity.ok("Cotação para seguro " + tipoSeguro + ": Prêmio Anual = R$ " + String.format("%.2f", premio));
        } catch (ParametroInvalidoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            // Captura outras exceções que podem ser lançadas se o fallback também falhar ou se for uma exceção não tratada pelo Resilience4j
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar a cotação: " + e.getMessage());
        }
    }

    @GetMapping("/servico-indisponivel-teste")
    public ResponseEntity<String> testeServicoIndisponivel() {
        try {
            String resultado = cotacaoSeguroService.servicoSempreIndisponivel();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Erro: " + e.getMessage());
        }
    }
}
