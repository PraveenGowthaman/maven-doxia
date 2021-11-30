package org.apache.maven.doxia.module.markdown;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MarkdownParser}.
 *
 * @author <a href="mailto:julien.nicoulaud@gmail.com">Julien Nicoulaud</a>
 * @since 1.3
 */
public class MarkdownParserTest
    extends AbstractParserTest
{

    /**
     * The {@link MarkdownParser} used for the tests.
     */
    protected MarkdownParser parser;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        parser = (MarkdownParser) lookup( Parser.class, MarkdownParser.ROLE_HINT );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Parser createParser()
    {
        return parser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String outputExtension()
    {
        return MarkdownParserModule.FILE_EXTENSION;
    }

    /**
     * Assert the paragraph sink event is fired when parsing "paragraph.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testParagraphSinkEvent()
        throws Exception
    {
        List<SinkEventElement> list = parseFileToEventTestingSink( "paragraph" ).getEventList();

        assertThat ( list ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "paragraph", "text", "paragraph_", "body_" );
    }

    /**
     * Assert the bold sink event is fired when parsing "font-bold.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testFontBoldSinkEvent()
        throws Exception
    {
        //System.out.println( parseFileToHtml( "font-bold" ) );
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "font-bold" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "paragraph", "inline", "text", "inline_", "paragraph_", "body_" );

        SinkEventElement inline = eventList.get( 4 );
        assertEquals( "inline", inline.getName() );
        SinkEventAttributeSet atts = (SinkEventAttributeSet) inline.getArgs()[0];
        assertTrue( atts.containsAttribute( SinkEventAttributes.SEMANTICS, "bold" ) );
    }

    /**
     * Assert the italic sink event is fired when parsing "font-italic.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testFontItalicSinkEvent()
        throws Exception
    {
        //System.out.println( parseFileToHtml( "font-italic" ) );
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "font-italic" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "paragraph", "inline", "text", "inline_", "paragraph_", "body_" );

        SinkEventElement inline = eventList.get( 4 );
        assertEquals( "inline", inline.getName() );
        SinkEventAttributeSet atts = (SinkEventAttributeSet) inline.getArgs()[0];
        assertTrue( atts.containsAttribute( SinkEventAttributes.SEMANTICS, "italic" ) );
    }

    /**
     * Assert the monospaced/code sink event is fired when parsing "font-monospaced.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testFontMonospacedSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "font-monospaced" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "paragraph", "inline", "text", "inline_", "paragraph_", "body_" );

        SinkEventElement inline = eventList.get( 4 );
        assertEquals( "inline", inline.getName() );
        SinkEventAttributeSet atts = (SinkEventAttributeSet) inline.getArgs()[0];
        assertTrue( atts.containsAttribute( SinkEventAttributes.SEMANTICS, "code" ) );
    }

    /**
     * Assert the verbatim sink event is fired when parsing "code.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testCodeSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "code" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "paragraph", "text", "paragraph_", "text", "verbatim", "inline", "text", "inline_", "verbatim_", "body_" );
    }

    /**
     * Assert the verbatim sink event is fired when parsing "fenced-code-block.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testFencedCodeBlockSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "fenced-code-block" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "paragraph", "text", "paragraph_", "text", "verbatim", "inline", "text", "inline_", "verbatim_", "body_" );

        // PRE element must be a "verbatim" Sink event that specifies
        // BOXED = true
        SinkEventElement pre = eventList.get( 7 );
        assertEquals( "verbatim", pre.getName() );
        SinkEventAttributeSet preAtts = (SinkEventAttributeSet) pre.getArgs()[0];
        assertTrue( preAtts.containsAttribute( SinkEventAttributes.DECORATION, "boxed" ) );

        // * CODE element must be an "inline" Sink event that specifies:
        // * SEMANTICS = "code" and CLASS = "language-java"
        SinkEventElement code = eventList.get( 8 );
        assertEquals( "inline", code.getName() );
        SinkEventAttributeSet codeAtts = (SinkEventAttributeSet) code.getArgs()[0];
        assertTrue( codeAtts.containsAttribute( SinkEventAttributes.SEMANTICS, "code" ) );
        assertTrue( codeAtts.containsAttribute( SinkEventAttributes.CLASS, "language-java" ) );
    }

    /**
     * Assert the figureGraphics sink event is fired when parsing "image.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testImageSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "image" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "paragraph", "text", "figureGraphics", "text", "paragraph_", "body_" );
    }

    /**
     * Assert the link sink event is fired when parsing "link.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testLinkSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "link" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "paragraph", "text", "link", "text", "link_", "text", "paragraph_", "body_" );
    }

    /**
     * Assert the link sink event is fired when parsing "link.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testLinkRewriteSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "link_rewrite" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "paragraph", "text", "link", "text", "link_", "text", "link", "text",
                      "link_", "text", "paragraph_", "body_" );

        assertEquals( "doc.html", eventList.get( 5 ).getArgs()[0] );
        assertEquals( "ftp://doc.md", eventList.get( 9 ).getArgs()[0] );
    }

    public void testLinkWithAnchorAndQuery() throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "link_anchor_query" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "paragraph", "link", "text", "link_", "paragraph_", "body_" );
    }

    /**
     * Assert the list sink event is fired when parsing "list.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testListSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "list" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "list", "text", "listItem", "text", "listItem_", "listItem", "text",
                      "listItem_", "text", "list_", "body_" );
    }

    /**
     * Assert the numbered list sink event is fired when parsing "numbered-list.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testNumberedListSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "numbered-list" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "numberedList", "text", "numberedListItem", "text", "numberedListItem_",
                      "numberedListItem", "text", "numberedListItem_", "text", "numberedList_", "body_" );
    }

    /**
     * Assert the metadata is passed through when parsing "metadata.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testMetadataSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "metadata" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "title", "text", "text", "text", "title_", "author", "text", "author_", "date", "text", "date_",
                      "unknown", "head_", "body", "unknown", "text", "unknown", "paragraph", "text", "paragraph_", "section1",
                      "sectionTitle1", "text", "sectionTitle1_", "paragraph", "text", "paragraph_", "section1_",
                      "body_" );

        // Title must be "A Title & a Test"
        assertEquals( "A Title ", eventList.get( 2 ).getArgs()[0]);
        assertEquals( "&", eventList.get( 3 ).getArgs()[0]);
        assertEquals( " a 'Test'", eventList.get( 4 ).getArgs()[0]);

        // Author must be "Somebody <somebody@somewhere.org>"
        assertEquals( "Somebody 'Nickname' Great <somebody@somewhere.org>", eventList.get( 7 ).getArgs()[0]);

        // Date must be "2013 © Copyleft"
        assertEquals( "2013 \u00A9 Copyleft", eventList.get( 10 ).getArgs()[0]);

        // * META element must be an "unknown" Sink event that specifies:
        // * name = "keywords" and content = "maven,doxia,markdown"
        SinkEventElement meta = eventList.get( 12 );
        assertEquals( "unknown", meta.getName() );
        assertEquals( "meta", meta.getArgs()[0] );
        SinkEventAttributeSet metaAtts = (SinkEventAttributeSet) meta.getArgs()[2];
        assertTrue( metaAtts.containsAttribute( SinkEventAttributes.NAME, "keywords" ) );
        assertTrue( metaAtts.containsAttribute( "content", "maven,doxia,markdown" ) );

    }

    /**
     * Assert the first header is passed as title event when parsing "first-heading.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testFirstHeadingSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "first-heading" ).getEventList();

        // NOTE: H1 is rendered as "unknown" and H2 is "section1" (see DOXIA-203)
        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "title", "text", "title_", "head_", "body", "comment", "text",
                "section1", "sectionTitle1", "text", "sectionTitle1_", "paragraph", "text",
                "paragraph_", "section1_", "body_" );
    }

    /**
     * Assert the first header is passed as title event when parsing "comment-before-heading.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testCommentBeforeHeadingSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "comment-before-heading" ).getEventList();

        // NOTE: H1 is rendered as "unknown" and H2 is "section1" (see DOXIA-203)
        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "title", "text", "title_", "head_", "body", "comment", "text", "unknown", "text",
                      "unknown", "paragraph", "text", "link", "text", "link_", "text", "paragraph_", "body_" );
    }

    /**
     * Assert the first header is passed as title event when parsing "comment-before-heading.md".
     *
     * @throws Exception if the event list is not correct when parsing the document
     */
    public void testHtmlContent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "html-content" ).getEventList();

        // NOTE: H1 and DIV are rendered as "unknown" and H2 is "section1" (see DOXIA-203)
        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "head_", "body", "unknown", "text", "paragraph", "inline", "text",
                      "inline_", "text", "inline", "text", "inline_", "text", "paragraph_", "text", "unknown", "text", "horizontalRule", "unknown",
                "text", "unknown", "paragraph", "text", "paragraph_", "text", "table", "tableRows", "text", "tableRow",
                "tableHeaderCell", "text", "tableHeaderCell_", "tableRow_", "text", "tableRow",
                                "tableCell", "text", "tableCell_", "tableRow_", "text", "tableRows_", "table_",
                "body_" );
    }

    /**
     * Parse the file and return a {@link SinkEventTestingSink}.
     *
     * @param file the file to parse with {@link #parser}
     * @return a sink to test parsing events
     * @throws ParseException if the document parsing failed
     * @throws IOException if an I/O error occurs while closing test reader
     */
    protected SinkEventTestingSink parseFileToEventTestingSink( String file ) throws ParseException, IOException
    {
        SinkEventTestingSink sink;
        try ( Reader reader = getTestReader( file ) )
        {
            sink = new SinkEventTestingSink();
            parser.parse( reader, sink );
        }

        return sink;
    }

    protected String parseFileToHtml( String file ) throws IOException
    {
        try ( Reader reader = getTestReader( file ) )
        {
            return parser.toHtml( reader ).toString();
        }
    }

    public void testTocMacro2() throws IOException
    {
        String html = parseFileToHtml( "macro-toc" );
        assertThat( html ).contains( "<!-- MACRO{toc|fromDepth=1|toDepth=2} -->" );
    }

    public void testTocMacro()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "macro-toc" ).getEventList();

        assertThat( eventList ).extracting( "name" ).containsExactly(
                "head", "title", "text", "title_", "head_",
                      "body",
                      "list", // TOC start
                      "listItem", "link", "text", "link_", // emtpy section 2 TOC entry
                      "list", // sections 3 list start
                      "listItem", "link", "text", "link_", "listItem_", // first section 3 TOC entry
                      "listItem", "link", "text", "link_", "listItem_", // second section 3 TOC entry
                      "list_", // sections 3 list end
                      "listItem_", // emtpy section 2 TOC entry end
                      "list_", // TOC end
                      "text",
                      "section1",
                      "section2", "sectionTitle2", "text", "sectionTitle2_", "section2_",
                      "section2", "sectionTitle2", "text", "sectionTitle2_", "section2_",
                      "section1_",
                      "body_" );
    }

    // TOC macro fails with EmptyStackException when title 2 followed by title 4 then title 2
    public void testTocMacroDoxia559()
        throws Exception
    {
        List<SinkEventElement> list = parseFileToEventTestingSink( "macro-toc-DOXIA-559" ).getEventList();

        assertThat( list ).extracting( "name" ).containsExactly(
              "head", "title", "text", "title_", "head_",
              "body",
              "list", // TOC start
              "listItem", "link", "text", "link_", // first section 2 TOC entry
              "list", // sections 3 list start
              "listItem", "link", "text", "link_", "listItem_", // empty section 3 TOC entry
              "list_", // sections 3 list end
              "listItem_", // first section 2 TOC entry end
              "listItem", "link", "text", "link_", "listItem_", // second section 2 TOC entry
              "list_", // TOC end
              "text",
              "section1", "sectionTitle1", "text", "sectionTitle1_",
              "section2",
              "section3", "sectionTitle3", "text", "sectionTitle3_",
              "section3_",
              "section2_",
              "section1_",
              "section1", "sectionTitle1", "text", "sectionTitle1_",
              "section1_",
              "body_" );
    }

    // test fix for https://github.com/vsch/flexmark-java/issues/384
    public void testFlexIssue384()
        throws Exception
    {
        parseFileToEventTestingSink( "flex-384" );
    }

    // Apostrophe versus single quotes
    // Simple apostrophes (like in Sophie's Choice) must not be replaced with a single quote
    public void testQuoteVsApostrophe()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "quote-vs-apostrophe" ).getEventList();

        StringBuilder content = new StringBuilder();
        for ( SinkEventElement element : eventList )
        {
            if ( "text".equals(element.getName()) )
            {
                content.append( element.getArgs()[0] );
            }
        }
        assertEquals(
                "This apostrophe isn't a quote."
                + "This \u2018quoted text\u2019 isn't surrounded by apostrophes.",
                content.toString() );

    }

}
