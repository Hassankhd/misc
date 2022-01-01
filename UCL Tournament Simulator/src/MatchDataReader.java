import java.io.File;
import java.util.Set;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * This class fetches the HTML data for team matches from the 2010/11 season 
 * till the most recent matches of the 2021/22 season. We use this data to 
 * train our model.
 * 
 * @author Hassan Kheireddine
 *
 */
public class MatchDataReader {

    private TeamStatReader statsRdr = new TeamStatReader();
    public Hashtable<String, String[]> allStats;
    private String[] files = {"bundesliga.csv", "epl.csv", "eredivisie.csv", "laliga.csv",
        "ligueun.csv", "PrimeiraLiga.csv", "SerieA.csv"};
    private String[] qualified = {"Liverpool", "Ajax", "Bayern Munich", "Manchester Utd", "Chelsea",
        "Juventus", "Manchester City", "Paris S-G", "Real Madrid", "Inter Milan", "Benfica",
        "Sporting CP", "Atletico Madrid", "Lille", "RB Salzburg", "Villarreal"};
    protected List<List<String>> fixturesOfInterest;

    public MatchDataReader() {
        this.fixturesOfInterest = new ArrayList<List<String>>(900000);
        for (int i = 0; i < 900000; i++) {
            fixturesOfInterest.add(new ArrayList<>());
        }
    }

    public List<List<String>> getFixtures() {
        return fixturesOfInterest;
    }

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }

        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static void printSimilarity(String s, String t) {
        System.out.println(String.format("%.3f is the similarity between \"%s\" and \"%s\"",
            similarity(s, t), s, t));
    }



    public boolean fetchFixtures() throws IOException {

        allStats = statsRdr.fetchData();

        try {
            for (String filePath : files) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                String eachLine = "";
                int iteration = 0;
                int year = 2010;
                String home = "";
                String away = "";
                String home_score = "";
                String away_score = "";
                String home_att = "";
                String away_att = "";
                String home_mid = "";
                String away_mid = "";
                String home_def = "";
                String away_def = "";
                String home_ovr = "";
                String away_ovr = "";

                // read each line of the data file
                while ((eachLine = bufferedReader.readLine()) != null) {
                    // split the data and set each field
                    ArrayList<String> currFixture = new ArrayList<String>();
                    String[] line = eachLine.split(",");
                    if (line.length < 7)
                        continue;
                    if (line[0].contains("Wk")) {
                        year++;
                        continue;
                    }

                    if (line[10].equals("Head-to-Head")) {
                        continue;
                    }

                    for (String team : qualified) {
                        if (line[4].equals(team) || line[6].equals(team)) {


                            // add year, home team, away team
                            fixturesOfInterest.get(iteration).add(Integer.toString(year));
                            fixturesOfInterest.get(iteration).add(line[4]);
                            fixturesOfInterest.get(iteration).add(line[6]);

                            String[] score = line[5].split("–");
                            if (score.length == 1)
                                continue;
                            fixturesOfInterest.get(iteration)
                                .add(Integer.toString(Integer.parseInt(score[0].trim())
                                    - Integer.parseInt(score[1].trim())));
                            if (Integer.parseInt(score[0].trim())
                                - Integer.parseInt(score[1].trim()) > 0)
                                fixturesOfInterest.get(iteration).add("1");
                            else
                                fixturesOfInterest.get(iteration).add("0");

                            String homeKey = line[4] + year;

                            Set<String> keys = allStats.keySet();
                            for (String key : keys) {
                                if ((key.toLowerCase()).contains(homeKey.toLowerCase())
                                    || key.contains(homeKey) || (similarity(key, homeKey) > 0.75
                                        && key.contains(Integer.toString(year)))) {
                                    homeKey = key;
                                    // homeKey = homeKey.substring(0, homeKey.length() - 4);
                                    // homeKey = homeKey + year;
                                }
                            }

                            if (line[4].equals("Nürnberg") && (year == 2013 || year == 2014)) {
                                homeKey = "1. FC Nuremberg" + year;
                            }
                            if (line[4].equals("M'Gladbach") && (year == 2013 || year == 2014
                                || year == 2015 || year == 2016 || year == 2017 || year == 2018
                                || year == 2019 || year == 2020 || year == 2021)) {
                                homeKey = "Borussia Mönchengladbach" + year;
                            }
                            if (line[4].equals("Hertha BSC") && (year == 2014 || year == 2015
                                || year == 2016 || year == 2017 || year == 2018)) {
                                homeKey = "Hertha BSC Berlin" + year;
                            }
                            if (line[4].equals("Hertha BSC") && (year == 2021)) {
                                homeKey = "Hertha Berlin" + year;
                            }
                            if (line[4].equals("Arminia") && (year == 2021)) {
                                homeKey = "DSC Arminia Bielefeld" + year;
                            }
                            if (line[4].equals("Arminia") && (year == 2022)) {
                                homeKey = "Arminia Bielefeld" + year;
                            }
                            if (line[4].equals("Bayern Munich")
                                && (year == 2019 || year == 2020 || year == 2021)) {
                                homeKey = "FC Bayern München" + year;
                            }
                            if (line[4].equals("West Brom") && (year < 2023)) {
                                homeKey = "West Bromwich Albion" + year;
                            }
                            if (line[4].equals("Tottenham") && (year < 2023)) {
                                homeKey = "Tottenham Hotspur" + year;
                            }
                            if (line[4].equals("Manchester City") && (year < 2023)) {
                                homeKey = "Manchester City" + year;
                            }
                            if (line[4].equals("Manchester Utd") && (year < 2023)) {
                                homeKey = "Manchester United" + year;
                            }
                            if (line[4].equals("West Ham") && (year < 2023)) {
                                homeKey = "West Ham United" + year;
                            }
                            if (line[4].equals("Wolves") && (year < 2023)) {
                                homeKey = "Wolverhampton Wanderers" + year;
                            }
                            if (line[4].equals("Blackburn") && (year < 2023)) {
                                homeKey = "Blackburn Rovers" + year;
                            }
                            if (line[4].equals("Bolton") && (year == 2011 || year == 2012)) {
                                homeKey = "Bolton Wanderers" + year;
                            }
                            if (line[4].equals("QPR") && (year < 2023)) {
                                homeKey = "Queens Park Rangers" + year;
                            }
                            if (line[4].equals("Brighton") && (year < 2023)) {
                                homeKey = "Brighton & Hove Albion" + year;
                            }
                            if (line[4].equals("PSV Eindhoven") && (year < 2023)) {
                                homeKey = "PSV" + year;
                            }
                            if (line[4].equals("NEC Nijmegen") && (year < 2023)) {
                                homeKey = "N.E.C." + year;
                            }
                            if (line[4].equals("AZ Alkmaar") && (year >= 2013)) {
                                homeKey = "AZ" + year;
                            }
                            if (line[4].equals("Roda JC") && (year >= 2013)) {
                                homeKey = "Roda JC Kerkrade" + year;
                            }
                            if (line[4].equals("Sparta R'dam") && (year >= 2013)) {
                                homeKey = "Sparta Rotterdam" + year;
                            }

                            // la liga 2011, 2012
                            if (line[4].equals("Sevilla") && (year == 2011 || year == 2012)) {
                                homeKey = "Sevilla Fútbol Club S.A.D." + year;
                            }
                            if (line[4].equals("Barcelona") && (year == 2011 || year == 2012)) {
                                homeKey = "F.C. Barcelona" + year;
                            }
                            if (line[4].equals("Real Madrid") && (year < 2016)) {
                                homeKey = "Real Madrid Club de Fútbol" + year;
                            }
                            if (line[4].equals("Atlético Madrid") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    homeKey = "Club Atlético de Madrid S.A.D." + year;
                                else
                                    homeKey = "Club Atlético de Madrid" + year;
                            }
                            if (line[4].equals("Valencia") && year < 2016) {
                                if (year == 2011 || year == 2012)
                                    homeKey = "Valencia Club de Fútbol S.A.D." + year;
                                else
                                    homeKey = "Valencia Club de Fútbol" + year;
                            }

                            if (line[4].equals("Villarreal") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    homeKey = "Villarreal Club de Fútbol S.A.D." + year;
                                else
                                    homeKey = "Villarreal Club de Fútbol" + year;

                            }
                            if (line[4].equals("Athletic Club") && (year < 2023)) {
                                if (year == 2019 || year == 2020 || year == 2021)
                                    homeKey = "Athletic Club" + year;
                                else
                                    homeKey = "Athletic Club de Bilbao" + year;
                            }
                            if (line[4].equals("Getafe") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    homeKey = "Getafe Club de Fútbol S.A.D." + year;
                                else
                                    homeKey = "Getafe Club de Fútbol" + year;
                            }
                            if (line[4].equals("La Coruña") && (year == 2011 || year == 2012)) {
                                homeKey = "Real Club Deportivo de La Coruña S.A.D." + year;
                            }
                            if (line[4].equals("Espanyol") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    homeKey = "R.C.D. Espanyol de Barcelona S.A.D." + year;
                                else
                                    homeKey = "RCD Espanyol de Barcelona" + year;
                            }
                            if (line[4].equals("Sporting Gijón")
                                && (year == 2011 || year == 2012)) {
                                homeKey = "Real Sporting de Gijón S.A.D." + year;
                            }
                            if (line[4].equals("Zaragoza") && (year == 2011 || year == 2012)) {
                                homeKey = "Real Zaragoza S.A.D." + year;
                            }
                            if (line[4].equals("Osasuna") && (year < 2016)) {
                                homeKey = "Club Atlético Osasuna" + year;
                            }
                            if (line[4].equals("Mallorca") && (year == 2011 || year == 2012)) {
                                homeKey = "Real Club Deportivo Mallorca S.A.D." + year;
                            }
                            if (line[4].equals("Almería") && (year == 2011 || year == 2012)) {
                                homeKey = "Unión Deportiva Almería S.A.D." + year;
                            }
                            if (line[4].equals("Racing Sant") && (year == 2011 || year == 2012)) {
                                homeKey = "Real Racing Club S.A.D." + year;
                            }
                            if (line[4].equals("Hércules") && (year == 2011 || year == 2012)) {
                                homeKey = "Hércules Club de Fútbol S.A.D." + year;
                            }
                            if (line[4].equals("Málaga") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    homeKey = "Málaga Club de Fútbol S.A.D." + year;
                                else
                                    homeKey = "Málaga Club de Fútbol" + year;
                            }
                            if (line[4].equals("Real Sociedad") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    homeKey = "Real Sociedad de Fútbol S.A.D." + year;
                                else
                                    homeKey = "Real Sociedad de Fútbol" + year;
                            }
                            if (line[4].equals("Levante") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    homeKey = "Levante Unión Deportiva S.A.D." + year;
                                else
                                    homeKey = "Levante Unión Deportiva" + year;
                            }
                            if (line[4].equals("Rayo Vallecano") && (year < 2016)) {
                                if (year == 2012)
                                    homeKey = "Rayo Vallecano de Madrid S.A.D." + year;
                                else
                                    homeKey = "Rayo Vallecano de Madrid" + year;
                            }
                            if (line[4].equals("Real Zaragoza") && (year == 2012)) {
                                homeKey = "Real Zaragoza S.A.D." + year;
                            }
                            if (line[4].equals("Betis") && (year < 2023)) {
                                if (year == 2012)
                                    homeKey = "Real Betis Balompié S.A.D." + year;
                                else if (year == 2019 || year == 2020)
                                    homeKey = "Real Betis" + year;
                                else
                                    homeKey = "Real Betis Balompié" + year;
                            }
                            if (line[4].equals("Granada") && (year < 2016)) {
                                homeKey = "Granada Club de Fútbol" + year;
                            }
                            if (line[4].equals("Celta Vigo") && (year < 2016)) {
                                homeKey = "Real Club Celta de Vigo" + year;
                            }
                            if (line[4].equals("Celta Vigo") && (year >= 2016)) {
                                if (year == 2019 || year == 2020 || year == 2021)
                                    homeKey = "RC Celta" + year;
                                else
                                    homeKey = "RC Celta de Vigo" + year;
                            }
                            if (line[4].equals("Valladolid") && (year < 2016)) {
                                homeKey = "Real Valladolid Club de Fútbol" + year;
                            }
                            if (line[4].equals("Valladolid") && (year >= 2019)) {
                                homeKey = "R. Valladolid CF" + year;
                            }
                            if (line[4].equals("Sevilla") && (year >= 2013 && year < 2016)) {
                                homeKey = "Sevilla Fútbol Club" + year;
                            }
                            if (line[4].equals("Elche") && (year < 2016)) {
                                homeKey = "Elche Club de Fútbol" + year;
                            }
                            if (line[4].equals("Sporting Gijón") && (year >= 2013 && year < 2023)) {
                                homeKey = "Real Sporting de Gijón" + year;
                            }
                            if (line[4].equals("Cádiz") && (year < 2023)) {
                                homeKey = "Cádiz CF" + year;
                            }
                            if (line[4].equals("Elche") && (year == 2021 || year == 2022)) {
                                homeKey = "Elche CF" + year;
                            }
                            if (line[4].equals("Paris S-G")) {
                                homeKey = "Paris Saint-Germain" + year;
                            }
                            if (line[4].equals("Saint-Étienne")) {
                                if (year == 2011)
                                    homeKey = "A.S. Saint-Etienne" + year;
                                else if (year < 2016)
                                    homeKey = "AS Saint-Etienne" + year;
                            }
                            if (line[4].equals("Lille") && year < 2013) {
                                homeKey = "LOSC Lille Métropole" + year;
                            }
                            if (line[4].equals("Rennes")) {
                                if (year < 2022)
                                    homeKey = "Stade Rennais FC" + year;
                                else
                                    homeKey = "Stade Rennais" + year;
                            }
                            if (line[4].equals("Sochaux")) {
                                homeKey = "FC Sochaux-Montbéliard" + year;
                            }
                            if (line[4].equals("Montpellier")) {
                                if (year < 2016)
                                    homeKey = "Montpellier Hérault Sport Club" + year;
                                else if (year < 2022)
                                    homeKey = "Montpellier Hérault SC" + year;
                            }
                            if (line[4].equals("Lyon")) {
                                homeKey = "Olympique Lyonnais" + year;
                            }
                            if (line[4].equals("Brest")) {
                                homeKey = "Stade Brestois 29" + year;
                            }
                            if (line[4].equals("Lorient") && year < 2016) {
                                homeKey = "FC Lorient Bretagne Sud" + year;
                            }
                            if (line[4].equals("Monaco") && (year < 2016)) {
                                homeKey = "AS Monaco FC" + year;
                            }
                            if (line[4].equals("Monaco") && (year == 2019 || year == 2020)) {
                                homeKey = "AS Monaco Football Club SA" + year;
                            }
                            if (line[4].equals("Evian")) {
                                homeKey = "Evian Thonon Gaillard FC" + year;
                            }
                            if (line[4].equals("Dijon")) {
                                homeKey = "Dijon FCO" + year;
                            }
                            if (line[4].equals("Angers")) {
                                homeKey = "Angers SCO" + year;
                            }
                            if (line[4].equals("Troyes")) {
                                homeKey = "ES Troyes AC" + year;
                                if (year == 2022)
                                    homeKey = "ESTAC Troyes" + year;
                            }
                            if (line[4].equals("Gazélec Ajaccio")) {
                                homeKey = "GFC Ajaccio" + year;
                            }
                            if (line[4].equals("Amiens") && (year < 2019)) {
                                homeKey = "Amiens SC Football" + year;
                            }
                            if (line[4].equals("Nîmes")) {
                                homeKey = "Nîmes Olympique" + year;
                            }
                            if (line[4].equals("Toulouse") && year >= 2019) {
                                homeKey = "Toulouse Football Club" + year;
                            }
                            if (line[4].equals("Strasbourg")) {
                                if (year == 2018 || year == 2022)
                                    homeKey = "RC Strasbourg" + year;
                                else
                                    homeKey = "RC Strasbourg Alsace" + year;
                            }
                            if (line[4].equals("Nancy") && (year < 2023)) {
                                if (year == 2011)
                                    homeKey = "A.S. Nancy Lorraine" + year;
                                else if (year < 2017)
                                    homeKey = "AS Nancy Lorraine" + year;
                                else
                                    homeKey = "AS Nancy-Lorraine" + year;
                            }
                            if (line[4].equals("Académica")) {
                                if (year < 2016)
                                    homeKey = "Académica Coimbra" + year;
                                else
                                    homeKey = "Académica de Coimbra" + year;
                            }
                            if (line[4].equals("Marítimo")) {
                                if (year < 2015)
                                    homeKey = "C. Funchal" + year;
                                else if (year < 2018)
                                    homeKey = "CD Nacional" + year;
                            }
                            if (line[4].equals("Naval")) {
                                homeKey = "Naval 1° de Maio" + year;
                            }
                            if (line[4].equals("Vitória")) {
                                if (year < 2016 || year == 2022)
                                    homeKey = "Vitória de Guimarães" + year;
                                else
                                    homeKey = "Vitória Guimarães" + year;
                            }
                            if (line[4].equals("Vitória Setúbal")) {
                                if (year < 2016)
                                    homeKey = "Vitória de Setúbal" + year;
                                else
                                    homeKey = "Vitória Setúbal" + year;
                            }
                            if (line[4].equals("Beira-Mar")) {
                                homeKey = "SC Beira Mar" + year;
                            }
                            if (line[4].equals("Portimonense") && year < 2019) {
                                homeKey = "Portimão" + year;
                            }
                            if (line[4].equals("Gil Vicente")) {
                                if (year < 2015)
                                    homeKey = "V. Barcelos" + year;
                            }
                            if (line[4].equals("Feirense") && year < 2019) {
                                homeKey = "F. Santa Maria da Feira" + year;
                            }
                            if (line[4].equals("Tondela") && year < 2019) {
                                homeKey = "Tondela" + year;
                            }
                            if (line[4].equals("Moreirense")) {
                                if (year < 2015)
                                    homeKey = "Moreira de Cónegos" + year;
                            }
                            if (line[4].equals("Estoril")) {
                                if (year == 2015)
                                    homeKey = "GD Estoril-Praia" + year;
                                else if (year > 2015 && year < 2022)
                                    homeKey = "Estoril Praia" + year;
                            }
                            if (line[4].equals("Belenenses")) {
                                if (year == 2014)
                                    homeKey = "Belém" + year;
                                else if (year < 2016)
                                    homeKey = "C.F. Os Belenenses" + year;
                            }
                            if (line[4].equals("Chievo")) {
                                homeKey = "Chievo Verona" + year;
                            }
                            if (line[4].equals("Atalanta") && year == 2022) {
                                homeKey = "Bergamo Calcio" + year;
                            }
                            if (line[4].equals("Lazio") && year == 2022) {
                                homeKey = "Latium" + year;
                            }


                            System.out.println(homeKey);
                            String[] homeStats = allStats.get(homeKey);

                            String awayKey = line[6] + year;

                            for (String key : keys) {
                                if ((key.toLowerCase()).contains(awayKey.toLowerCase())
                                    || key.contains(awayKey) || (similarity(key, awayKey) > 0.75
                                        && key.contains(Integer.toString(year)))) {
                                    awayKey = key;
                                    // awayKey = awayKey.substring(0, awayKey.length() - 4);
                                    // awayKey = awayKey + year;
                                }
                            }

                            if (line[6].equals("Nürnberg") && (year == 2013 || year == 2014)) {
                                awayKey = "1. FC Nuremberg" + year;
                            }
                            if (line[6].equals("Nürnberg") && (year == 2013 || year == 2014)) {
                                awayKey = "1. FC Nuremberg" + year;
                            }
                            if (line[6].equals("M'Gladbach") && (year == 2013 || year == 2014
                                || year == 2015 || year == 2016 || year == 2017 || year == 2018
                                || year == 2019 || year == 2020 || year == 2021)) {
                                awayKey = "Borussia Mönchengladbach" + year;
                            }
                            if (line[6].equals("Hertha BSC") && (year == 2014 || year == 2015
                                || year == 2016 || year == 2017 || year == 2018)) {
                                awayKey = "Hertha BSC Berlin" + year;
                            }
                            if (line[6].equals("Hertha BSC") && (year == 2021)) {
                                awayKey = "Hertha Berlin" + year;
                            }
                            if (line[6].equals("Arminia") && (year == 2021)) {
                                awayKey = "DSC Arminia Bielefeld" + year;
                            }
                            if (line[6].equals("Arminia") && (year == 2022)) {
                                awayKey = "Arminia Bielefeld" + year;
                            }
                            if (line[6].equals("Bayern Munich")
                                && (year == 2019 || year == 2020 || year == 2021)) {
                                awayKey = "FC Bayern München" + year;
                            }
                            if (line[6].equals("West Brom") && (year < 2023)) {
                                awayKey = "West Bromwich Albion" + year;
                            }
                            if (line[6].equals("Tottenham") && (year < 2023)) {
                                awayKey = "Tottenham Hotspur" + year;
                            }
                            if (line[6].equals("Manchester City") && (year < 2023)) {
                                awayKey = "Manchester City" + year;
                            }
                            if (line[6].equals("Manchester Utd") && (year < 2023)) {
                                awayKey = "Manchester United" + year;
                            }
                            if (line[6].equals("Blackburn") && (year < 2023)) {
                                awayKey = "Blackburn Rovers" + year;
                            }
                            if (line[6].equals("West Ham") && (year < 2023)) {
                                awayKey = "West Ham United" + year;
                            }
                            if (line[6].equals("Wolves") && (year < 2023)) {
                                awayKey = "Wolverhampton Wanderers" + year;
                            }
                            if (line[6].equals("Blackburn") && (year < 2023)) {
                                awayKey = "Blackburn Rovers" + year;
                            }
                            if (line[6].equals("Bolton") && (year == 2011 || year == 2012)) {
                                awayKey = "Bolton Wanderers" + year;
                            }
                            if (line[6].equals("QPR") && (year < 2023)) {
                                awayKey = "Queens Park Rangers" + year;
                            }
                            if (line[6].equals("Brighton") && (year < 2023)) {
                                awayKey = "Brighton & Hove Albion" + year;
                            }
                            if (line[6].equals("PSV Eindhoven") && (year < 2023)) {
                                awayKey = "PSV" + year;
                            }
                            if (line[6].equals("NEC Nijmegen") && (year < 2022)) {
                                awayKey = "N.E.C." + year;
                            }
                            if (line[6].equals("AZ Alkmaar") && (year >= 2013)) {
                                awayKey = "AZ" + year;
                            }
                            if (line[6].equals("Roda JC") && (year >= 2013)) {
                                awayKey = "Roda JC Kerkrade" + year;
                            }
                            if (line[6].equals("Sparta R'dam") && (year >= 2013)) {
                                awayKey = "Sparta Rotterdam" + year;
                            }
                            // la liga 2011, 2012
                            if (line[6].equals("Sevilla") && (year == 2011 || year == 2012)) {
                                awayKey = "Sevilla Fútbol Club S.A.D." + year;
                            }
                            if (line[6].equals("Barcelona") && (year == 2011 || year == 2012)) {
                                awayKey = "F.C. Barcelona" + year;
                            }
                            if (line[6].equals("Real Madrid") && (year < 2016)) {
                                awayKey = "Real Madrid Club de Fútbol" + year;
                            }
                            if (line[6].equals("Atlético Madrid") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    awayKey = "Club Atlético de Madrid S.A.D." + year;
                                else
                                    awayKey = "Club Atlético de Madrid" + year;
                            }
                            if (line[6].equals("Valencia") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    awayKey = "Valencia Club de Fútbol S.A.D." + year;
                                else
                                    awayKey = "Valencia Club de Fútbol" + year;
                            }
                            if (line[6].equals("Villarreal") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    awayKey = "Villarreal Club de Fútbol S.A.D." + year;
                                else
                                    awayKey = "Villarreal Club de Fútbol" + year;

                            }
                            if (line[6].equals("Athletic Club") && (year < 2023)) {
                                if (year == 2019 || year == 2020 || year == 2021)
                                    awayKey = "Athletic Club" + year;
                                else
                                    awayKey = "Athletic Club de Bilbao" + year;
                            }
                            if (line[6].equals("Getafe") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    awayKey = "Getafe Club de Fútbol S.A.D." + year;
                                else
                                    awayKey = "Getafe Club de Fútbol" + year;
                            }
                            if (line[6].equals("La Coruña") && (year == 2011 || year == 2012)) {
                                awayKey = "Real Club Deportivo de La Coruña S.A.D." + year;
                            }
                            if (line[6].equals("Espanyol") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    awayKey = "R.C.D. Espanyol de Barcelona S.A.D." + year;
                                else
                                    awayKey = "RCD Espanyol de Barcelona" + year;
                            }
                            if (line[6].equals("Sporting Gijón")
                                && (year == 2011 || year == 2012)) {
                                awayKey = "Real Sporting de Gijón S.A.D." + year;
                            }
                            if (line[6].equals("Zaragoza") && (year == 2011 || year == 2012)) {
                                awayKey = "Real Zaragoza S.A.D." + year;
                            }
                            if (line[6].equals("Osasuna") && (year < 2016)) {
                                awayKey = "Club Atlético Osasuna" + year;
                            }
                            if (line[6].equals("Mallorca") && (year == 2011 || year == 2012)) {
                                awayKey = "Real Club Deportivo Mallorca S.A.D." + year;
                            }
                            if (line[6].equals("Almería") && (year == 2011 || year == 2012)) {
                                awayKey = "Unión Deportiva Almería S.A.D." + year;
                            }
                            if (line[6].equals("Racing Sant") && (year == 2011 || year == 2012)) {
                                awayKey = "Real Racing Club S.A.D." + year;
                            }
                            if (line[6].equals("Hércules") && (year == 2011 || year == 2012)) {
                                awayKey = "Hércules Club de Fútbol S.A.D." + year;
                            }
                            if (line[6].equals("Málaga") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    awayKey = "Málaga Club de Fútbol S.A.D." + year;
                                else
                                    awayKey = "Málaga Club de Fútbol" + year;
                            }
                            if (line[6].equals("Real Sociedad") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    awayKey = "Real Sociedad de Fútbol S.A.D." + year;
                                else
                                    awayKey = "Real Sociedad de Fútbol" + year;
                            }
                            if (line[6].equals("Levante") && (year < 2016)) {
                                if (year == 2011 || year == 2012)
                                    awayKey = "Levante Unión Deportiva S.A.D." + year;
                                else
                                    awayKey = "Levante Unión Deportiva" + year;
                            }
                            if (line[6].equals("Rayo Vallecano") && (year < 2016)) {
                                if (year == 2012)
                                    awayKey = "Rayo Vallecano de Madrid S.A.D." + year;
                                else
                                    awayKey = "Rayo Vallecano de Madrid" + year;
                            }
                            if (line[6].equals("Real Zaragoza") && (year == 2012)) {
                                awayKey = "Real Zaragoza S.A.D." + year;
                            }
                            if (line[6].equals("Betis") && (year < 2023)) {
                                if (year == 2012)
                                    awayKey = "Real Betis Balompié S.A.D." + year;
                                else if (year == 2019 || year == 2020)
                                    awayKey = "Real Betis" + year;
                                else
                                    awayKey = "Real Betis Balompié" + year;
                            }
                            if (line[6].equals("Granada") && (year < 2016)) {
                                awayKey = "Granada Club de Fútbol" + year;
                            }
                            if (line[6].equals("Sevilla") && (year >= 2013 && year < 2016)) {
                                awayKey = "Sevilla Fútbol Club" + year;
                            }
                            if (line[6].equals("Celta Vigo") && (year < 2016)) {
                                awayKey = "Real Club Celta de Vigo" + year;
                            }
                            if (line[6].equals("Celta Vigo") && (year >= 2016)) {
                                if (year == 2019 || year == 2020 || year == 2021)
                                    awayKey = "RC Celta" + year;
                                else
                                    awayKey = "RC Celta de Vigo" + year;
                            }
                            if (line[6].equals("Valladolid") && (year < 2016)) {
                                awayKey = "Real Valladolid Club de Fútbol" + year;
                            }
                            if (line[6].equals("Valladolid") && (year >= 2019)) {
                                awayKey = "R. Valladolid CF" + year;
                            }
                            if (line[6].equals("Elche") && (year < 2016)) {
                                awayKey = "Elche Club de Fútbol" + year;
                            }
                            if (line[6].equals("Sporting Gijón") && (year >= 2013 && year < 2023)) {
                                awayKey = "Real Sporting de Gijón" + year;
                            }
                            if (line[6].equals("Cádiz") && (year < 2023)) {
                                awayKey = "Cádiz CF" + year;
                            }
                            if (line[6].equals("Elche") && (year == 2021 || year == 2022)) {
                                awayKey = "Elche CF" + year;
                            }
                            if (line[6].equals("Paris S-G")) {
                                awayKey = "Paris Saint-Germain" + year;
                            }
                            if (line[6].equals("Saint-Étienne")) {
                                if (year == 2011)
                                    awayKey = "A.S. Saint-Etienne" + year;
                                else if (year < 2016)
                                    awayKey = "AS Saint-Etienne" + year;
                            }
                            if (line[6].equals("Lille") && year < 2013) {
                                awayKey = "LOSC Lille Métropole" + year;
                            }
                            if (line[6].equals("Rennes")) {
                                if (year < 2022)
                                    awayKey = "Stade Rennais FC" + year;
                                else
                                    awayKey = "Stade Rennais" + year;
                            }
                            if (line[6].equals("Sochaux")) {
                                awayKey = "FC Sochaux-Montbéliard" + year;
                            }
                            if (line[6].equals("Montpellier")) {
                                if (year < 2016)
                                    awayKey = "Montpellier Hérault Sport Club" + year;
                                else if (year < 2022)
                                    awayKey = "Montpellier Hérault SC" + year;
                            }
                            if (line[6].equals("Lyon")) {
                                awayKey = "Olympique Lyonnais" + year;
                            }
                            if (line[6].equals("Brest")) {
                                awayKey = "Stade Brestois 29" + year;
                            }
                            if (line[6].equals("Lorient") && year < 2016) {
                                awayKey = "FC Lorient Bretagne Sud" + year;
                            }
                            if (line[6].equals("Monaco") && (year < 2016)) {
                                awayKey = "AS Monaco FC" + year;
                            }
                            if (line[6].equals("Monaco") && (year == 2019 || year == 2020)) {
                                awayKey = "AS Monaco Football Club SA" + year;
                            }
                            if (line[6].equals("Evian")) {
                                awayKey = "Evian Thonon Gaillard FC" + year;
                            }
                            if (line[6].equals("Dijon")) {
                                awayKey = "Dijon FCO" + year;
                            }
                            if (line[6].equals("Troyes")) {
                                awayKey = "ES Troyes AC" + year;
                                if (year == 2022)
                                    awayKey = "ES Troyes" + year;
                            }
                            if (line[6].equals("Gazélec Ajaccio")) {
                                awayKey = "GFC Ajaccio" + year;
                            }
                            if (line[6].equals("Amiens") && (year < 2019)) {
                                awayKey = "Amiens SC Football" + year;
                            }
                            if (line[6].equals("Strasbourg")) {
                                if (year == 2018 || year == 2022)
                                    awayKey = "RC Strasbourg" + year;
                                else
                                    awayKey = "RC Strasbourg Alsace" + year;
                            }
                            if (line[6].equals("Nîmes")) {
                                awayKey = "Nîmes Olympique" + year;
                            }
                            if (line[6].equals("Toulouse") && year >= 2019) {
                                awayKey = "Toulouse Football Club" + year;
                            }
                            if (line[6].equals("Angers")) {
                                awayKey = "Angers SCO" + year;
                            }
                            if (line[6].equals("Nancy") && (year < 2023)) {
                                if (year == 2011)
                                    awayKey = "A.S. Nancy Lorraine" + year;
                                else if (year < 2017)
                                    awayKey = "AS Nancy Lorraine" + year;
                                else
                                    awayKey = "AS Nancy-Lorraine" + year;
                            }
                            if (line[6].equals("Académica")) {
                                if (year < 2016)
                                    awayKey = "Académica Coimbra" + year;
                                else
                                    awayKey = "Académica de Coimbra" + year;
                            }
                            if (line[6].equals("Marítimo")) {
                                if (year < 2015)
                                    awayKey = "C. Funchal" + year;
                                else if (year < 2018)
                                    awayKey = "CD Nacional" + year;
                            }
                            if (line[6].equals("Naval")) {
                                awayKey = "Naval 1° de Maio" + year;
                            }
                            if (line[6].equals("Vitória")) {
                                if (year < 2016 || year == 2022)
                                    awayKey = "Vitória de Guimarães" + year;
                                else
                                    awayKey = "Vitória Guimarães" + year;
                            }
                            if (line[6].equals("Vitória Setúbal")) {
                                if (year < 2016)
                                    awayKey = "Vitória de Setúbal" + year;
                                else
                                    awayKey = "Vitória Setúbal" + year;
                            }
                            if (line[6].equals("Beira-Mar")) {
                                awayKey = "SC Beira Mar" + year;
                            }
                            if (line[6].equals("Portimonense") && year < 2019) {
                                awayKey = "Portimão" + year;
                            }
                            if (line[6].equals("Gil Vicente")) {
                                if (year < 2015)
                                    awayKey = "V. Barcelos" + year;
                            }
                            if (line[6].equals("Feirense") && year < 2019) {
                                awayKey = "F. Santa Maria da Feira" + year;
                            }
                            if (line[6].equals("Tondela") && year < 2019) {
                                awayKey = "Tondela" + year;
                            }
                            if (line[6].equals("Moreirense")) {
                                if (year < 2015)
                                    awayKey = "Moreira de Cónegos" + year;
                            }
                            if (line[6].equals("Estoril")) {
                                if (year == 2015)
                                    awayKey = "GD Estoril-Praia" + year;
                                else if (year > 2015 && year < 2022)
                                    awayKey = "Estoril Praia" + year;
                            }
                            if (line[6].equals("Belenenses")) {
                                if (year == 2014)
                                    awayKey = "Belém" + year;
                                else if (year < 2016)
                                    awayKey = "C.F. Os Belenenses" + year;
                            }
                            if (line[6].equals("Chievo")) {
                                awayKey = "Chievo Verona" + year;
                            }
                            if (line[6].equals("Chievo")) {
                                awayKey = "Chievo Verona" + year;
                            }
                            if (line[6].equals("Atalanta") && year == 2022) {
                                awayKey = "Bergamo Calcio" + year;
                            }
                            if (line[6].equals("Lazio") && year == 2022) {
                                awayKey = "Latium" + year;
                            }



                            System.out.println(awayKey);
                            String[] awayStats = allStats.get(awayKey);

                            // in the FIFA video game, attackers are frequently wrongfully classified as midfielders, leading to overpowered ATT vals
                            
                            fixturesOfInterest.get(iteration)
                                .add(Double.toString(((Double.parseDouble(homeStats[2])*0.3 + Double.parseDouble(homeStats[3])*0.7)
                                    - ((Double.parseDouble(awayStats[3])
                                        + Double.parseDouble(awayStats[4])) / 2.0))));

                            fixturesOfInterest.get(iteration)
                                .add(Double.toString(((Double.parseDouble(awayStats[2])*0.3 + Double.parseDouble(awayStats[3])*0.7)
                                    - ((Double.parseDouble(homeStats[3])
                                        + Double.parseDouble(homeStats[4])) / 2.0))));

                            fixturesOfInterest.get(iteration)
                                .add(Double.toString((Double.parseDouble(homeStats[3])
                                    - ((Double.parseDouble(awayStats[2])
                                        + Double.parseDouble(awayStats[4])) / 2.0))));

                            fixturesOfInterest.get(iteration)
                                .add(Double.toString((Double.parseDouble(awayStats[3])
                                    - ((Double.parseDouble(homeStats[2])
                                        + Double.parseDouble(homeStats[4])) / 2.0))));

                            fixturesOfInterest.get(iteration)
                                .add(Double.toString((Double.parseDouble(homeStats[4])
                                    - ((Double.parseDouble(awayStats[3])
                                        + Double.parseDouble(awayStats[2])) / 2.0))));

                            fixturesOfInterest.get(iteration)
                                .add(Double.toString((Double.parseDouble(awayStats[4])
                                    - ((Double.parseDouble(homeStats[3])
                                        + Double.parseDouble(homeStats[2])) / 2.0))));

                            iteration++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // if any exception, print the trace
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        // fetchEPLFixtures();
        MatchDataReader rdr = new MatchDataReader();
        rdr.fetchFixtures();
    }

}
