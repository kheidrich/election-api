package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.client.CandidateClient;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final ModelMapper modelMapper;
    private final CandidateClientService candidateClientService;
    private final ElectionRepository electionRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository, ModelMapper modelMapper, CandidateClientService candidateClientService, ElectionRepository electionRepository) {
        this.voteRepository = voteRepository;
        this.modelMapper = modelMapper;
        this.candidateClientService = candidateClientService;
        this.electionRepository = electionRepository;
    }

    public GenericOutput vote(VoteInput voteInput) {
        if (!electionExists(voteInput.getElectionId()))
            throw new GenericOutputException("Invalid election");

        if(!candidateExists(voteInput.getCandidateId()))
            throw new GenericOutputException("Invalid candidate");

        if (alreadyVoted(voteInput.getElectionId(), voteInput.getVoterId()))
            throw new GenericOutputException("Already voted in this election");

        Vote vote = modelMapper.map(voteInput, Vote.class);
        voteRepository.save(vote);

        return new GenericOutput("Ok");
    }

    private boolean alreadyVoted(Long electionId, Long voterId) {
        for (Vote vote : voteRepository.findAll())
            if (vote.getElectionId().equals(electionId)
                    && vote.getVoterId().equals(voterId))
                return true;

        return false;
    }

    private boolean electionExists(Long electionId) {
        return this.electionRepository.findById(electionId).orElse(null) != null;
    }

    private boolean candidateExists(Long candidateId) {
        try {
            this.candidateClientService.getById(candidateId);
            return true;
        } catch (FeignException e) {
            return false;
        }
    }
}
