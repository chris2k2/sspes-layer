package de.cweyermann.sspes.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchResult {

    public enum Result {
        WON("gewonnen"), DRAW("unentschieden gespielt"), LOST("verloren");

        private final String aussprache;

        Result(String aussprache) {
            this.aussprache = aussprache;
        }

        /**
         * just prefix this with: "Du hast "
         */
        public String getAussprache() {
            return aussprache;
        }
    }

    Result result;

    String opponentName;

    String opponentChoice;
}
