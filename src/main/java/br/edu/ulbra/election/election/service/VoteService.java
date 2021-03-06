package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.TokenValidationInput;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.output.v1.VoteOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final ModelMapper modelMapper;
    private final VoterClientService voterClientService;
    private final ElectionRepository electionRepository;
    private final CandidateClientService candidateClientService;

    @Autowired
    public VoteService(VoteRepository voteRepository, ModelMapper modelMapper, VoterClientService voterClientService, ElectionRepository electionRepository, CandidateClientService candidateClientService) {
        this.voteRepository = voteRepository;
        this.modelMapper = modelMapper;
        this.voterClientService = voterClientService;
        this.electionRepository = electionRepository;
        this.candidateClientService = candidateClientService;
    }

    public List<VoteOutput> getByElectionId(Long electionId) {
        List<VoteOutput> votes = new ArrayList<>();

        for (Vote v : voteRepository.findAll())
            if (v.getElectionId().equals(electionId))
                votes.add(modelMapper.map(v, VoteOutput.class));

        return votes;
    }

    public List<VoteOutput> getByVoterId(Long voterId) {
        List<VoteOutput> votes = new ArrayList<>();

        for (Vote v : voteRepository.findAll())
            if (v.getVoterId().equals(voterId))
                votes.add(modelMapper.map(v, VoteOutput.class));

        return votes;
    }

    public GenericOutput vote(VoteInput voteInput) {
        TokenValidationInput tokenValidationInput = new TokenValidationInput();
        tokenValidationInput.setToken(voteInput.getToken());
        tokenValidationInput.setVoterId(voteInput.getVoterId());

        if(!voterClientService.validateToken(tokenValidationInput).getValid())
            throw new GenericOutputException("Invalid token");

        if (!electionExists(voteInput.getElectionId()))
            throw new GenericOutputException("Invalid election");

        if (!voterExists(voteInput.getVoterId()))
            throw new GenericOutputException("Invalid voter");

        if (alreadyVoted(voteInput.getElectionId(), voteInput.getVoterId()))
            throw new GenericOutputException("Already voted in this election");

        if (voteInput.getCandidateId() == null)
            voteInput.setCandidateId(0L);
        else if (!candidateExists(voteInput.getCandidateId()))
            voteInput.setCandidateId(null);


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

    private boolean voterExists(Long voterId) {
        try {
            this.voterClientService.getById(voterId);
            return true;
        } catch (FeignException e) {
            return false;
        }
    }

    private boolean candidateExists(Long candidateId) {
        try {
            candidateClientService.getById(candidateId);
            return true;
        } catch (FeignException e) {
            return false;
        }
    }
}
