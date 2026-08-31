package com.agrilink.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeoController {
    @GetMapping(value="/robots.txt", produces=MediaType.TEXT_PLAIN_VALUE)
    public String robots(HttpServletRequest request) {
        return "User-agent: *\nAllow: /\nDisallow: /api/\nSitemap: " + root(request) + "/sitemap.xml\n";
    }

    @GetMapping(value="/sitemap.xml", produces=MediaType.APPLICATION_XML_VALUE)
    public String sitemap(HttpServletRequest request) {
        String url = xml(root(request) + "/");
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" +
            "<url><loc>" + url + "</loc><changefreq>weekly</changefreq></url>" +
            "</urlset>";
    }

    private String root(HttpServletRequest request) {
        String scheme=request.getScheme();
        int port=request.getServerPort();
        boolean normal=("https".equalsIgnoreCase(scheme)&&port==443)||("http".equalsIgnoreCase(scheme)&&port==80);
        return scheme + "://" + request.getServerName() + (normal?"":":"+port);
    }

    private String xml(String value) {
        return value.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&apos;");
    }
}
