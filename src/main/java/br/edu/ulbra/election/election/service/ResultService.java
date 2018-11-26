package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.ResultOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResultService {

    private final ModelMapper modelMapper;
    private final ElectionRepository electionRepository;

    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not found";

    @Autowired
    public ResultService(ModelMapper modelMapper, ElectionRepository electionRepository){

        this.modelMapper = modelMapper;
        this.electionRepository = electionRepository;
    }

    public ResultOutput getResultByElection(Long electionId){
        ResultOutput result = new ResultOutput();

        if (electionId == null)
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null)
            throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);

        result.setElection(modelMapper.map(election, ElectionOutput.class));

        return result;
    }
}


