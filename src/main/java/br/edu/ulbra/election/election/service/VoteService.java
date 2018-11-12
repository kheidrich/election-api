package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.repository.VoteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoteService {

    private VoteRepository voteRepository;
    private ModelMapper modelMapper;

    @Autowired
    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public GenericOutput vote(VoteInput voteInput) {

        if(alreadyVoted(voteInput.getElectionId(), voteInput.getVoterId()))
            throw new GenericOutputException("Already voted in this election");

        Vote vote = modelMapper.map(voteInput, Vote.class);
        vote = voteRepository.save(vote);

        return new GenericOutput("Ok");
    }

    private boolean alreadyVoted(Long electionId, Long voterId) {
        for (Vote vote : voteRepository.findAll())
            if (vote.getElectionId().equals(electionId)
                    && vote.getVoterId().equals(voterId))
                return true;

        return false;
    }
}
