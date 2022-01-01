import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TournamentSimulator {

    public ArrayList<ArrayList<String>> trainData;

    // array containing all teams that qualified to the knock-out phase
    public String[] teams = {"Liverpool", "Ajax", "Bayern München", "Manchester United", "Chelsea",
        "Juventus", "Manchester City", "Paris Saint-Germain", "Real Madrid", "Inter", "Benfica",
        "Sporting CP", "Atlético de Madrid", "LOSC Lille", "RB Salzburg", "Villarreal CF"};

    // official round of 16 fixtures of this year's UCL tournament
    public String[][] R16Fixtures = {{"Paris Saint-Germain", "Real Madrid"},
        {"Sporting CP", "Manchester City"}, {"RB Salzburg", "Bayern München"},
        {"Inter", "Liverpool"}, {"Chelsea", "LOSC Lille"}, {"Villarreal CF", "Juventus"},
        {"Atlético de Madrid", "Manchester United"}, {"Benfica", "Ajax"}};

    // class constructor
    public TournamentSimulator() {
        this.trainData = new ArrayList<ArrayList<String>>(900000);
        for (int i = 0; i < 900000; i++) {
            trainData.add(new ArrayList<>());
        }
    }

    // cleans raw data found in other classes for training
    public void cleanForTraining(List<List<String>> list) {

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).size() == 0)
                break;
            trainData.get(i).add(list.get(i).get(4));
            trainData.get(i).add(list.get(i).get(5));
            trainData.get(i).add(list.get(i).get(6));
            trainData.get(i).add(list.get(i).get(7));
            trainData.get(i).add(list.get(i).get(8));
            trainData.get(i).add(list.get(i).get(9));
            trainData.get(i).add(list.get(i).get(10));
        }
    }

    // makes use of the Monte Carlo method to simulate matches
    public String[] matchSimulator(String[] fixture, String home, String away, Regression r,
        boolean isFinal) {
        ArrayList<String> fix = new ArrayList<String>();
        String[] results = new String[2];
        Random rand = new Random();
        int h_win = 0;
        int a_win = 0;
        String winner = "";
        // two-legged fixtures
        if (!isFinal) {
            // first leg
            for (int i = 0; i < 10000; i++) {
                double att1 = Double.parseDouble(fixture[0]), mid1 = Double.parseDouble(fixture[1]),
                    def1 = Double.parseDouble(fixture[2]), ovr1 = Double.parseDouble(fixture[3]),
                    att2 = Double.parseDouble(fixture[4]), mid2 = Double.parseDouble(fixture[5]),
                    def2 = Double.parseDouble(fixture[6]), ovr2 = Double.parseDouble(fixture[7]);

                // team form involves randomness. after finding a random value for a team's current
                // form, we add that value to each attribute (ATT, MID, and DEF) of the team in
                // question
                int homeCurrForm = rand.nextInt(11) - 5;
                int awayCurrForm = rand.nextInt(11) - 5;

                // adjustments made fairly according to team sheets and number of attackers in each
                // team that are classifies as midfielders in the overall ratings of FIFA
                if (home.equals("Paris Saint-Germain"))
                    att1 = att1 * 0.7 + mid1 * 0.3;
                else if (home.equals("Real Madrid"))
                    att1 = att1 * 0.9 + mid1 * 0.1;
                else if (home.equals("Liverpool"))
                    att1 = att1 * 1;
                else if (home.equals("Manchester United"))
                    att1 = att1 * 0.7 + mid1 * 0.3;
                else if (home.equals("Manchester City"))
                    att1 = att1 * 0.15 + mid1 * 0.85;
                else
                    att1 = att1 * 0.3 + mid1 * 0.7;

                // adjustments made fairly according to team sheets and number of attackers in each
                // team that are classifies as midfielders in the overall ratings of FIFA
                if (away.equals("Paris Saint-Germain"))
                    att2 = att2 * 0.7 + mid2 * 0.3;
                else if (away.equals("Real Madrid"))
                    att2 = att2 * 0.9 + mid2 * 0.1;
                else if (away.equals("Liverpool"))
                    att2 = att2 * 1;
                else if (away.equals("Manchester United"))
                    att2 = att2 * 0.7 + mid2 * 0.3;
                else if (away.equals("Manchester City"))
                    att2 = att2 * 0.15 + mid2 * 0.85;
                else
                    att2 = att2 * 0.3 + mid2 * 0.7;

                att1 = att1 + homeCurrForm;
                mid1 = mid1 + homeCurrForm;
                def1 = def1 + homeCurrForm;
                att2 = att2 + awayCurrForm;
                mid2 = mid2 + awayCurrForm;
                def2 = def2 + awayCurrForm;

                double att_1 = att1 - ((def2 + mid2) / 2.0);
                double mid_1 = mid1 - ((def2 + att2) / 2.0);
                double def_1 = def1 - ((att2 + mid2) / 2.0);
                double att_2 = att2 - ((def1 + mid1) / 2.0);
                double mid_2 = mid2 - ((def1 + att1) / 2.0);
                double def_2 = def2 - ((att1 + mid1) / 2.0);

                fix.add(Double.toString(att_1));
                fix.add(Double.toString(mid_1));
                fix.add(Double.toString(def_1));
                fix.add(Double.toString(att_2));
                fix.add(Double.toString(mid_2));
                fix.add(Double.toString(def_2));

                double result = r.findProbability(fix);
                if (result > 0.5)
                    h_win++;
                else
                    a_win++;
                fix = new ArrayList<String>();
            }

            // second leg
            for (int i = 0; i < 10000; i++) {
                double att2 = Double.parseDouble(fixture[0]), mid2 = Double.parseDouble(fixture[1]),
                    def2 = Double.parseDouble(fixture[2]), ovr2 = Double.parseDouble(fixture[3]),
                    att1 = Double.parseDouble(fixture[4]), mid1 = Double.parseDouble(fixture[5]),
                    def1 = Double.parseDouble(fixture[6]), ovr1 = Double.parseDouble(fixture[7]);

                int homeCurrForm = rand.nextInt(11) - 5;
                int awayCurrForm = rand.nextInt(11) - 5;

                // adjustments made fairly according to team sheets and number of attackers in each
                // team that are classifies as midfielders in the overall ratings of FIFA
                if (home.equals("Paris Saint-Germain"))
                    att1 = att1 * 0.7 + mid1 * 0.3;
                else if (home.equals("Real Madrid"))
                    att1 = att1 * 0.9 + mid1 * 0.1;
                else if (home.equals("Liverpool"))
                    att1 = att1 * 1;
                else if (home.equals("Manchester United"))
                    att1 = att1 * 0.7 + mid1 * 0.3;
                else if (home.equals("Manchester City"))
                    att1 = att1 * 0.15 + mid1 * 0.85;
                else
                    att1 = att1 * 0.3 + mid1 * 0.7;

                // adjustments made fairly according to team sheets and number of attackers in each
                // team that are classifies as midfielders in the overall ratings of FIFA
                if (away.equals("Paris Saint-Germain"))
                    att2 = att2 * 0.7 + mid2 * 0.3;
                else if (away.equals("Real Madrid"))
                    att2 = att2 * 0.9 + mid2 * 0.1;
                else if (away.equals("Liverpool"))
                    att2 = att2 * 1;
                else if (away.equals("Manchester United"))
                    att2 = att2 * 0.7 + mid2 * 0.3;
                else if (away.equals("Manchester City"))
                    att2 = att2 * 0.15 + mid2 * 0.85;
                else
                    att2 = att2 * 0.3 + mid2 * 0.7;

                att1 = att1 + homeCurrForm;
                mid1 = mid1 + homeCurrForm;
                def1 = def1 + homeCurrForm;
                att2 = att2 + awayCurrForm;
                mid2 = mid2 + awayCurrForm;
                def2 = def2 + awayCurrForm;

                double att_1 = att1 - ((def2 + mid2) / 2.0);
                double mid_1 = mid1 - ((def2 + att2) / 2.0);
                double def_1 = def1 - ((att2 + mid2) / 2.0);
                double att_2 = att2 - ((def1 + mid1) / 2.0);
                double mid_2 = mid2 - ((def1 + att1) / 2.0);
                double def_2 = def2 - ((att1 + mid1) / 2.0);

                fix.add(Double.toString(att_1));
                fix.add(Double.toString(mid_1));
                fix.add(Double.toString(def_1));
                fix.add(Double.toString(att_2));
                fix.add(Double.toString(mid_2));
                fix.add(Double.toString(def_2));

                double result = r.findProbability(fix);
                if (result > 0.5)
                    a_win++;
                else
                    h_win++;
                fix = new ArrayList<String>();
            }

            double prob_home = (double) h_win / 20000.0 * 100;
            double prob_away = (double) a_win / 20000.0 * 100;

            System.out.println(home + ": " + prob_home + "%");
            System.out.println(away + ": " + prob_away + "%");

            if (prob_home > prob_away) {
                winner = home;
                results[0] = winner;
                results[1] = Double.toString(prob_home);
            } else {
                winner = away;
                results[0] = winner;
                results[1] = Double.toString(prob_away);
            }
        }

        else {
            for (int i = 0; i < 10000; i++) {
                double att1 = Double.parseDouble(fixture[0]), mid1 = Double.parseDouble(fixture[1]),
                    def1 = Double.parseDouble(fixture[2]), ovr1 = Double.parseDouble(fixture[3]),
                    att2 = Double.parseDouble(fixture[4]), mid2 = Double.parseDouble(fixture[5]),
                    def2 = Double.parseDouble(fixture[6]), ovr2 = Double.parseDouble(fixture[7]);

                int homeCurrForm = rand.nextInt(11) - 5;
                int awayCurrForm = rand.nextInt(11) - 5;

                // adjustments made fairly according to team sheets and number of attackers in each
                // team that are classifies as midfielders in the overall ratings of FIFA
                if (home.equals("Paris Saint-Germain"))
                    att1 = att1 * 0.7 + mid1 * 0.3;
                else if (home.equals("Real Madrid"))
                    att1 = att1 * 0.9 + mid1 * 0.1;
                else if (home.equals("Liverpool"))
                    att1 = att1 * 1;
                else if (home.equals("Manchester United"))
                    att1 = att1 * 0.7 + mid1 * 0.3;
                else if (home.equals("Manchester City"))
                    att1 = att1 * 0.15 + mid1 * 0.85;
                else
                    att1 = att1 * 0.3 + mid1 * 0.7;

                // adjustments made fairly according to team sheets and number of attackers in each
                // team that are classifies as midfielders in the overall ratings of FIFA
                if (away.equals("Paris Saint-Germain"))
                    att2 = att2 * 0.7 + mid2 * 0.3;
                else if (away.equals("Real Madrid"))
                    att2 = att2 * 0.9 + mid2 * 0.1;
                else if (away.equals("Liverpool"))
                    att2 = att2 * 1;
                else if (away.equals("Manchester United"))
                    att2 = att2 * 0.7 + mid2 * 0.3;
                else if (away.equals("Manchester City"))
                    att2 = att2 * 0.15 + mid2 * 0.85;
                else
                    att2 = att2 * 0.3 + mid2 * 0.7;

                att1 = att1 + homeCurrForm;
                mid1 = mid1 + homeCurrForm;
                def1 = def1 + homeCurrForm;
                att2 = att2 + awayCurrForm;
                mid2 = mid2 + awayCurrForm;
                def2 = def2 + awayCurrForm;

                double att_1 = att1 - ((def2 + mid2) / 2.0);
                double mid_1 = mid1 - ((def2 + att2) / 2.0);
                double def_1 = def1 - ((att2 + mid2) / 2.0);
                double att_2 = att2 - ((def1 + mid1) / 2.0);
                double mid_2 = mid2 - ((def1 + att1) / 2.0);
                double def_2 = def2 - ((att1 + mid1) / 2.0);

                fix.add(Double.toString(att_1));
                fix.add(Double.toString(mid_1));
                fix.add(Double.toString(def_1));
                fix.add(Double.toString(att_2));
                fix.add(Double.toString(mid_2));
                fix.add(Double.toString(def_2));

                double result = r.findProbability(fix);
                if (result > 0.5)
                    h_win++;
                else
                    a_win++;
                fix = new ArrayList<String>();
            }

            double prob_home = (double) h_win / 10000.0 * 100;
            double prob_away = (double) a_win / 10000.0 * 100;

            System.out.println(home + ": " + prob_home + "%");
            System.out.println(away + ": " + prob_away + "%");

            if (prob_home > prob_away) {
                winner = home;
                results[0] = winner;
                results[1] = Double.toString(prob_home);
            } else {
                winner = away;
                results[0] = winner;
                results[1] = Double.toString(prob_away);
            }
        }

        return results;
    }

    // function that performs draw in between rounds of the tournament
    public String[][] performDraw(ArrayList<String> pot, int numTeams) {
        int bound = numTeams;
        Random rand = new Random();
        String[][] fixtures = new String[numTeams / 2][2];
        for (int i = 0; i < fixtures.length; i++) {
            int idxDrawn = rand.nextInt(bound);
            fixtures[i][0] = pot.get(idxDrawn);
            pot.remove(idxDrawn);
            bound--;
            if (bound > 1) {
                idxDrawn = rand.nextInt(bound);
                fixtures[i][1] = pot.get(idxDrawn);
                pot.remove(idxDrawn);
                bound--;
            } else {
                fixtures[i][1] = pot.get(0);
                pot.remove(0);
            }
        }
        return fixtures;
    }

    public static void main(String[] args) throws IOException {
        TeamStatReader stats = new TeamStatReader();
        MatchDataReader rdr = new MatchDataReader();
        List<List<String>> scratch = new ArrayList<List<String>>(900000);
        for (int i = 0; i < 900000; i++) {
            scratch.add(new ArrayList<>());
        }
        rdr.fetchFixtures();
        Collections.copy(scratch, rdr.getFixtures());
        System.out.println("\n\n" + scratch.get(0).get(1));
        TournamentSimulator sim = new TournamentSimulator();
        sim.cleanForTraining(scratch);
        Regression r = new Regression(6);
        r.trainData(sim.trainData);

        System.out.println(
            "\n The probabilities that the training examples are classified in category M are :\n");
        int o = 0;
        int m = 0;
        for (int i = 0; i < sim.trainData.size(); i++) {
            if (sim.trainData.get(i).size() == 0)
                break;
            ArrayList<String> attributes = new ArrayList<String>();
            attributes.add(sim.trainData.get(i).get(1));
            attributes.add(sim.trainData.get(i).get(2));
            attributes.add(sim.trainData.get(i).get(3));
            attributes.add(sim.trainData.get(i).get(4));
            attributes.add(sim.trainData.get(i).get(5));
            attributes.add(sim.trainData.get(i).get(6));
            System.out.println("Example belongs in category " + sim.trainData.get(i).get(0) + " "
                + r.findProbability(attributes));
            m++;
            if (((r.findProbability(attributes) < 0.5) && (sim.trainData.get(i).get(0).equals("1")))
                || (r.findProbability(attributes) > 0.5)
                    && (sim.trainData.get(i).get(0).equals("-1")
                        || sim.trainData.get(i).get(0).equals("0")))
                o++;
        }
        // Wrong classified training examples
        System.out.println("\n The number of the wrong classified training examples is: " + o);
        System.out.println("\n The % correctness is: "
            + ((((double) m - (double) o) / (double) m) * (double) 100));

        System.out.println("\n Get ready, the tournament is about to start!\n");

        ArrayList<String> pot = new ArrayList<String>();
        Hashtable<String, Double> odds = new Hashtable<String, Double>();

        // simulate round of 16
        for (int i = 0; i < sim.R16Fixtures.length; i++) {
            String[] fixture = new String[8];
            String[] team1 = rdr.allStats.get(sim.R16Fixtures[i][0] + "2022");
            String[] team2 = rdr.allStats.get(sim.R16Fixtures[i][1] + "2022");
            fixture[0] = team1[2];
            fixture[1] = team1[3];
            fixture[2] = team1[4];
            fixture[3] = team1[5];
            fixture[4] = team2[2];
            fixture[5] = team2[3];
            fixture[6] = team2[4];
            fixture[7] = team2[5];
            String[] result =
                sim.matchSimulator(fixture, sim.R16Fixtures[i][0], sim.R16Fixtures[i][1], r, false);
            pot.add(result[0]);
            System.out.println("Quarter-finalist #" + (i + 1) + ": " + pot.get(i) + "\n");
            odds.put(result[0], (Double.parseDouble(result[1]) / 100.0));
        }

        System.out.println(
            "\n Now that the Round of 16 fixtures are done, it's time for the Quarter Finals Draw!\n");

        // quarter finals draw
        String[][] QFFixures = sim.performDraw(pot, pot.size());
        pot = new ArrayList<String>();

        System.out.println(
            "\n Things are shaping up in the UEFA Champions League as we kick off the Quarter Finals!\n");

        // simulate quarter finals
        for (int i = 0; i < QFFixures.length; i++) {
            String[] fixture = new String[8];
            String[] team1 = rdr.allStats.get(QFFixures[i][0] + "2022");
            String[] team2 = rdr.allStats.get(QFFixures[i][1] + "2022");
            fixture[0] = team1[2];
            fixture[1] = team1[3];
            fixture[2] = team1[4];
            fixture[3] = team1[5];
            fixture[4] = team2[2];
            fixture[5] = team2[3];
            fixture[6] = team2[4];
            fixture[7] = team2[5];
            String[] result =
                sim.matchSimulator(fixture, QFFixures[i][0], QFFixures[i][1], r, false);
            pot.add(result[0]);
            System.out.println("Semi-finalist #" + (i + 1) + ": " + pot.get(i) + "\n");
            double x = odds.get(result[0]);
            x = x * (Double.parseDouble(result[1]) / 100.0);
            odds.replace(result[0], x);
        }

        System.out.println(
            "\n As the Quarter Finals come to a close, this is where things get interesting for the remaining 4 clubs. The Semi-Finals Draw is officially underway!\n");

        // semi-finals draw
        String[][] SFFixures = sim.performDraw(pot, pot.size());
        pot = new ArrayList<String>();

        System.out.println(
            "\n All to play for as far as these four clubs are concerned as we approach the business end of the competition. Get your popcorn ready, we're about to witness an absolute madness!\n");

        // simulate semi-finals
        for (int i = 0; i < SFFixures.length; i++) {
            String[] fixture = new String[8];
            String[] team1 = rdr.allStats.get(SFFixures[i][0] + "2022");
            String[] team2 = rdr.allStats.get(SFFixures[i][1] + "2022");
            fixture[0] = team1[2];
            fixture[1] = team1[3];
            fixture[2] = team1[4];
            fixture[3] = team1[5];
            fixture[4] = team2[2];
            fixture[5] = team2[3];
            fixture[6] = team2[4];
            fixture[7] = team2[5];
            String[] result =
                sim.matchSimulator(fixture, SFFixures[i][0], SFFixures[i][1], r, false);
            pot.add(result[0]);
            System.out.println("Finalist #" + (i + 1) + ": " + pot.get(i) + "\n");
            double x = odds.get(result[0]);
            x = x * (Double.parseDouble(result[1]) / 100.0);
            odds.replace(result[0], x);
        }

        // simulate final
        String[] finalFixture = {pot.get(0), pot.get(1)};

        System.out.println(
            "\n We are on the brink of witnessing greatness. This is where it all matters for these two clubs. This is where players achieve immortality, where every managerial decision counts, where dreams are both achieved and torn to shreads. The whole world watches. "
                + finalFixture[0] + " take on " + finalFixture[1] + " in what promises to be the greatest Champions League final in a long long time. \n");

        String[] fixture = new String[8];
        String[] team1 = rdr.allStats.get(finalFixture[0] + "2022");
        String[] team2 = rdr.allStats.get(finalFixture[1] + "2022");
        fixture[0] = team1[2];
        fixture[1] = team1[3];
        fixture[2] = team1[4];
        fixture[3] = team1[5];
        fixture[4] = team2[2];
        fixture[5] = team2[3];
        fixture[6] = team2[4];
        fixture[7] = team2[5];
        String[] result = sim.matchSimulator(fixture, finalFixture[0], finalFixture[1], r, true);
        System.out.println("\n" + result[0] + " have won the UEFA Champions League!\n");
        double x = odds.get(result[0]);
        x = x * (Double.parseDouble(result[1]) / 100.0);
        odds.replace(result[0], x);
        double error = ((double) m - (double) o) / ((double) m);
        System.out.println("Tournament Winner Odds to actually win (according to this simulator): "
            + (odds.get(result[0]) * 100 * error * error * error * error) + "%");
    }
}
