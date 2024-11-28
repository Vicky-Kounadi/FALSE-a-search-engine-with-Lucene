package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

public class A_Scraper {

    private ArrayList<String> songInfo;
    private String singerN, songN;

    public A_Scraper(String singerName, String songName) {
        singerN = singerName.toLowerCase().replaceAll("[-_ ]", "");
        songN = songName.toLowerCase().replaceAll("[-_ ]", "");
        try {
            // Connect to the website
            Document document = Jsoup.connect("https://www.azlyrics.com/lyrics/" + singerN + "/" + songN + ".html")
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get();

            Elements elements1 = document.select("div.lyricsh > h2 > a > b");
            String singer_name = elements1.toString().replaceAll("<b>", "").replaceAll("</b>", "").replace(" Lyrics",
                    "");
            System.out.println(singer_name);

            Elements elements2 = document.select("div.col-xs-12.col-lg-8.text-center > b");
            String song_name = elements2.toString().replaceAll("\"", "").replaceAll("<b>", "").replaceAll("</b>", "");
            System.out.println(song_name);

            Elements elements3 = document.select("div:nth-child(8)");
            String str3[] = elements3.toString().split("</div>");
            String lyrics = "\"" + str3[0].replace("<div>", "")
                    .replace(
                            "<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->",
                            "")
                    .replaceAll("<br>", "") + "\"";
            System.out.println(lyrics);

            Elements elements4 = document.select("div.songinalbum_title");
            System.out.println(elements4.toString());
            String str4[] = elements4.toString().split("\n");
            String AlbumInfo = str4[1].replaceAll("[\\(\\)\":]", "").replaceAll("<b>", "").replaceAll("</b>", "");
            String Album[] = AlbumInfo.split(" "); // 1 album type, 2 album name, 3 album release year
            System.out.println("Album[0] === " + Album[0]);
            System.out.println("Album[1] === " + Album[1]);
            System.out.println("Album[2] === " + Album[2]);
            songInfo = new ArrayList<>();
            songInfo.add(singer_name);
            songInfo.add(song_name);
            songInfo.add(Album[0]);
            songInfo.add(Album[1]);
            songInfo.add(Album[2]);
            songInfo.add(lyrics);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getSongInfo() {
        return songInfo;
    }

}
