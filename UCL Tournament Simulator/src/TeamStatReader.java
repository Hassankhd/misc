import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

/**
 * This class fetches the HTML data for team stats on the FIFA video game 
 * from the 2004/05 season till 2021/22. We use this data to train our model
 * 
 * @author Hassan Kheireddine
 *
 */
public class TeamStatReader {

    public Hashtable<String, String[]> FIFAData = new Hashtable<String, String[]>();

    public Hashtable<String, String[]> fetchData() throws IOException {
        Document doc = null;
        Elements body = null;
        String src = "";
        String[][] teamStats;
        String key = "";
        Elements x = null;

        // FIFA 11
        for (int pageNo = 1; pageNo <= 19; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa11_7/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2011";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }

            
        }

        // FIFA 12
        for (int pageNo = 1; pageNo <= 18; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa12_9/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2012";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }

            
        }

        // FIFA 13
        for (int pageNo = 1; pageNo <= 18; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa13_10/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2013";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }

            
        }

        // FIFA 14
        for (int pageNo = 1; pageNo <= 20; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa14_13/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2014";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }

            
        }

        // FIFA 15
        for (int pageNo = 1; pageNo <= 20; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa15_14/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2015";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }

            
        }

        // FIFA 16
        for (int pageNo = 1; pageNo <= 21; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa16_73/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2016";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }

            
        }

        // FIFA 17
        for (int pageNo = 1; pageNo <= 22; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa17_173/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2017";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }

            
        }

        // FIFA 18
        for (int pageNo = 1; pageNo <= 22; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa18_278/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2018";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }

            
        }

        // FIFA 19
        for (int pageNo = 1; pageNo <= 22; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa19_353/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2019";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }

            
        }

        // FIFA 20
        for (int pageNo = 1; pageNo <= 24; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa20_419/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2020";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }

            
        }

        // FIFA 21
        for (int pageNo = 1; pageNo <= 23; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa21_486/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2021";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }
        }

        // FIFA 22
        for (int pageNo = 1; pageNo <= 24; pageNo++) {
            src = "https://www.fifaindex.com/teams/fifa22_499/?page=" + pageNo;
            doc = Jsoup.connect(src).timeout(6000).get();
            body = doc.select("tbody");
            for (Element dat : body)
                x = dat.getElementsByTag("tr");
            teamStats = new String[x.size()][6];
            // add team name, team league, and team stats to teamStats array here
            int cnt = 0;
            for (Element e : body.select("tr")) {
                Elements rawDat = e.getElementsByTag("td");
                for (Element dat : rawDat) {
                    // Name
                    if (dat.attr("data-title").equals("Name"))
                        teamStats[cnt][0] = dat.select("a").text();
                    // League
                    if (dat.attr("data-title").equals("League"))
                        teamStats[cnt][1] = dat.select("a").text();
                    // ATT
                    if (dat.attr("data-title").equals("ATT"))
                        teamStats[cnt][2] = dat.select("span").text();
                    // MID
                    if (dat.attr("data-title").equals("MID"))
                        teamStats[cnt][3] = dat.select("span").text();
                    // DEF
                    if (dat.attr("data-title").equals("DEF"))
                        teamStats[cnt][4] = dat.select("span").text();
                    // OVR
                    if (dat.attr("data-title").equals("OVR"))
                        teamStats[cnt][5] = dat.select("span").text();
                }
             // add to hashtable here
                key = teamStats[cnt][0] + "2022";
                FIFAData.put(key, teamStats[cnt]);
                cnt++;
            }
        }
        return FIFAData;
    }


    public static void main(String[] args) throws IOException {
         TeamStatReader rdr = new TeamStatReader();
         Hashtable<String, String[]> hi = rdr.fetchData();
         System.out.println(hi.toString());
    }



}
