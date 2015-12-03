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
package coza.opencollab.epub.creator.util;

import coza.opencollab.epub.creator.EpubConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import coza.opencollab.epub.creator.api.OpfCreator;
import coza.opencollab.epub.creator.api.TocCreator;
import coza.opencollab.epub.creator.impl.OpfCreatorDefault;
import coza.opencollab.epub.creator.impl.TocCreatorDefault;
import coza.opencollab.epub.creator.model.Content;
import coza.opencollab.epub.creator.model.EpubBook;
import java.util.List;

/**
 * The EpubWriter creates the EPUB zip bundle.
 *
 * @author OpenCollab
 */
public class EpubWriter {

    private String containerXML = EpubConstants.CONTAINER_XML;

    private String contentFolder = EpubConstants.CONTENT_FOLDER;

    private String opfFileName = EpubConstants.OPF_FILE_NAME;

    private OpfCreator opfCreator = new OpfCreatorDefault();

    private TocCreator tocCreator = new TocCreatorDefault();

    /**
     * Writes the EPUB book zip container and contents to a file
     *
     * @param book the EpubBook
     * @param fileName name of the file to be written
     * @throws IOException if file could not be written
     */
    public void writeEpubToFile(EpubBook book, String fileName) throws IOException {
        try (FileOutputStream file = new FileOutputStream(new File(fileName))) {
            writeEpubToStream(book, file);
        }
    }

    /**
     * Writes the EPUB book zip container and contents to an OutputStream
     *
     * @param book the EpubBook
     * @param out the OutputStream to write to
     * @throws IOException if file could not be written
     */
    public void writeEpubToStream(EpubBook book, OutputStream out) throws IOException {
        try (ZipOutputStream resultStream = new ZipOutputStream(out)) {
            List<Content> contents = book.getContents();
            addMimeType(resultStream);
            contents.add(0, getTocCreator().createTocFromBook(book));
            addStringToZip(resultStream, "META-INF/container.xml", MessageFormat.format(containerXML, contentFolder));
            addStringToZip(resultStream, contentFolder + "/" + getOpfFileName(), getOpfCreator().createOpfString(book));
            addContent(resultStream, contents);
        }
    }

    /**
     * Adds the zip/EPUB mime type to the EPUB zip file
     *
     * @param resultStream
     * @throws IOException
     */
    private void addMimeType(ZipOutputStream resultStream) throws IOException {
        ZipEntry mimetypeZipEntry = new ZipEntry("mimetype");
        mimetypeZipEntry.setMethod(ZipEntry.STORED);
        byte[] mimetypeBytes = "application/epub+zip".getBytes("UTF-8");
        mimetypeZipEntry.setSize(mimetypeBytes.length);
        mimetypeZipEntry.setCrc(calculateCrc(mimetypeBytes));
        resultStream.putNextEntry(mimetypeZipEntry);
        resultStream.write(mimetypeBytes);
    }

    /**
     * Calculates the CRC32 of data for the zip entry
     *
     * @param data
     * @return
     */
    private long calculateCrc(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }

    /**
     * Adds string content as an zip entry with the specified file name
     *
     * @param resultStream
     * @param fileName
     * @param content
     * @throws IOException
     */
    private void addStringToZip(ZipOutputStream resultStream, String fileName, String content) throws IOException {
        resultStream.putNextEntry(new ZipEntry(fileName));
        Writer out = new OutputStreamWriter(resultStream, "UTF-8");
        out.write(content);
        out.flush();
    }

    /**
     * Adds the content objects zip entries
     *
     * @param resultStream
     * @param contents
     * @throws IOException
     */
    private void addContent(ZipOutputStream resultStream, List<Content> contents) throws IOException {
        for (Content content : contents) {
            resultStream.putNextEntry(new ZipEntry(contentFolder + "/" + content.getHref()));
            resultStream.write(content.getContent());
        }
    }

    /**
     * @return the CONTAINER_XML
     */
    public String getContainerXML() {
        return containerXML;
    }

    /**
     * @param containerXML the CONTAINER_XML to set
     */
    public void setContainerXML(String containerXML) {
        this.containerXML = containerXML;
    }

    /**
     * @return the CONTENT_FOLDER
     */
    public String getContentFolder() {
        return contentFolder;
    }

    /**
     * @param contentFolder the CONTENT_FOLDER to set
     */
    public void setContentFolder(String contentFolder) {
        this.contentFolder = contentFolder;
    }

    /**
     * @return the OPF_FILE_NAME
     */
    public String getOpfFileName() {
        return opfFileName;
    }

    /**
     * @param opfFileName the OPF_FILE_NAME to set
     */
    public void setOpfFileName(String opfFileName) {
        this.opfFileName = opfFileName;
    }

    /**
     * @return the opfCreator
     */
    public OpfCreator getOpfCreator() {
        return opfCreator;
    }

    /**
     * @param opfCreator the opfCreator to set
     */
    public void setOpfCreator(OpfCreator opfCreator) {
        this.opfCreator = opfCreator;
    }

    /**
     * @return the tocCreator
     */
    public TocCreator getTocCreator() {
        return tocCreator;
    }

    /**
     * @param tocCreator the tocCreator to set
     */
    public void setTocCreator(TocCreator tocCreator) {
        this.tocCreator = tocCreator;
    }

}
