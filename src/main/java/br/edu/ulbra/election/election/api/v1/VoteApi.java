package br.edu.ulbra.election.election.api.v1;

import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.output.v1.VoteOutput;
import br.edu.ulbra.election.election.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/vote")
public class VoteApi {

    private VoteService voteService;

    @Autowired
    public VoteApi(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping("/{electionId}")
    public List<VoteOutput> getByElectionId(@PathVariable(name = "electionId") Long electionId) {
        return voteService.getByElectionId(electionId);
    }

    @PutMapping("/{electionId}")
    public GenericOutput electionVote(@RequestBody VoteInput voteInput) {
        return voteService.vote(voteInput);
    }

    @PutMapping("/multiple")
    public GenericOutput multipleElectionVote(@RequestBody List<VoteInput> voteInputList) {
        return new GenericOutput("OK");
    }
}
