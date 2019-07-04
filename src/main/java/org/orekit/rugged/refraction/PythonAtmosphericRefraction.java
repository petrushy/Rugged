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

package org.orekit.rugged.refraction;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.rugged.intersection.IntersectionAlgorithm;
import org.orekit.rugged.utils.ExtendedEllipsoid;
import org.orekit.rugged.utils.NormalizedGeodeticPoint;

public class PythonAtmosphericRefraction extends AtmosphericRefraction {

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
     * Apply correction to the intersected point with an atmospheric refraction model.
     *
     * @param satPos          satellite position, in <em>body frame</em>
     * @param satLos          satellite line of sight, in <em>body frame</em>
     * @param rawIntersection intersection point before refraction correction
     * @param algorithm       intersection algorithm
     * @return corrected point with the effect of atmospheric refraction
     * {@link ExtendedEllipsoid#pointAtAltitude(Vector3D, Vector3D, double)} or see
     * {@link IntersectionAlgorithm#refineIntersection(ExtendedEllipsoid, Vector3D, Vector3D, NormalizedGeodeticPoint)}
     */
    @Override
    public native NormalizedGeodeticPoint applyCorrection(Vector3D satPos, Vector3D satLos, NormalizedGeodeticPoint rawIntersection, IntersectionAlgorithm algorithm);
}
