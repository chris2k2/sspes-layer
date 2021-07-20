package de.cweyermann.sspes.api;

import de.cweyermann.sspes.api.repo.Participant;
import de.cweyermann.sspes.api.repo.Repository;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class SspesAPI {
    private static final List<String> ALLOWED = Arrays.asList(
            "schere",
            "stein",
            "papier",
            "echse",
            "spock"
    );

    private static final List<String> WINNING_COMB = Arrays.asList(
            "schere.papier",
            "papier.stein",
            "stein.echse",
            "echse.spock",
            "spock.schere",
            "schere.echse",
            "echse.papier",
            "papier.spock",
            "spock.stein",
            "stein.schere"
    );
    private final Repository repo;
    private final CallOpponent call;

    public SspesAPI() {
        this(new Repository(), new CallOpponent());
    }

    public SspesAPI(Repository repo, CallOpponent callOpponent) {
        this.repo = repo;
        this.call = callOpponent;
    }

    public List<String> getAllowed() {
        return ALLOWED;
    }

    public List<String> winsAgainst(String option) {
        return WINNING_COMB
                .stream()
                .filter(x -> x.contains("." + option.toLowerCase()))
                .map(x -> x.replaceAll("[.].*", ""))
                .collect(Collectors.toList());
    }

    public List<String> getValidOpponents() {
        return repo
                .getAllParticipants()
                .stream()
                .map(p -> p.getName())
                .collect(Collectors.toList());
    }

    public boolean doYouWinAgainst(String yourChoice, String otherChoice) {
        return playsFair(yourChoice) &&
                (!playsFair(otherChoice) || wins(yourChoice, otherChoice));
    }

    public boolean wins(String yourChoice, String otherChoice) {
        return WINNING_COMB.contains(yourChoice.toLowerCase() + "." + otherChoice.toLowerCase());
    }

    public boolean playsFair(String choice) {
        return ALLOWED.contains(choice.toLowerCase());
    }

    public MatchResult playUnrankedAgainstRandom(String yourChoice) {
        List<String> opponents = getValidOpponents();
        Collections.shuffle(opponents);

        return playUnrankedAgainst(yourChoice, opponents.get(0));
    }

    public MatchResult playUnrankedAgainst(String yourChoice, String opponent) {
        if (!getValidOpponents().contains(opponent) || !playsFair(yourChoice)) {
            throw new IllegalArgumentException("Your choice must be one of schere, stein, papier, echse, spock");
        }

        Participant participant = repo
                .getAllParticipants()
                .stream()
                .filter(p -> p.getName().equals(opponent))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("opponent with name " + opponent + " unknown"));

        String choiceOfOpponent = this.call.getChoiceOf(participant, "");

        MatchResult.MatchResultBuilder builder = MatchResult.builder();
        if (choiceOfOpponent.equalsIgnoreCase(yourChoice)) {
            builder.result(MatchResult.Result.DRAW);
        } else if (doYouWinAgainst(yourChoice, choiceOfOpponent)) {
            builder.result(MatchResult.Result.WON);
        } else {
            builder.result(MatchResult.Result.LOST);
        }

        return builder
                .opponentChoice(choiceOfOpponent)
                .opponentName(opponent)
                .build();
    }
}
