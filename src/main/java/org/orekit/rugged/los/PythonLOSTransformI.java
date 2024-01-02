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

import org.hipparchus.analysis.differentiation.Derivative;
import org.hipparchus.analysis.differentiation.DerivativeStructure;
import org.hipparchus.geometry.euclidean.threed.FieldVector3D;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.rugged.utils.DSGenerator;
import org.orekit.rugged.utils.DerivativeGenerator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.ParameterDriver;


import java.util.stream.Stream;

public class PythonLOSTransformI implements LOSTransform {


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
     * Transform a line-of-sight.
     *
     * @param i    los pixel index
     * @param los  line-of-sight to transform
     * @param date current date
     * @return transformed line-of-sight
     */
    @Override
    public native Vector3D transformLOS(int i, Vector3D los, AbsoluteDate date);

    @Override
    public native <T extends Derivative<T>> FieldVector3D<T> transformLOS(int index, FieldVector3D<T> los, AbsoluteDate date, DerivativeGenerator<T> generator);


    /**
     * Get the drivers for LOS parameters.
     *
     * @return drivers for LOS parameters
     * @since 2.0
     */
    @Override
    public native Stream<ParameterDriver> getParametersDrivers();
}
