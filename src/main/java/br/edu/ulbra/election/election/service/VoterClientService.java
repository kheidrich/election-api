package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.client.VoterClient;
import br.edu.ulbra.election.election.input.v1.TokenValidationInput;
import br.edu.ulbra.election.election.output.v1.TokenValidationOutput;
import br.edu.ulbra.election.election.output.v1.VoterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoterClientService {
    private final VoterClient voterClient;

    @Autowired
    public VoterClientService(VoterClient voterClient) {
        this.voterClient = voterClient;
    }

    public VoterOutput getById(Long voterId) {
        return this.voterClient.getById(voterId);
    }

    public TokenValidationOutput validateToken(TokenValidationInput tokenValidationInput){
        return this.voterClient.validateToken(tokenValidationInput);
    }
}
