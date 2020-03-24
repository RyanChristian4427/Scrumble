package com.nsa.bt.scrumble.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GitLabLinkParser {

    private static final Logger logger = LoggerFactory.getLogger(GitLabLinkParser.class);

    public GitLabLinkParser(){}

    public GitLabLinks parseLink(String linkHeader)  {
        GitLabLinks gitLabLinks = new GitLabLinks();
        String[] links = linkHeader.split(",");
        for (String link : links) {
            String[] segments = link.split(";");
            if (segments.length < 2)
                continue;

            String linkPart = segments[0].trim();
            if (!linkPart.startsWith("<") || !linkPart.endsWith(">"))
                continue;
            linkPart = linkPart.substring(1, linkPart.length() - 1);
            linkPart = linkPart.replaceAll("http://10.72.98.102", "https://gitlab.ryanchristian.dev");

            for (int i = 1; i < segments.length; i++) {
                String[] rel = segments[i].trim().split("="); //$NON-NLS-1$
                if (rel.length < 2 || !"rel".equals(rel[0]))
                    continue;

                String relValue = rel[1];
                if (relValue.startsWith("\"") && relValue.endsWith("\"")) //$NON-NLS-1$ //$NON-NLS-2$
                    relValue = relValue.substring(1, relValue.length() - 1);

                if ("next".equals(relValue))
                    gitLabLinks.setNext(linkPart);
                else if ("last".equals(relValue))
                    gitLabLinks.setLast(linkPart);
                else if ("first".equals(relValue))
                    gitLabLinks.setFirst(linkPart);
                else if ("prev".equals(relValue))
                    gitLabLinks.setPrev(linkPart);
            }
        }
        return gitLabLinks;
    }
}