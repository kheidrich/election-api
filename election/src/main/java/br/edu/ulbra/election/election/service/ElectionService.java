package br.edu.ulbra.election.election.service;

import antlr.StringUtils;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.ElectionInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElectionService {
    private final ElectionRepository electionRepository;
    private final ModelMapper modelMapper;

    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not found";

    @Autowired
    public ElectionService(ElectionRepository electionRepository, ModelMapper modelMapper) {
        this.electionRepository = electionRepository;
        this.modelMapper = modelMapper;
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

    private void validateInput(ElectionInput input) {
        if (input.getYear() != null && input.getYear() < 0)
            throw new GenericOutputException("Invalid year");

        if (input.getStateCode() != null && input.getStateCode().length() != 2)
            throw new GenericOutputException("Invalid state code");

        if (input.getDescription() != null && input.getDescription().isEmpty())
            throw new GenericOutputException("Must provide a description");
    }
}
