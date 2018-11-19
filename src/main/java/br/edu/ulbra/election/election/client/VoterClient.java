package br.edu.ulbra.election.election.client;

import br.edu.ulbra.election.election.output.v1.VoterOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "voter-service", url = "${url.voter-service}")
public interface VoterClient {

    @GetMapping("/v1/voter/{voterId}")
    VoterOutput getById(@PathVariable(name = "voterId") Long voterId);
}
