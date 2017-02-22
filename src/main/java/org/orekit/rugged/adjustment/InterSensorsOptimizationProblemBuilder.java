package org.orekit.rugged.adjustment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hipparchus.analysis.differentiation.DerivativeStructure;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.hipparchus.optim.ConvergenceChecker;
import org.hipparchus.optim.nonlinear.vector.leastsquares.LeastSquaresBuilder;
import org.hipparchus.optim.nonlinear.vector.leastsquares.LeastSquaresProblem;
import org.hipparchus.optim.nonlinear.vector.leastsquares.MultivariateJacobianFunction;
import org.hipparchus.optim.nonlinear.vector.leastsquares.ParameterValidator;
import org.hipparchus.util.Pair;
import org.orekit.errors.OrekitException;
import org.orekit.errors.OrekitExceptionWrapper;
import org.orekit.rugged.api.Rugged;
import org.orekit.rugged.errors.RuggedException;
import org.orekit.rugged.errors.RuggedExceptionWrapper;
import org.orekit.rugged.errors.RuggedMessages;
import org.orekit.rugged.linesensor.LineSensor;
import org.orekit.rugged.linesensor.SensorPixel;
import org.orekit.rugged.refining.measures.Observables;
import org.orekit.rugged.refining.measures.SensorToSensorMapping;
import org.orekit.rugged.utils.SpacecraftToObservedBody;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.ParameterDriver;



public class InterSensorsOptimizationProblemBuilder
extends OptimizationProblemBuilder {

    /** list of rugged instance to refine.*/
    private Map<String, Rugged> ruggedMap;

    /** sensorToGround mapping to generate target tab for optimization.*/
    private List<SensorToSensorMapping> sensorToSensorMappings;


    private HashMap<String, double[] > targetAndWeight;

    /**
     * @param sensors list of sensors to refine
     * @param measures set of observables
     * @param rugged name of rugged to refine
     * @throws RuggedException an exception is generated if no parameters has been selected for refining
     */
    public InterSensorsOptimizationProblemBuilder(final List<LineSensor> sensors,
                                                  final Observables measures, final Collection<Rugged> ruggedList)
                                                                  throws RuggedException {
        super(sensors, measures);

        this.ruggedMap = new LinkedHashMap<String, Rugged>();

        for(final Rugged rugged : ruggedList)
        {
            this.ruggedMap.put(rugged.getName(),rugged);
        }
        this.initMapping();
    }

    /* (non-Javadoc)
     * @see org.orekit.rugged.adjustment.OptimizationProblemBuilder#initMapping()
     */
    @Override
    protected void initMapping()
    {
        this.sensorToSensorMappings = new ArrayList<SensorToSensorMapping>();
        for (final String ruggedNameA : this.ruggedMap.keySet()){
            for (final String ruggedNameB : this.ruggedMap.keySet()){
                for (final LineSensor sensorA : this.sensors){
                    for (final LineSensor sensorB : this.sensors){
                        final String sensorNameA = sensorA.getName();
                        final String sensorNameB = sensorB.getName();
                        final SensorToSensorMapping mapping = this.measures.getInterMapping(ruggedNameA, sensorNameA, ruggedNameB, sensorNameB);
                        if (mapping != null)
                        {

                            this.sensorToSensorMappings.add(mapping);
                        }
                    }
                }

            }

        }

    }

    @Override
    protected void createTargetAndWeight()
                    throws RuggedException {
        try {
            int n = 0;
            for (final SensorToSensorMapping reference : this.sensorToSensorMappings) {
                n += reference.getMapping().size();
            }

            if (n == 0) {
                throw new RuggedException(RuggedMessages.NO_REFERENCE_MAPPINGS);
            }

            n = 2*n ;

            final double[] target = new double[n];
            final double[] weight = new double[n];

            int k = 0;

            for (final SensorToSensorMapping reference : this.sensorToSensorMappings) {

                // Get Earth constraint weight
                final double earthConstraintWeight = reference.getEarthConstraintWeight();
                int i=0;
                for (Iterator<Map.Entry<SensorPixel, SensorPixel>> gtIt = reference.getMapping().iterator();gtIt.hasNext();i++){
                    if(i==reference.getMapping().size()) break;

                    // Get LOS distance
                    Double losDistance  = reference.getLosDistance(i);

                    weight[k] = 1.0 - earthConstraintWeight;
                    target[k++] = losDistance.doubleValue();

                    // Get Earth distance (constraint)
                    Double earthDistance  = reference.getEarthDistance(i);
                    weight[k] = earthConstraintWeight;
                    target[k++] = earthDistance.doubleValue();
                }
            }

            this.targetAndWeight = new HashMap<String, double[]>();
            this.targetAndWeight.put("Target", target);
            this.targetAndWeight.put("Weight", weight);

        } catch  (RuggedExceptionWrapper rew) {
            throw rew.getException();
        }
    }

    @Override
    protected MultivariateJacobianFunction createFunction()
    {
        // model function
        final MultivariateJacobianFunction model = point -> {

            try {

                // set the current parameters values
                int i = 0;
                for (final ParameterDriver driver : this.drivers.getDrivers()) {
                    driver.setNormalizedValue(point.getEntry(i++));

                }

                final double[] target = this.targetAndWeight.get("Target");

                // compute distance and its partial derivatives
                final RealVector value = new ArrayRealVector(target.length);
                final RealMatrix jacobian = new Array2DRowRealMatrix(target.length, this.nbParams);

                int l = 0;
                for (final SensorToSensorMapping reference : this.sensorToSensorMappings) {


                    final String ruggedNameA = reference.getRuggedNameA();
                    final String ruggedNameB = reference.getRuggedNameB();
                    final Rugged ruggedA = this.ruggedMap.get(ruggedNameA);

                    if (ruggedA == null) {
                        throw new RuggedException(RuggedMessages.INVALID_RUGGED_NAME);
                    }
                    final Rugged ruggedB = this.ruggedMap.get(ruggedNameB);
                    if (ruggedB == null) {
                        throw new RuggedException(RuggedMessages.INVALID_RUGGED_NAME);
                    }


                    for (final Map.Entry<SensorPixel, SensorPixel> mapping : reference.getMapping()) {

                        final SensorPixel spA = mapping.getKey();
                        final SensorPixel spB = mapping.getValue();

                        LineSensor lineSensorB = ruggedB.getLineSensor(reference.getSensorNameB());
                        LineSensor lineSensorA = ruggedA.getLineSensor(reference.getSensorNameA());

                        final AbsoluteDate dateA = lineSensorA.getDate(spA.getLineNumber());
                        final AbsoluteDate dateB = lineSensorB.getDate(spB.getLineNumber());

                        final double pixelA = spA.getPixelNumber();
                        final double pixelB = spB.getPixelNumber();

                        final SpacecraftToObservedBody scToBodyA = ruggedA.getScToBody();

                        final DerivativeStructure[] ilResult = ruggedB.distanceBetweenLOSDerivatives(lineSensorA,
                                                                                                     dateA,
                                                                                                     pixelA,
                                                                                                     scToBodyA,
                                                                                                     lineSensorB,
                                                                                                     dateB,
                                                                                                     pixelB,
                                                                                                     generator);

                        if (ilResult == null) {
                            // TODO
                        } else {

                            // extract the value
                            value.setEntry(l, ilResult[0].getValue());
                            value.setEntry(l + 1, ilResult[1].getValue());

                            // extract the Jacobian
                            final int[] orders = new int[this.nbParams];
                            int m = 0;

                            for (final ParameterDriver driver : this.drivers.getDrivers()) {
                                final double scale = driver.getScale();
                                orders[m] = 1;
                                jacobian.setEntry(l, m, ilResult[0].getPartialDerivative(orders) * scale);
                                jacobian.setEntry(l + 1, m, ilResult[1].getPartialDerivative(orders) * scale);
                                orders[m] = 0;
                                m++;
                            }
                        }

                        l += 2; // pass to the next evaluation
                    }
                }

                // distance result with Jacobian for all reference points
                return new Pair<RealVector, RealMatrix>(value, jacobian);

            } catch (RuggedException re) {
                throw new RuggedExceptionWrapper(re);
            } catch (OrekitException oe) {
                throw new OrekitExceptionWrapper(oe);
            }
        };

        return model;
    }


    /** leastsquare problem builder.
     * @param maxEvaluations maxIterations and evaluations
     * @param convergenceThreshold parameter convergence threshold
     * @throws RuggedException if sensor is not found
     * @return
     */
    @Override
    public final LeastSquaresProblem build(final int maxEvaluations, final double convergenceThreshold) throws RuggedException {

        this.createTargetAndWeight();
        final double[] target = this.targetAndWeight.get("Target");
        final double[] start = this.createStartTab();
        final ParameterValidator validator = this.createParameterValidator();
        final ConvergenceChecker<LeastSquaresProblem.Evaluation> checker = this.createChecker(convergenceThreshold);
        final MultivariateJacobianFunction model = this.createFunction();
        return new LeastSquaresBuilder()
                        .lazyEvaluation(false).maxIterations(maxEvaluations)
                        .maxEvaluations(maxEvaluations).weight(null).start(start)
                        .target(target).parameterValidator(validator).checker(checker)
                        .model(model).build();
    }

}
