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

    public List<ElectionOutput> getByYear(Integer year) {
        Type electionOutputListType = new TypeToken<List<ElectionOutput>>() {}.getType();
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

    private void validateInput(ElectionInput input) {
        if (input.getYear() < 0)
            throw new GenericOutputException("Invalid year");

        if (input.getStateCode().length() != 2)
            throw new GenericOutputException("Invalid state code");

        if (input.getDescription().isEmpty())
            throw new GenericOutputException("Must provide a description");
    }
}
