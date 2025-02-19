package org.apache.maven.doxia.module;

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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.doxia.AbstractModuleTest;

import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkTestDocument;
import org.apache.maven.doxia.sink.impl.TextSink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * If a module provides both Parser and Sink, this class
 * can be used to check that chaining them together
 * results in the identity transformation, ie the model is still the same
 * after being piped through a Parser and the corresponding Sink.
 */
public abstract class AbstractIdentityTest
    extends AbstractModuleTest
{
    /** Expected Identity String */
    private String expected;

    /**
     * Set to true if the identity transformation should actually be asserted,
     * by default only the expected and actual results are written to a file, but not compared.
     */
    private boolean assertIdentity;

    /**
     * Create a new instance of the parser to test.
     *
     * @return the parser to test.
     */
    protected abstract Parser createParser();

    /**
     * Return a new instance of the sink that is being tested.
     *
     * @param writer The writer for the sink.
     * @return A new sink.
     */
    protected abstract Sink createSink( Writer writer );

    /**
     * Pipes a full model generated by {@link SinkTestDocument} through
     * a Sink (generated by {@link #createSink(Writer)}) and a Parser
     * (generated by {@link #createParser()}) and checks if the result
     * is the same as the original model. By default, this doesn't actually
     * assert anything (use {@link #assertIdentity(boolean)} in the setUp()
     * of an implementation to switch on the test), but the two generated
     * output files, expected.txt and actual.txt, can be compared for differences.
     *
     * @throws IOException if there's a problem reading/writing a test file.
     * @throws ParseException if a model cannot be parsed.
     */
    @Test
    public void testIdentity()
        throws IOException, ParseException
    {
        // generate the expected model
        StringWriter writer = new StringWriter();
        Sink sink = new TextSink( writer );
        SinkTestDocument.generate( sink );
        sink.close();
        expected = writer.toString();

        // write to file for comparison
        try ( Writer fileWriter = getTestWriter( "expected" ) )
        {
            fileWriter.write( expected );
        }
        // generate the actual model
        writer = new StringWriter();
        sink = createSink( writer );
        SinkTestDocument.generate( sink );
        sink.close();
        StringReader reader = new StringReader( writer.toString() );

        writer = new StringWriter();
        sink = new TextSink( writer );
        Parser parser = createParser();
        parser.parse( reader, sink );
        String actual = writer.toString();

        // write to file for comparison
        try( Writer fileWriter = getTestWriter( "actual" ) )
        {
            fileWriter.write( actual );
        }

        // Disabled by default, it's unlikely that all our modules
        // will pass this test any time soon, but the generated
        // output files can still be compared.

        if ( assertIdentity )
        {
            // TODO: make this work for at least apt and xdoc modules?
            assertEquals( getExpected(), actual,
                          "Identity test failed! See results in "
                                  + getTestWriterFile( "actual" ).getParent() );
        }
    }

    /** {@inheritDoc} */
    protected String getOutputDir()
    {
        return "identity/";
    }

    /**
     * The output files generated by this class are text files,
     * independent of the kind of module being tested.
     *
     * @return The String "txt".
     */
    protected String outputExtension()
    {
        return "txt";
    }

    /**
     * Set to true if the identity transformation should actually be asserted,
     * by default only the expected and actual results are written to a file, but not compared.
     * This should be called during setUp().
     *
     * @param doAssert True to actually execute the test.
     */
    protected void assertIdentity( boolean doAssert )
    {
        this.assertIdentity = doAssert;
    }

    /**
     * @return the expected identity string
     */
    protected String getExpected()
    {
        return expected;
    }
}
