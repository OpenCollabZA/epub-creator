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
package coza.opencollab.epub.creator.model;

import coza.opencollab.epub.creator.EpubConstants;
import coza.opencollab.epub.creator.util.EpubWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Representation of an EPUB book
 *
 * @author OpenCollab
 */
public class EpubBook {

    /**
     * A list of the the content files, this includes the cover, HTML pages, CSS
     * JavaScript or any other resource
     */
    private List<Content> contents;
    /**
     * Indicates whether the TOC must be created automatically. If this is false
     * the tocLinks must be set
     */
    private boolean autoToc = true;
    /**
     * The 2 letter language code set in the dc:language meta data
     */
    private String language;
    /**
     * The id used as the meta data dc:identifier
     */
    private String id;
    /**
     * The title of the book
     */
    private String title;
    /**
     * The author, this is set as the meta data dc:creator value
     */
    private String author;
    /**
     * Unique content id that is incremental set on content with no id
     */
    private int contentId = 1;
    /**
     * List of the links that must be added to the TOC, they can be nested
     */
    private List<TocLink> tocLinks;
    /**
     * List of the landmarks to be added to the TOC
     */
    private List<Landmark> landmarks;
    /**
     * Instance of the EpubWriter to write the book to file or stream
     */
    private final EpubWriter epubCreator;

    /**
     * Unique set of href's used to make sure we do not add duplicates
     */
    private final Set uniqueHrefs;

    /**
     * The href can not be repeated in the OPF, thus is we want to add the same
     * content in more than one place we have to create duplicate Content
     * objects but with different href's
     */
    private int hrefUniquePostfix = 1;

    /**
     * Constructs EPUBBook
     */
    public EpubBook() {
        this.epubCreator = new EpubWriter();
        this.contents = new ArrayList<>();
        this.tocLinks = new ArrayList<>();
        this.uniqueHrefs = new HashSet();
    }

    /**
     * Constructs EPUBBook
     *
     * @param language the 2 letter language code set in the dc:language meta
     * data
     * @param id the id used as the meta data dc:identifier
     * @param title the title of the book
     * @param author the author, this is set as the meta data dc:creator value
     */
    public EpubBook(String language, String id, String title, String author) {
        this();
        this.language = language;
        this.id = id;
        this.title = title;
        this.author = author;
    }

    /**
     * Checks if href unique, adds postfix if not
     *
     * @param content
     */
    private void checkHref(Content content) {
        content.setHref(removeLeadingFileSeparator(content.getHref()));
        if (uniqueHrefs.contains(content.getHref())) {
            content.setHref(incrementHref(content.getHref()));
        }
        uniqueHrefs.add(content.getHref());
    }

    /**
     * Checks if href unique, adds postfix if not
     *
     * @param href
     */
    private String checkHref(String href) {
        href = removeLeadingFileSeparator(href);
        if (uniqueHrefs.contains(href)) {
            return incrementHref(href);
        }
        return href;
    }

    /**
     * Increment href. Checks for file extensions
     *
     * @param href
     * @return
     */
    private String incrementHref(String href) {
        if (href.contains(".")) {
            return href.replace(".", "_" + hrefUniquePostfix++ + ".");
        }
        return href + "_" + hrefUniquePostfix++;
    }

    /**
     * Remove the leading file separator
     *
     * @param href
     * @return
     */
    private String removeLeadingFileSeparator(String href) {
        if (href.startsWith("/") || href.startsWith("\\")) {
            return href.substring(1);
        }
        return href;
    }

    /**
     * Adds content to the content list and checks the id. Only adds unique
     * href's
     *
     * @param content the EpubBook content - TOC, pages, files
     * @return boolean indicating if the content has been added
     */
    public boolean addContent(Content content) {
        checkContentId(content);
        checkHref(content);
        contents.add(content);
        return true;
    }

    /**
     * Inserts content at a specific index. Only adds unique href's
     *
     * @param content the EpubBook content - TOC, pages, files
     * @param index index where the content will be inserted
     * @return boolean indicating if the content has been added
     */
    public boolean insertContent(Content content, int index) {
        checkContentId(content);
        checkHref(content);
        contents.add(index, content);
        return true;
    }

    /**
     * Wraps a String in the HTML wrapper and adds create a content object that
     * is added to the content list. Returns null if the href is not unique
     *
     * @param title the title of the page
     * @param href used as unique link
     * @param content text content to be added
     * @return the Content object generated from the text
     */
    public Content addTextContent(String title, String href, String content) {
        href = checkHref(href);
        String contentString = MessageFormat.format(EpubConstants.HTML_WRAPPER, title, content);
        Content textContent = new Content("application/xhtml+xml", href, contentString.getBytes());
        addContent(textContent);
        return textContent;
    }

    /**
     * Creates and adds file Content to the book
     *
     * @param contents the byte array content
     * @param mediaType the mime type
     * @param href used as unique link
     * @param toc flag whether it must be added to the TOC
     * @param spine flag whether it must be added to the spine
     * @return a reference to the newly created Content object
     */
    public Content addContent(byte[] contents, String mediaType, String href, boolean toc, boolean spine) {
        Content content = new Content(mediaType, href, contents);
        content.setToc(toc);
        content.setSpine(spine);
        addContent(content);
        return content;
    }

    /**
     * Creates and adds an InputStream to the book
     *
     * @param contents the InputStream to the content
     * @param mediaType the mime type
     * @param href used as unique link
     * @param toc flag whether it must be added to the TOC
     * @param spine flag whether it must be added to the spine
     * @return a reference to the newly created Content object
     * @throws java.io.IOException if the InputStream can not be converted to
     * byte[]
     */
    public Content addContent(InputStream contents, String mediaType, String href, boolean toc, boolean spine) throws IOException {
        Content content = new Content(mediaType, href, contents);
        content.setToc(toc);
        content.setSpine(spine);
        addContent(content);
        return content;
    }

    /**
     * Checks if Content object has an id and adds an unique if not
     *
     * @param content
     */
    private void checkContentId(Content content) {
        if (content.getId() == null) {
            content.setId(id.replaceAll("[^a-zA-Z0-9\\-]", "_") + "_" + (contentId++));
        }
    }

    /**
     * Adds an file as the cover image
     *
     * @param coverImage the cover image
     * @param mediaType the mime type of the cover image
     * @param href used to name the cover image
     */
    public void addCoverImage(byte[] coverImage, String mediaType, String href) {
        Content cover = new Content(mediaType, href, coverImage);
        cover.setProperties("cover-image");
        cover.setSpine(false);
        addContent(cover);
    }

    /**
     * Creates the EPUB zip container and writes it to the OutputStream
     *
     * @param out the OutputStream
     * @throws Exception if the content can not be zipped and written
     */
    public void writeToStream(OutputStream out) throws Exception {
        epubCreator.writeEpubToStream(this, out);
    }

    /**
     * Creates the EPUB zip container and writes it to the File
     *
     * @param fileName to store as
     * @throws Exception if the content can not be zipped and stored
     */
    public void writeToFile(String fileName) throws Exception {
        epubCreator.writeEpubToFile(this, fileName);
    }

    /**
     * @return the contents
     */
    public List<Content> getContents() {
        return contents;
    }

    /**
     * @param contents the content to set
     */
    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    /**
     * @return the autoToc
     */
    public boolean isAutoToc() {
        return autoToc;
    }

    /**
     * @param autoToc the autoToc to set
     */
    public void setAutoToc(boolean autoToc) {
        this.autoToc = autoToc;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the tocLinks
     */
    public List<TocLink> getTocLinks() {
        return tocLinks;
    }

    /**
     * @param tocLinks the tocLinks to set
     */
    public void setTocLinks(List<TocLink> tocLinks) {
        this.tocLinks = tocLinks;
    }

    /**
     * @return the landmarks
     */
    public List<Landmark> getLandmarks() {
        return landmarks;
    }

    /**
     * @param landmarks the landmarks to set
     */
    public void setLandmarks(List<Landmark> landmarks) {
        this.landmarks = landmarks;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the epubCreator
     */
    public EpubWriter getEpubCreator() {
        return epubCreator;
    }

    /**
     * @return the uniqueHrefs
     */
    public Set getUniqueHrefs() {
        return uniqueHrefs;
    }

}
