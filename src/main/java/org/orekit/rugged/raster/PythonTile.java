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

package org.orekit.rugged.raster;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.rugged.utils.NormalizedGeodeticPoint;

public class PythonTile implements Tile {

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
     * Hook called at the end of tile update completion.
     */
    @Override
    public native void tileUpdateCompleted();

    /**
     * Get minimum latitude of grid interpolation points.
     *
     * @return minimum latitude of grid interpolation points (rad)
     * (latitude of the center of the cells of South row)
     */
    @Override
    public native double getMinimumLatitude();

    /**
     * Get the latitude at some index.
     *
     * @param latitudeIndex latitude index
     * @return latitude at the specified index (rad)
     * (latitude of the center of the cells of specified row)
     */
    @Override
    public native double getLatitudeAtIndex(int latitudeIndex);

    /**
     * Get maximum latitude.
     * <p>
     * Beware that as a point at maximum latitude is the northernmost
     * one of the grid, it doesn't have a northwards neighbor and
     * therefore calling {@link #getLocation(double, double) getLocation}
     * on such a latitude will return either {@link Location#NORTH_WEST},
     * {@link Location#NORTH} or {@link Location#NORTH_EAST}, but can
     * <em>never</em> return {@link Location#HAS_INTERPOLATION_NEIGHBORS}!
     * </p>
     *
     * @return maximum latitude (rad)
     * (latitude of the center of the cells of North row)
     */
    @Override
    public native double getMaximumLatitude();

    /**
     * Get minimum longitude.
     *
     * @return minimum longitude (rad)
     * (longitude of the center of the cells of West column)
     */
    @Override
    public native double getMinimumLongitude();

    /**
     * Get the longitude at some index.
     *
     * @param longitudeIndex longitude index
     * @return longitude at the specified index (rad)
     * (longitude of the center of the cells of specified column)
     */
    @Override
    public native double getLongitudeAtIndex(int longitudeIndex);

    /**
     * Get maximum longitude.
     * <p>
     * Beware that as a point at maximum longitude is the easternmost
     * one of the grid, it doesn't have an eastwards neighbor and
     * therefore calling {@link #getLocation(double, double) getLocation}
     * on such a longitude will return either {@link Location#SOUTH_EAST},
     * {@link Location#EAST} or {@link Location#NORTH_EAST}, but can
     * <em>never</em> return {@link Location#HAS_INTERPOLATION_NEIGHBORS}!
     * </p>
     *
     * @return maximum longitude (rad)
     * (longitude of the center of the cells of East column)
     */
    @Override
    public native double getMaximumLongitude();

    /**
     * Get step in latitude (size of one raster element).
     *
     * @return step in latitude (rad)
     */
    @Override
    public native double getLatitudeStep();

    /**
     * Get step in longitude (size of one raster element).
     *
     * @return step in longitude (rad)
     */
    @Override
    public native double getLongitudeStep();

    /**
     * Get number of latitude rows.
     *
     * @return number of latitude rows
     */
    @Override
    public native int getLatitudeRows();

    /**
     * Get number of longitude columns.
     *
     * @return number of longitude columns
     */
    @Override
    public native int getLongitudeColumns();

    /**
     * Get the floor latitude index of a point.
     * <p>
     * The specified latitude is always between index and index+1.
     * </p>
     *
     * @param latitude geodetic latitude
     * @return floor latitude index (it may lie outside of the tile!)
     */
    @Override
    public native int getFloorLatitudeIndex(double latitude);

    /**
     * Get the floor longitude index of a point.
     * <p>
     * The specified longitude is always between index and index+1.
     * </p>
     *
     * @param longitude geodetic longitude
     * @return floor longitude index (it may lie outside of the tile!)
     */
    @Override
    public native int getFloorLongitudeIndex(double longitude);

    /**
     * Get the minimum elevation in the tile.
     *
     * @return minimum elevation in the tile (m)
     */
    @Override
    public native double getMinElevation();

    /**
     * Get the latitude index of min elevation.
     *
     * @return latitude index of min elevation
     */
    @Override
    public native int getMinElevationLatitudeIndex();

    /**
     * Get the longitude index of min elevation.
     *
     * @return longitude index of min elevation
     */
    @Override
    public native int getMinElevationLongitudeIndex();

    /**
     * Get the maximum elevation in the tile.
     *
     * @return maximum elevation in the tile (m)
     */
    @Override
    public native double getMaxElevation();

    /**
     * Get the latitude index of max elevation.
     *
     * @return latitude index of max elevation
     */
    @Override
    public native int getMaxElevationLatitudeIndex();

    /**
     * Get the longitude index of max elevation.
     *
     * @return longitude index of max elevation
     */
    @Override
    public native int getMaxElevationLongitudeIndex();

    /**
     * Get the elevation of an exact grid point.
     *
     * @param latitudeIndex  grid point index along latitude
     * @param longitudeIndex grid point index along longitude
     * @return elevation at grid point (m)
     */
    @Override
    public native double getElevationAtIndices(int latitudeIndex, int longitudeIndex);

    /**
     * Interpolate elevation.
     * <p>
     * In order to cope with numerical accuracy issues when computing
     * points at tile boundary, a slight tolerance (typically 1/8 cell)
     * around the tile is allowed. Elevation can therefore be interpolated
     * (really extrapolated in this case) even for points slightly overshooting
     * tile boundaries, using the closest tile cell. Attempting to interpolate
     * too far from the tile will trigger an exception.
     * </p>
     *
     * @param latitude  ground point latitude
     * @param longitude ground point longitude
     * @return interpolated elevation (m)
     */
    @Override
    public native double interpolateElevation(double latitude, double longitude);

    @Override
    public native NormalizedGeodeticPoint cellIntersection(NormalizedGeodeticPoint p, Vector3D los, int latitudeIndex, int longitudeIndex);

    /**
     * Check if a tile covers a ground point.
     *
     * @param latitude  ground point latitude
     * @param longitude ground point longitude
     * @return location of the ground point with respect to tile
     */
    @Override
    public native Location getLocation(double latitude, double longitude);

    /**
     * Set the tile global geometry.
     *
     * @param minLatitude      minimum latitude (rad)
     * @param minLongitude     minimum longitude (rad)
     * @param latitudeStep     step in latitude (size of one raster element) (rad)
     * @param longitudeStep    step in longitude (size of one raster element) (rad)
     * @param latitudeRows     number of latitude rows
     * @param longitudeColumns number of longitude columns
     */
    @Override
    public native void setGeometry(double minLatitude, double minLongitude, double latitudeStep, double longitudeStep, int latitudeRows, int longitudeColumns);

    /**
     * Set the elevation for one raster element.
     * <p>
     * BEWARE! The order of the indices follows geodetic conventions, i.e.
     * the latitude is given first and longitude afterwards, so the first
     * index specifies a <em>row</em> index with zero at South and max value
     * at North, and the second index specifies a <em>column</em> index
     * with zero at West and max value at East. This is <em>not</em> the
     * same as some raster conventions (as our row index increases from South
     * to North) and this is also not the same as Cartesian coordinates as
     * our ordinate index appears before our abscissa index).
     * </p>
     *
     * @param latitudeIndex  index of latitude (row index)
     * @param longitudeIndex index of longitude (column index)
     * @param elevation      elevation (m)
     */
    @Override
    public native void setElevation(int latitudeIndex, int longitudeIndex, double elevation);
}
