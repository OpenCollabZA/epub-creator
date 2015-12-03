/* Copyright 2014 OpenCollab.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package coza.opencollab.epub.creator.impl;

import coza.opencollab.epub.creator.EpubConstants;
import coza.opencollab.epub.creator.api.TocCreator;
import coza.opencollab.epub.creator.model.Content;
import coza.opencollab.epub.creator.model.EpubBook;
import coza.opencollab.epub.creator.model.Landmark;
import coza.opencollab.epub.creator.model.TocLink;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.Serializer;
import org.htmlcleaner.TagNode;

/**
 * Default implementation of the TocCreator. This follows EPUB3 standards to
 * create the Navigation Document file content.
 *
 * @author OpenCollab
 */
public class TocCreatorDefault implements TocCreator {

    /**
     * HtmlCleaner used to alter the XHTML document
     */
    private final HtmlCleaner cleaner;

    private String href = EpubConstants.TOC_FILE_NAME;

    private String tocHtml = EpubConstants.TOC_XML;

    /**
     * XmlSerializer used to format to XML String output
     */
    private final Serializer htmlSetdown;

    public TocCreatorDefault() {
        cleaner = new HtmlCleaner();
        CleanerProperties htmlProperties = cleaner.getProperties();
        htmlProperties.setOmitHtmlEnvelope(false);
        htmlProperties.setAdvancedXmlEscape(false);
        htmlProperties.setUseEmptyElementTags(true);
        htmlSetdown = new PrettyXmlSerializer(htmlProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content createTocFromBook(EpubBook book) {
        List<TocLink> links = book.getTocLinks();
        if (book.isAutoToc()) {
            links = generateAutoLinks(book);
        }
        List<Landmark> landmarks = book.getLandmarks();
        String tocString = createTocHtml(links, landmarks, getTocHtml());
        Content toc = new Content("application/xhtml+xml", getHref(), tocString.getBytes());
        toc.setProperties("nav");
        toc.setId("toc");
        toc.setLinear(false);
        return toc;
    }

    /**
     * Builds the TOC HTML content from the EpubBook TocLinks
     *
     * @param book the EpubBook
     * @return the TOC HTML String
     */
    private String createTocHtml(List<TocLink> links, List<Landmark> landmarks, String tocHtml) {
        TagNode tagNode = cleaner.clean(tocHtml);
        if (!CollectionUtils.isEmpty(links)) {
            addTocLinks(tagNode, links);
        }
        if (!CollectionUtils.isEmpty(landmarks)) {
            addLandmarks(tagNode, landmarks);
        }
        return htmlSetdown.getAsString(tagNode);
    }

    /**
     * Recursive method adding links and sub links to the TOC Navigation
     * Document
     *
     * @param tagNode
     * @param links
     */
    private void addTocLinks(TagNode tagNode, List<TocLink> links) {
        TagNode navNode = tagNode.findElementByAttValue("epub:type", "toc", true, false);
        TagNode parentNode = navNode.findElementByName("ol", true);
        for (TocLink toc : links) {
            TagNode linkNode = buildLinkNode(toc);
            if (!CollectionUtils.isEmpty(toc.getTocChildLinks())) {
                TagNode olNode = new TagNode("ol");
                addTocLinks(olNode, toc.getTocChildLinks());
                linkNode.addChild(olNode);
            }
            parentNode.addChild(linkNode);
        }
    }

    /**
     * Adds landmarks to the Navigation Document
     *
     * @param tagNode
     * @param links
     */
    private void addLandmarks(TagNode tagNode, List<Landmark> landmarks) {
        TagNode navNode = tagNode.findElementByAttValue("epub:type", "landmarks", true, false);
        TagNode parentNode = navNode.findElementByName("ol", true);
        for (Landmark landmark : landmarks) {
            TagNode landmarkNode = buildLandMarkNode(landmark);
            parentNode.addChild(landmarkNode);
        }
    }

    /**
     * Builds an link tag for the TOC
     *
     * @param content
     * @return
     */
    private TagNode buildLinkNode(TocLink link) {
        TagNode linkNode = new TagNode("li");
        TagNode aNode = new TagNode("a");
        aNode.addAttribute("href", link.getHref());
        aNode.addChild(new ContentNode(link.getTitle()));
        if (link.getAltTitle() != null) {
            aNode.addAttribute("title", link.getHref());
        }
        linkNode.addChild(aNode);
        return linkNode;
    }

    /**
     * Builds an link tag for the TOC landmarks
     *
     * @param content
     * @return
     */
    private TagNode buildLandMarkNode(Landmark landmark) {
        TagNode linkNode = new TagNode("li");
        TagNode aNode = new TagNode("a");
        aNode.addAttribute("href", landmark.getHref());
        aNode.addAttribute("epub:type", landmark.getType());
        aNode.addChild(new ContentNode(landmark.getTitle()));
        linkNode.addChild(aNode);
        return linkNode;
    }

    /**
     * Generates a list of TocLinks for all Content that should be included in
     * the Navigation Document. This will only be used when auto TOC is set
     *
     * @param book
     * @return
     */
    private List<TocLink> generateAutoLinks(EpubBook book) {
        List<TocLink> links = new ArrayList();
        for (Content content : book.getContents()) {
            if (content.isToc()) {
                links.add(new TocLink(content.getHref(), content.getId(), null));
            }
        }
        return links;
    }

    /**
     * @return the HREF
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href the HREF to set
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @return the tocHtml
     */
    public String getTocHtml() {
        return tocHtml;
    }

    /**
     * @param tocHtml the tocHtml to set
     */
    public void setTocHtml(String tocHtml) {
        this.tocHtml = tocHtml;
    }

}
