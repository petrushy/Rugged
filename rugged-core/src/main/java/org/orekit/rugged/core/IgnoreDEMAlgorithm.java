/* Copyright 2013-2014 CS Systèmes d'Information
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
package org.orekit.rugged.core;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.rugged.api.RuggedException;
import org.orekit.rugged.api.TileUpdater;
import org.orekit.rugged.core.dem.IntersectionAlgorithm;

/** Intersection ignoring Digital Elevation Model.
 * <p>
 * This dummy implementation simply uses the ellipsoid itself.
 * </p>
 * @author Luc Maisonobe
 */
public class IgnoreDEMAlgorithm implements IntersectionAlgorithm {

    /** Simple constructor.
     */
    public IgnoreDEMAlgorithm() {
    }

    /** {@inheritDoc} */
    @Override
    public void setUpTilesManagement(final TileUpdater updater, final int maxCachedTiles) {
        // we ignore the DEM
    }

    /** {@inheritDoc} */
    @Override
    public GeodeticPoint intersection(final ExtendedEllipsoid ellipsoid,
                                      final Vector3D position, final Vector3D los)
        throws RuggedException {
        try {
            return ellipsoid.transform(ellipsoid.pointAtAltitude(position, los, 0.0),
                                       ellipsoid.getBodyFrame(), null);
        } catch (OrekitException oe) {
            // this should never happen
            throw new RuggedException(oe, oe.getSpecifier(), oe.getParts());
        }
    }

}
