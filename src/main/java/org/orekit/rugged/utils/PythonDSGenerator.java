/* Copyright 2013-2019 CS Systèmes d'Information
 * Licensed to CS Systèmes d'Information (CS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * CS licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// this file was created by SSC 2019 and is largely a derived work from the
// original java class/interface

package org.orekit.rugged.utils;

import org.hipparchus.analysis.differentiation.DerivativeStructure;
import org.orekit.utils.ParameterDriver;

import java.util.List;

public class PythonDSGenerator implements DSGenerator {

    /** Part of JCC Python interface to object */
    private long pythonObject;

    /** Part of JCC Python interface to object */
    public void pythonExtension(long pythonObject)
    {
        this.pythonObject = pythonObject;
    }

    /** Part of JCC Python interface to object */
    public long pythonExtension()
    {
        return this.pythonObject;
    }

    /** Part of JCC Python interface to object */
    public void finalize()
            throws Throwable
    {
        pythonDecRef();
    }

    /** Part of JCC Python interface to object */
    public native void pythonDecRef();


    /**
     * Get the parameters selected for estimation.
     *
     * @return parameters selected for estimation
     */
    @Override
    public native List<ParameterDriver> getSelected();

    /**
     * Generate a constant {@link DerivativeStructure}.
     *
     * @param value value of the constant
     * @return constant {@link DerivativeStructure}
     */
    @Override
    public native DerivativeStructure constant(double value);

    /**
     * Generate a {@link DerivativeStructure} representing the
     * parameter driver either as a canonical variable or a constant.
     * <p>
     * The instance created is a variable only if the parameter
     * has been selected for estimation, otherwise it is a constant.
     * </p>
     *
     * @param driver driver for the variable
     * @return variable {@link DerivativeStructure}
     */
    @Override
    public native DerivativeStructure variable(ParameterDriver driver);
}
