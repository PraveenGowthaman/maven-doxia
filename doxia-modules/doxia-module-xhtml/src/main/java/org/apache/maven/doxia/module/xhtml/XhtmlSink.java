package org.apache.maven.doxia.module.xhtml;

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

import java.io.Writer;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.sink.XhtmlBaseSink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;

/**
 * <a href="http://www.w3.org/TR/xhtml1/">Xhtml 1.0 Transitional</a> sink implementation.
 * <br/>
 * It uses the DTD/xhtml1-transitional <a href="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
 * http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd</a>.
 *
 * @author Jason van Zyl
 * @author ltheussl
 * @version $Id$
 * @since 1.0
 */
public class XhtmlSink
    extends XhtmlBaseSink
    implements XhtmlMarkup
{
    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    private String encoding;

    private String languageId;

    /** An indication on if we're inside a head title. */
    private boolean headTitleFlag;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Constructor, initialize the Writer.
     *
     * @param writer not null writer to write the result.
     */
    protected XhtmlSink( Writer writer )
    {
        super( writer );
    }

    /**
     * Constructor, initialize the Writer and tells which encoding is used.
     *
     * @param writer not null writer to write the result.
     * @param encoding the encoding used, that should be written to the generated HTML content
     * if not <code>null</code>.
     */
    protected XhtmlSink( Writer writer, String encoding )
    {
        super( writer );

        this.encoding = encoding;
    }

    /**
     * Constructor, initialize the Writer and tells which encoding and languageId are used.
     *
     * @param writer not null writer to write the result.
     * @param encoding the encoding used, that should be written to the generated HTML content
     * if not <code>null</code>.
     * @param languageId language identifier for the root element as defined by
     * <a href="ftp://ftp.isi.edu/in-notes/bcp/bcp47.txt">IETF BCP 47</a>, Tags for the Identification of Languages;
     * in addition, the empty string may be specified.
     */
    protected XhtmlSink( Writer writer, String encoding, String languageId )
    {
        this( writer, encoding );

        this.languageId = languageId;
    }

    /** {@inheritDoc} */
    public void head()
    {
        resetState();

        setHeadFlag( true );

        write( "<!DOCTYPE html PUBLIC \"" + XHTML_TRANSITIONAL_PUBLIC_ID + "\" \"" + XHTML_TRANSITIONAL_SYSTEM_ID
            + "\">" );

        MutableAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute( "xmlns", XHTML_NAMESPACE );

        if ( languageId != null )
        {
            atts.addAttribute( Attribute.LANG.toString(), languageId );
            atts.addAttribute( "xml:lang", languageId );
        }

        writeStartTag( Tag.HTML, atts );

        writeStartTag( Tag.HEAD );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        if ( !isHeadTitleFlag() )
        {
            // The content of element type "head" must match
            // "((script|style|meta|link|object|isindex)*,
            //  ((title,(script|style|meta|link|object|isindex)*,
            //  (base,(script|style|meta|link|object|isindex)*)?)|(base,(script|style|meta|link|object|isindex)*,
            //  (title,(script|style|meta|link|object|isindex)*))))"
            writeStartTag( Tag.TITLE );
            writeEndTag( Tag.TITLE );
        }

        setHeadFlag( false );
        setHeadTitleFlag( false );

        if ( encoding != null )
        {
            write( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\"/>" );
        }

        writeEndTag( Tag.HEAD );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title()
    {
        setHeadTitleFlag( true );

        writeStartTag( Tag.TITLE );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title_()
    {
        content( getTextBuffer().toString() );

        writeEndTag( Tag.TITLE );

        resetTextBuffer();

    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#META
     */
    public void author_()
    {
        if ( getTextBuffer().length() > 0 )
        {
            MutableAttributeSet att = new SinkEventAttributeSet();
            att.addAttribute( Attribute.NAME, "author" );
            att.addAttribute( Attribute.CONTENT, getTextBuffer().toString() );

            writeSimpleTag( Tag.META, att );

            resetTextBuffer();
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#META
     */
    public void date_()
    {
        if ( getTextBuffer().length() > 0 )
        {
            MutableAttributeSet att = new SinkEventAttributeSet();
            att.addAttribute( Attribute.NAME, "date" );
            att.addAttribute( Attribute.CONTENT, getTextBuffer().toString() );

            writeSimpleTag( Tag.META, att );

            resetTextBuffer();
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BODY
     */
    public void body()
    {
        writeStartTag( Tag.BODY );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BODY
     * @see javax.swing.text.html.HTML.Tag#HTML
     */
    public void body_()
    {
        writeEndTag( Tag.BODY );

        writeEndTag( Tag.HTML );

        flush();

        resetState();
    }

    // ----------------------------------------------------------------------
    // Public protected methods
    // ----------------------------------------------------------------------

    /**
     * <p>Setter for the field <code>headTitleFlag</code>.</p>
     *
     * @param headTitleFlag an header title flag.
     * @since 1.1
     */
    protected void setHeadTitleFlag( boolean headTitleFlag )
    {
        this.headTitleFlag = headTitleFlag;
    }

    /**
     * <p>isHeadTitleFlag</p>
     *
     * @return the current headTitleFlag.
     * @since 1.1
     */
    protected boolean isHeadTitleFlag()
    {
        return this.headTitleFlag ;
    }
}
