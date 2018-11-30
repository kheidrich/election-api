package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import br.edu.ulbra.election.election.output.v1.ElectionCandidateResultOutput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.ResultOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResultService {

    private final ModelMapper modelMapper;
    private final ElectionRepository electionRepository;
    private final VoteRepository voteRepository;
    private final CandidateClientService candidateClientService;

    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not found";

    @Autowired
    public ResultService(
            ModelMapper modelMapper,
            ElectionRepository electionRepository,
            VoteRepository voteRepository,
            CandidateClientService candidateClientService
    ) {
        this.modelMapper = modelMapper;
        this.electionRepository = electionRepository;
        this.voteRepository = voteRepository;
        this.candidateClientService = candidateClientService;
    }

    public ResultOutput getElectionResult(Long electionId) {
        ResultOutput result = new ResultOutput();

        if (electionId == null)
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null)
            throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);

        result.setElection(modelMapper.map(election, ElectionOutput.class));
        result.setCandidates(generateCandidatesResults(electionId));
        result.setTotalVotes(countTotalVotes(electionId));
        result.setBlankVotes(countBlankVotes(electionId));
        result.setNullVotes(countNullVotes(electionId));

        return result;
    }

    public ElectionCandidateResultOutput getElectionCanditateResult(Long candidateId) {
        ElectionCandidateResultOutput candidateResult = new ElectionCandidateResultOutput();

        try {
            CandidateOutput candidate = candidateClientService.getById(candidateId);

            candidateResult.setCandidate(candidate);
            candidateResult.setTotalVotes(countCandidateVotes(candidateId));

            return candidateResult;
        } catch (FeignException e) {
            if (e.status() == 404)
                throw new GenericOutputException("Candidate not found");
            else
                throw new GenericOutputException("Error in retrieving candidate");
        }
    }

    private List<ElectionCandidateResultOutput> generateCandidatesResults(Long electionId) {
        List<CandidateOutput> electionCandidates;
        List<ElectionCandidateResultOutput> results = new ArrayList<ElectionCandidateResultOutput>();

        electionCandidates = candidateClientService.getByElectionId(electionId);
        for (CandidateOutput candidate : electionCandidates) {
            ElectionCandidateResultOutput candidateResult = new ElectionCandidateResultOutput();

            candidateResult.setCandidate(candidate);
            candidateResult.setTotalVotes(countCandidateVotes(candidate.getId()));
            results.add(candidateResult);
        }

        return results;
    }

    private Long countCandidateVotes(Long candidateId) {
        Long votes = 0L;

        for (Vote vote : voteRepository.findAll())
            if (vote.getCandidateId() != null && vote.getCandidateId().equals(candidateId))
                votes++;

        return votes;
    }

    private Long countTotalVotes(Long electionId) {
        Long total = 0L;

        for (Vote vote : voteRepository.findAll())
            if (vote.getElectionId().equals(electionId))
                total++;

        return total;
    }

    private Long countBlankVotes(Long electionId) {
        Long blank = 0L;

        for (Vote vote : voteRepository.findAll())
            if (vote.getElectionId().equals(electionId)
                    && vote.getCandidateId() != null
                    && vote.getCandidateId().equals(0L))
                blank++;

        return blank;
    }

    private Long countNullVotes(Long electionId) {
        Long nullVote = 0L;

        for (Vote vote : voteRepository.findAll())
            if (vote.getElectionId().equals(electionId) && vote.getCandidateId() == null)
                nullVote++;

        return nullVote;
    }
}


