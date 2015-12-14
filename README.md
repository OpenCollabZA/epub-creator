# epub-creator
[![epub-creator version](https://img.shields.io/badge/epub--creator-v1.0.0-green.svg?style=flat)](http://semver.org) [![WTFPL license](http://img.shields.io/badge/License-MIT-blue.svg)](http://opensource.org/licenses/MIT) [![Build Status](https://travis-ci.org/OpenCollabZA/epub-creator.svg)](https://travis-ci.org/OpenCollabZA/epub-creator)

# Java EPUB3 creator API

Create EPUB3 standard ebooks. 

# Features
1. Build valid EPUB3 ([Validator](http://validator.idpf.org/))
2. Add files or text as content
3. Set TOC inclusion
4. Set spine inclusion
5. Add text as content/pages

# Build instructions

You will need

1. [Java 7+](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
2. [Maven 3+](https://maven.apache.org/download.cgi)

```
mvn clean install
```


# Code example

```
try (FileOutputStream file = new FileOutputStream(new File("test.epub"))) {

            EpubBook book = new EpubBook("en", "Samuel .-__Id1", "Test Book", "Samuel Holtzkampf");
            book.addContent(this.getClass().getResourceAsStream("/epub30-overview.xhtml"),
                    "application/xhtml+xml", "xhtml/epub30-overview.xhtml", true, true).setId("Overview");
            book.addContent(this.getClass().getResourceAsStream("/idpflogo_web_125.jpg"),
                    "image/jpeg", "img/idpflogo_web_125.jpg", false, false);
            book.addContent(this.getClass().getResourceAsStream("/epub-spec.css"),
                    "text/css", "css/epub-spec.css", false, false);
            book.addTextContent("TestHtml", "xhtml/test2.xhtml", "Test one two four!!!!!\nTesting two").setToc(true);
            book.addTextContent("TestHtml", "xhtml/test.xhtml", "Test one two three\nTesting two").setToc(true);
            book.addCoverImage(IOUtils.toByteArray(this.getClass().getResourceAsStream("/P1010832.jpg")),
                    "image/jpeg", "images/P1010832.jpg");

            book.writeToStream(file);
        }
```
