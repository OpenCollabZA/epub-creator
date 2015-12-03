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
package coza.opencollab.epub.creator;

/**
 * Constants used as default values in the EpubCreator and EpubBook. This is set
 * according to the EPUB 3 standards.
 *
 * @author OpenCollab
 */
public class EpubConstants {

    private EpubConstants() {
    }

    /**
     * Used to wrap plain text in a valid XHTML document
     */
    public static final String HTML_WRAPPER = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>{0}</title></head><body>{1}</body></html>";

    /**
     * Template of the OPF file
     */
    public static final String OPF_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<package xmlns=\"http://www.idpf.org/2007/opf\" version=\"3.0\" unique-identifier=\"uid\">\n"
            + "   <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n"
            + "       <dc:identifier id=\"uid\"></dc:identifier>\n"
            + "       <dc:title></dc:title>\n"
            + "       <dc:language></dc:language>\n"
            + "       <meta property=\"dcterms:modified\"></meta>"
            + "   </metadata>\n"
            + "   <manifest>\n"
            + "   </manifest>\n"
            + "   <spine>\n"
            + "   </spine>\n"
            + "</package>";

    /**
     * Template of a valid table of contents navigation(TOC) XHTML document
     */
    public static final String TOC_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
            + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:epub=\"http://www.idpf.org/2007/ops\">\n"
            + "	<head>\n"
            + "		<meta charset=\"utf-8\" />\n"
            + "		<title>{0}</title>		\n"
            + "	</head>\n"
            + "	<body>\n"
            + "		<nav epub:type=\"toc\" id=\"toc\">\n"
            + "           <ol></ol>\n"
            + "		</nav>\n"
            + "		<nav epub:type=\"landmarks\" hidden=\"\">\n"
            + "           <ol></ol>\n"
            + "		</nav>\n"
            + "	</body>\n"
            + "</html>";

    /**
     * Template of a valid EPUB3 container XML
     */
    public static final String CONTAINER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<container xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\" version=\"1.0\">\n"
            + "   <rootfiles>\n"
            + "      <rootfile full-path=\"{0}/book.opf\" media-type=\"application/oebps-package+xml\"/>\n"
            + "   </rootfiles>\n"
            + "</container>";

    /**
     * Default folder used to save content in
     */
    public static final String CONTENT_FOLDER = "content";

    /**
     * The default href of the toc file
     */
    public static final String TOC_FILE_NAME = "toc.xhtml";

    /**
     * The default href of the opf file
     */
    public static final String OPF_FILE_NAME = "book.opf";

}
