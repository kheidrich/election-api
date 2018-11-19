package br.edu.ulbra.election.election.service;

import antlr.StringUtils;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.ElectionInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ElectionService {
    private final ModelMapper modelMapper;
    private final ElectionRepository electionRepository;
    private final VoteRepository voteRepository;
    private final CandidateClientService candidateClientService;

    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not found";
    private static final String MESSAGE_ELECTION_DELETED = "Election deleted";

    @Autowired
    public ElectionService(ElectionRepository electionRepository, ModelMapper modelMapper, VoteRepository voteRepository, CandidateClientService candidateClientService) {
        this.electionRepository = electionRepository;
        this.modelMapper = modelMapper;
        this.voteRepository = voteRepository;
        this.candidateClientService = candidateClientService;
    }

    public List<ElectionOutput> getAll() {
        Type electionOutputListType = new TypeToken<List<ElectionOutput>>() {
        }.getType();
        return modelMapper.map(electionRepository.findAll(), electionOutputListType);
    }

    public ElectionOutput getById(Long electionId) {
        if (electionId == null)
            throw new GenericOutputException(MESSAGE_INVALID_ID);

        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null)
            throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);

        return modelMapper.map(election, ElectionOutput.class);
    }

    public List<ElectionOutput> getByYear(Integer year) {
        Type electionOutputListType = new TypeToken<List<ElectionOutput>>() {
        }.getType();
        List<Election> electionsOfTheYear = new ArrayList<>();

        for (Election election : electionRepository.findAll()) {
            if (election.getYear().equals(year))
                electionsOfTheYear.add(election);
        }

        return modelMapper.map(electionsOfTheYear, electionOutputListType);
    }

    public ElectionOutput create(ElectionInput electionInput) {
        validateInput(electionInput);
        Election election = modelMapper.map(electionInput, Election.class);
        election = electionRepository.save(election);

        return modelMapper.map(election, ElectionOutput.class);
    }

    public ElectionOutput update(Long electionId, ElectionInput electionInput) {
        if (electionId == null)
            throw new GenericOutputException(MESSAGE_INVALID_ID);

        if (this.electionHasVotes(electionId) || this.electionHasCandidate(electionId))
            throw new GenericOutputException("Cannot update election");

        validateInput(electionInput);

        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null)
            throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);

        election.setYear(electionInput.getYear());
        election.setStateCode(electionInput.getStateCode());
        election.setDescription(electionInput.getDescription());
        election = electionRepository.save(election);

        return modelMapper.map(election, ElectionOutput.class);
    }

    public GenericOutput delete(Long electionId) {
        if (electionId == null)
            throw new GenericOutputException(MESSAGE_INVALID_ID);

        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null)
            throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);

        if (this.electionHasVotes(electionId) || this.electionHasCandidate(electionId))
            throw new GenericOutputException("Cannot delete election");

        electionRepository.delete(election);

        return new GenericOutput(MESSAGE_ELECTION_DELETED);
    }

    private void validateInput(ElectionInput input) {
        if (input.getYear() == null || input.getYear() < 2000 || input.getYear() > 2200)
            throw new GenericOutputException("Invalid year");

        if (input.getStateCode() == null || !validStateScode(input.getStateCode()))
            throw new GenericOutputException("Invalid state code");

        if (input.getDescription() == null || input.getDescription().length() < 5)
            throw new GenericOutputException("Invalid description");
    }

    private boolean validStateScode(String stateCode) {
        ArrayList<String> brazillianStateCodes = new ArrayList<>(Arrays.asList("BR", "RO", "AC", "AM", "RR", "PA", "AP", "TO", "MA", "PI", "CE", "RN", "PB", "PE", "AL", "SE", "BA", "MG", "ES", "RJ", "SP", "PR", "SC", "RS", "MS", "MT", "GO", "DF"));

        return brazillianStateCodes.contains(stateCode);
    }

    private boolean electionHasVotes(Long electionId) {
        for (Vote vote : voteRepository.findAll())
            if (vote.getElectionId().equals(electionId))
                return true;

        return false;
    }

    private boolean electionHasCandidate(Long electionId) {
        List<CandidateOutput> candidates = this.candidateClientService.getByElectionId(electionId);

        return candidates.size() > 0;
    }
}
