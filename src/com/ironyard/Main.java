package com.ironyard;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Spark.init();

        Spark.get(
                "/",
                (request, response) -> {
                    String address = request.queryParams("address");
                    HashMap m = new HashMap();
                    if (address != null) {
                        Document doc = Jsoup.connect(address).get();
                        Elements images = doc.select("img");
                        ArrayList<String> html = new ArrayList<String>();
                        for (Element e : images) {
                            e.attr("src", e.absUrl("src"));
                            html.add(String.format("<a href='%s'>%s</a>", e.attr("src"), e.toString()));
                        }
                        m.put("images", html);
                    }
                   return new ModelAndView(m, "home.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/scrape" ,
                (request, response) -> {
                    String address = request.queryParams("address");
                    if (!address.startsWith("http")) {
                        address = "http://" + address;
                    }
                    response.redirect("/?address=" + address);
                    return "";
                }
        );
    }
}
