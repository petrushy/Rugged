/* Copyright 2013-2016 CS Systèmes d'Information
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
package AffinagePleiades;


import org.orekit.rugged.api.SensorToGroundMapping;
import org.orekit.rugged.api.Rugged;
import org.orekit.rugged.linesensor.LineSensor;
import org.orekit.rugged.linesensor.SensorPixel;
import org.orekit.rugged.errors.RuggedException;
import org.orekit.time.AbsoluteDate;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.random.UncorrelatedRandomVectorGenerator;
import org.hipparchus.random.UniformRandomGenerator;
import org.hipparchus.random.Well19937a;
import org.hipparchus.util.FastMath;
import org.orekit.bodies.GeodeticPoint;

/** class for measure generation
 * @author Jonathan Guinet
 */
public class MeasureGenerator {


    /** mapping */
    private SensorToGroundMapping mapping;

    private Rugged rugged;
    
    private LineSensor sensor;

    private PleiadesViewingModel viewingModel;
    
    private int measureCount;
    
    
    
    /** Simple constructor.
     * <p>
     *
     * </p>
     */
    public MeasureGenerator(PleiadesViewingModel viewingModel, Rugged rugged) throws RuggedException
    {
	
    // generate reference mapping
    String sensorName = viewingModel.getSensorName();	
    mapping = new SensorToGroundMapping(sensorName);
    this.rugged = rugged;
    this.viewingModel = viewingModel;
    sensor = rugged.getLineSensor(mapping.getSensorName());
    measureCount = 0;
    
    }
    
    public SensorToGroundMapping getMapping() {
    	return mapping;
    }
    
    public int  getMeasureCount() {
    	return measureCount;
    }    
    
    public void CreateMeasure(final int lineSampling,final int pixelSampling)  throws RuggedException
    {
    	for (double line = 0; line < viewingModel.dimension; line += lineSampling) {
        	
        	AbsoluteDate date = sensor.getDate(line);
        	for (int pixel = 0; pixel < sensor.getNbPixels(); pixel += pixelSampling) {

        		GeodeticPoint gp2 = rugged.directLocation(date, sensor.getPosition(),
                                                      sensor.getLOS(date, pixel));
            
        		mapping.addMapping(new SensorPixel(line, pixel), gp2);
        		measureCount++;
        	}
        }
    }
    public void CreateNoisyMeasure(final int lineSampling,final int pixelSampling)  throws RuggedException
    {
    	Vector3D latLongError = estimateLatLongError();
    	
    	double latError = FastMath.toRadians(latLongError.getX()); // in line: -0.000002 deg
    	double lonError = FastMath.toRadians(latLongError.getY()); // in line: 0.000012 deg
    	System.out.format("Corresponding error estimation on ground: {lat: %1.10f deg, lon: %1.10f deg} %n", latLongError.getX(), latLongError.getY());
    	
    	// Gaussian random generator
    	// Build a null mean random uncorrelated vector generator with standard deviation corresponding to the estimated error on ground
    	double mean[] = {0.0,0.0,0.0};
    	double std[] = {latError,lonError,0.0};
    	UniformRandomGenerator rng = new UniformRandomGenerator(new Well19937a(0xefac03d9be4d24b9l));
    	UncorrelatedRandomVectorGenerator rvg = new UncorrelatedRandomVectorGenerator(mean, std, rng);
        
    	System.out.format("Add a gaussian noise to measures without biais (null mean) and standard deviation corresponding to the estimated error on ground.%n");
    	for (double line = 0; line < viewingModel.dimension; line += lineSampling) {
        	
        	AbsoluteDate date = sensor.getDate(line);
        	for (int pixel = 0; pixel < sensor.getNbPixels(); pixel += pixelSampling) {

        		// Components of generated vector follow (independent) Gaussian distribution
            	Vector3D vecRandom = new Vector3D(rvg.nextVector());
            	
        		GeodeticPoint gp2 = rugged.directLocation(date, sensor.getPosition(),
                                                      sensor.getLOS(date, pixel));
            
        		GeodeticPoint gpNoisy = new GeodeticPoint(gp2.getLatitude()+vecRandom.getX(), 
                        gp2.getLongitude()+vecRandom.getY(),
                        gp2.getAltitude()); // no altitude error introducing

        		/*if(line == 0) {
        			System.out.format("Init  gp: (%f,%d): %s %n",line,pixel,gp2.toString());
        			System.out.format("Random:   (%f,%d): %s %n",line,pixel,vecRandom.toString());
        			System.out.format("Final gp: (%f,%d): %s %n",line,pixel,gpNoisy.toString());
        		}*/
   
        		mapping.addMapping(new SensorPixel(line, pixel), gpNoisy);
        		measureCount++;
        	}
        }
    }
    private Vector3D estimateLatLongError() throws RuggedException {
    
    	System.out.format("Uncertainty in pixel (in line) for a real geometric refining: 1 pixel (assumption)%n");
    	double line=0;
    	AbsoluteDate date = sensor.getDate(line);
    	GeodeticPoint gp_pix0 = rugged.directLocation(date, sensor.getPosition(), sensor.getLOS(date, 0));
    	GeodeticPoint gp_pix1 = rugged.directLocation(date, sensor.getPosition(), sensor.getLOS(date, 1));
    	double latErr=FastMath.toDegrees(gp_pix0.getLatitude()-gp_pix1.getLatitude());
		double lonErr=FastMath.toDegrees(gp_pix0.getLongitude()-gp_pix1.getLongitude());
    	
    	return new Vector3D(latErr,lonErr,0.0);
    }

    
}

