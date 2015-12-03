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

import java.util.List;

/**
 * Represents a link in the table of contents, this object can have nested child
 * links
 *
 * @author OpenCollab
 */
public class TocLink {

    /**
     * The href
     */
    private String href;

    /**
     * The displayed text
     */
    private String title;

    /**
     * Can be set for accessibility if the pronunciation of a link heading may
     * be ambiguous due to embedded images, math content, or other content
     * without intrinsic text. Will be included as an 'title' attribute
     */
    private String altTitle;

    /**
     * Any nested links
     */
    private List<TocLink> tocChildLinks;

    public TocLink(String href, String title, String altTitle) {
        this.href = href;
        this.title = title;
        this.altTitle = altTitle;
    }

    /**
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public void setHref(String href) {
        this.href = href;
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
     * @return the altTitle
     */
    public String getAltTitle() {
        return altTitle;
    }

    /**
     * @param altTitle the altTitle to set
     */
    public void setAltTitle(String altTitle) {
        this.altTitle = altTitle;
    }

    /**
     * @return the tocChildLinks
     */
    public List<TocLink> getTocChildLinks() {
        return tocChildLinks;
    }

    /**
     * @param tocChildLinks the tocChildLinks to set
     */
    public void setTocChildLinks(List<TocLink> tocChildLinks) {
        this.tocChildLinks = tocChildLinks;
    }
}
