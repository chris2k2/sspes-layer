package de.cweyermann.sspes.api;

import de.cweyermann.sspes.api.repo.Participant;
import de.cweyermann.sspes.api.repo.Repository;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SspesAPITest {

    @Test
    public void listofWinsAgainstPapier() {
        //given
        SspesAPI api = new SspesAPI();

        //when
        List<String> winnerAgainstPapier = api.winsAgainst("PaPier");

        //then
        assertEquals(2, winnerAgainstPapier.size());
        assertEquals("schere", winnerAgainstPapier.get(0));
        assertEquals("echse", winnerAgainstPapier.get(1));
    }

    @Test
    public void getAllOpponentsMapsToString() {
        SspesAPI api = getSspesAPIWithOneOpponent("dieter");

        List<String> validOpponents = api.getValidOpponents();

        assertEquals(1, validOpponents.size());
        assertEquals("dieter", validOpponents.get(0));
    }

    private SspesAPI getSspesAPIWithOneOpponent(String name) {
        Repository repo = mock(Repository.class);
        when(repo.getAllParticipants()).thenReturn(
                Collections.singletonList(new Participant(name, "url", "schere"))
        );
        return new SspesAPI(repo, new CallOpponent());
    }

    @Test
    public void doesPapierWinAgainstStein() {
        boolean doYouWin = new SspesAPI().doYouWinAgainst("papier", "stein");

        assertTrue(doYouWin);
    }
    @Test
    public void doesSteinWinAgainstPapier() {
        boolean doYouWin = new SspesAPI().doYouWinAgainst("stein", "papier");

        assertFalse(doYouWin);
    }

    @Test
    public void unfairAutomaticGoesToYou() {
        boolean doYouWin = new SspesAPI().doYouWinAgainst("stein", "brunnen");

        assertTrue(doYouWin);
    }

    @Test
    public void youUnfairAutomaticGoesToOpponent() {
        boolean doYouWin = new SspesAPI().doYouWinAgainst("brunnen", "EcHse");

        assertFalse(doYouWin);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playAgainstThrowsExceptionIfOpponentInvalid() {
        SspesAPI api = getSspesAPIWithOneOpponent("dieter");

        api.playUnrankedAgainst("schere", "nicht dieter");
    }

    @Test(expected = IllegalArgumentException.class)
    public void playAgainstThrowsExceptionIfChoiceInvalid() {
        SspesAPI api = getSspesAPIWithOneOpponent("dieter");

        api.playUnrankedAgainst("brunnen", "dieter");
    }

    @Test
    public void unrankedWorks() {
        CallOpponent opp = mock(CallOpponent.class);
        when(opp.getChoiceOf(any(), any())).thenReturn("schere");

        Repository repo = mock(Repository.class);
        when(repo.getAllParticipants()).thenReturn(
                Collections.singletonList(new Participant("dieter", "url", "schere"))
        );

        SspesAPI api = new SspesAPI(repo, opp);

        MatchResult matchResult = api.playUnrankedAgainst("stein", "dieter");

        assertEquals("schere", matchResult.opponentChoice);
        assertEquals(MatchResult.Result.WON, matchResult.result);
        assertEquals("dieter", matchResult.opponentName);
    }

    @Test
    public void unrankedAgainstRandomWorks() {
        CallOpponent opp = mock(CallOpponent.class);
        when(opp.getChoiceOf(any(), any())).thenReturn("stein");

        Repository repo = mock(Repository.class);
        when(repo.getAllParticipants()).thenReturn(
                Collections.singletonList(new Participant("dieter", "url", "stein"))
        );

        SspesAPI api = new SspesAPI(repo, opp);

        MatchResult matchResult = api.playUnrankedAgainstRandom("stein");

        assertEquals("stein", matchResult.opponentChoice);
        assertEquals(MatchResult.Result.DRAW, matchResult.result);
        assertEquals("dieter", matchResult.opponentName);
    }
}