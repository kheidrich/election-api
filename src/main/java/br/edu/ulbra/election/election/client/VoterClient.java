package br.edu.ulbra.election.election.client;

import br.edu.ulbra.election.election.input.v1.TokenValidationInput;
import br.edu.ulbra.election.election.output.v1.TokenValidationOutput;
import br.edu.ulbra.election.election.output.v1.VoterOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "voter-service", url = "${url.voter-service}")
public interface VoterClient {

    @GetMapping("/v1/voter/{voterId}")
    VoterOutput getById(@PathVariable(name = "voterId") Long voterId);

    @PostMapping("/v1/auth/token/validate")
    TokenValidationOutput validateToken(@RequestBody TokenValidationInput tokenValidationInput);
}
