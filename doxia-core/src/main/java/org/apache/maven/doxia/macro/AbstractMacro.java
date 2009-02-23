package org.apache.maven.doxia.macro;

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

import org.apache.maven.doxia.logging.Log;
import org.apache.maven.doxia.logging.SystemStreamLog;
import org.codehaus.plexus.util.StringUtils;

/**
 * Abstract base class to execute <code>Macro</code>.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractMacro
    implements Macro
{
    /** Log instance. */
    private Log log;

    /** {@inheritDoc} */
    public void enableLogging( Log log )
    {
        this.log = log;
    }

    /**
     * Returns a logger for this macro.
     * If no logger has been configured, a new SystemStreamLog is returned.
     *
     * @return Log
     * @since 1.1
     */
    protected Log getLog()
    {
        if ( log == null )
        {
            log = new SystemStreamLog();
        }

        return log;
    }

    /**
     * Check if the given parameter is required. Throws an
     * IllegalArgumentException if paramValue is null or empty.
     *
     * @param paramName The name of the parameter to check.
     * @param paramValue The parameter value.
     * @since 1.1
     */
    protected void required( String paramName, String paramValue )
    {
        if ( StringUtils.isEmpty( paramValue ) )
        {
            throw new IllegalArgumentException( paramName + " is a required parameter!" );
        }
    }
}
