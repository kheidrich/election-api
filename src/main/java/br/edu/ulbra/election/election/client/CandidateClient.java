package br.edu.ulbra.election.election.client;

import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "candidate-service", url = "${url.candidate-service}")
public interface CandidateClient {

    @GetMapping("/v1/candidate/{candidateId}")
    CandidateOutput getById(@PathVariable(name = "candidateId") Long candidateId);
}
