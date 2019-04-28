package com.rep.spider;

import com.rep.html.HtmlManage;
import com.rep.utils.FileDownload;
import com.rep.utils.HttpGetConnect;
import net.sf.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author xupenggao
 * @Date 22:14 2019/4/24
 * @Description:
 **/
public class SpiderKugou {

    public static String filePath = "E:/KuGou/";
    public static String mp3 = "https://wwwapi.kugou.com/yy/index.php?r=play/getdata&callback=jQuery19106959134040591868_1556372858229&"
            +"hash=HASH&album_id=0&_=TIME";
    public static String link = "https://www.kugou.com/yy/rank/home/PAGE-8888.html?from=rank";

    public static void main(String[] args) throws IOException {
//        FileDownload.download("http://fs.w.kugou.com/201904282243/66394816e29cca6e228e41c5d0e2e12d/G135/M06/17/13/xw0DAFtHOJOACZiNAEUe3eVFTx8339.mp3",filePath+"/于文文 - 体面.mp3");
        System.out.println("--------------------------------------start spider task------------------------------------");
/*        for (int i = 1;i < 23; i++){
            String url = link.replace("PAGE", i+"");
            getTitle(url);
        }*/
        getTitle("https://www.kugou.com/yy/rank/home/2-8888.html?from=rank");
        System.out.println("-------------------------------------下载完成---------------------------------");
    }

    public static void getTitle(String url) throws IOException {
        HttpGetConnect connect = new HttpGetConnect();
        String content = connect.connect(url, "UTF-8");
        HtmlManage html = new HtmlManage();
        Document doc = html.manage(content);
        Element ele = doc.getElementsByClass("pc_temp_songlist").get(0);
        Elements eles = ele.getElementsByTag("li");
        for (int a = 0 ; a < eles.size(); a++){
            Element item = eles.get(a);
            String title = item.attr("title").trim();
            String link = item.getElementsByTag("a").first().attr("href");
            download(link, title);
        }
    }

    public static String download(String url, String name) throws IOException {
        String hash = "";
        HttpGetConnect connect = new HttpGetConnect();
        String content = connect.connect(url, "UTF-8");

        String regx = "\"hash\":\"[0-9A-Z]+\"";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()){
            hash = matcher.group();
            hash = hash.replace("\"hash\":\"","");
            hash = hash.replace("\"","");
        }
        String item = mp3.replace("HASH",hash);
        item = item.replace("TIME",System.currentTimeMillis()+"");
//        System.out.println("item ===>>"+item);

        String mp = connect.connect(item,"UTF-8");
        mp = mp.substring(mp.indexOf("(")+1,mp.length()-3);
        JSONObject jsonObject = JSONObject.fromObject(mp);
        String playUrl = jsonObject.getJSONObject("data").getString("play_url");
        if (playUrl!=null && !"".equals(playUrl)) {
            FileDownload.download(playUrl, filePath + name + ".mp3");
            System.out.println("-------------------------------------(" + name + ")下载完成---------------------------------");
        }else {
            System.out.println("--------------------------------(" + name + ")为付费歌曲,下载失败--------------------------------");
            return null;
        }
        return playUrl;
    }
}
