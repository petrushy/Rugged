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

package org.orekit.rugged.adjustment;

import org.hipparchus.optim.nonlinear.vector.leastsquares.LeastSquaresProblem;
import org.hipparchus.optim.nonlinear.vector.leastsquares.MultivariateJacobianFunction;
import org.orekit.rugged.adjustment.measurements.Observables;
import org.orekit.rugged.linesensor.LineSensor;

import java.util.List;

public class PythonOptimizationProblemBuilder extends OptimizationProblemBuilder {

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
     * Constructor.
     *
     * @param sensors      list of sensors to refine
     * @param measurements set of observables
     */
    PythonOptimizationProblemBuilder(List<LineSensor> sensors, Observables measurements) {
        super(sensors, measurements);
    }

    /**
     * Least squares problem builder.
     *
     * @param maxEvaluations       maximum number of evaluations
     * @param convergenceThreshold convergence threshold
     * @return the least squares problem
     */
    @Override
    public native LeastSquaresProblem build(int maxEvaluations, double convergenceThreshold);

    /**
     * Create targets and weights of optimization problem.
     */
    @Override
    public native void createTargetAndWeight();

    /**
     * Create the model function value and its Jacobian.
     *
     * @return the model function value and its Jacobian
     */
    @Override
    public native MultivariateJacobianFunction createFunction();

    /**
     * Parse the observables to select mapping .
     */
    @Override
    public native void initMapping();
}
