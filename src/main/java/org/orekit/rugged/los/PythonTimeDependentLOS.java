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

package org.orekit.rugged.los;

import org.hipparchus.analysis.differentiation.DerivativeStructure;
import org.hipparchus.geometry.euclidean.threed.FieldVector3D;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.rugged.utils.DSGenerator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.ParameterDriver;

import java.util.stream.Stream;

public class PythonTimeDependentLOS implements TimeDependentLOS {

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
     * Get the number of pixels.
     *
     * @return number of pixels
     */
    @Override
    public native int getNbPixels();

    /**
     * Get the line of sight for a given date.
     *
     * @param index los pixel index
     * @param date  date
     * @return line of sight
     */
    @Override
    public native Vector3D getLOS(int index, AbsoluteDate date);

    /**
     * Get the line of sight and its partial derivatives for a given date.
     * <p>
     * This method is used for LOS calibration purposes. It allows to compute
     * the Jacobian matrix of the LOS with respect to the estimated parameters, which
     * are typically polynomials coefficients representing rotation angles.
     * These polynomials can be used for example to model thermo-elastic effects.
     * </p>
     * <p>
     * Note that in order for the partial derivatives to be properly set up, the
     * {@link ParameterDriver#setSelected(boolean) setSelected}
     * method must have been set to {@code true} for the various parameters returned
     * by {@link #getParametersDrivers()} that should be estimated.
     * </p>
     *
     * @param index     los pixel index
     * @param date      date
     * @param generator generator to use for building {@link DerivativeStructure} instances
     * @return line of sight, and its first partial derivatives with respect to the parameters
     * @since 2.0
     */
    @Override
    public native FieldVector3D<DerivativeStructure> getLOSDerivatives(int index, AbsoluteDate date, DSGenerator generator);

    /**
     * Get the drivers for LOS parameters.
     *
     * @return drivers for LOS parameters
     * @since 2.0
     */
    @Override
    public native Stream<ParameterDriver> getParametersDrivers();
}
