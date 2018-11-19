package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.client.CandidateClient;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateClientService {
    private final CandidateClient candidateClient;

    @Autowired
    public CandidateClientService(CandidateClient candidateClient) {
        this.candidateClient = candidateClient;
    }

    public CandidateOutput getById(Long id){
        return this.candidateClient.getById(id);
    }

    public List<CandidateOutput> getAll(){
        return this.candidateClient.getAll();
    }
}
