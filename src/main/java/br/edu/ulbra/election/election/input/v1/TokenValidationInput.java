package br.edu.ulbra.election.election.input.v1;

public class TokenValidationInput {
    private String token;
    private Long voterId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getVoterId() {
        return voterId;
    }

    public void setVoterId(Long voterId) {
        this.voterId = voterId;
    }
}
