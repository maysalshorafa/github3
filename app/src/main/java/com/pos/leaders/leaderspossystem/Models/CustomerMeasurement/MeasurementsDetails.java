package com.pos.leaders.leaderspossystem.Models.CustomerMeasurement;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Win8.1 on 12/14/2017.
 */

public class MeasurementsDetails {
    private long measurementsDetailsId;
    private long measurementId;
    private long dynamicVarId;
    private String value;
    @JsonIgnore
    private MeasurementDynamicVariable measurementDynamicVariable;
    //Constructors
    public MeasurementsDetails() {
    }

    public MeasurementsDetails(long measurementsDetailsId, long measurementId, long dynamicVarId, String value) {
        this.measurementsDetailsId = measurementsDetailsId;
        this.measurementId = measurementId;
        this.dynamicVarId = dynamicVarId;
        this.value = value;
    }
    public MeasurementsDetails(MeasurementsDetails measurementsDetails) {
        this(measurementsDetails.getMeasurementsDetailsId(),measurementsDetails.getMeasurementId(),measurementsDetails.getDynamicVarId(),measurementsDetails.getValue());
    }
    // end

    //Getters

    public long getMeasurementsDetailsId() {
        return measurementsDetailsId;
    }

    public long getMeasurementId() {
        return measurementId;
    }

    public long getDynamicVarId() {
        return dynamicVarId;
    }

    public String getValue() {
        return value;
    }

    public MeasurementDynamicVariable getMeasurementDynamicVariable() {
        return measurementDynamicVariable;
    }
    // end
    //Setters

    public void setMeasurementsDetailsId(long measurementsDetailsId) {
        this.measurementsDetailsId = measurementsDetailsId;
    }

    public void setMeasurementId(long measurementId) {
        this.measurementId = measurementId;
    }

    public void setDynamicVarId(long dynamicVarId) {
        this.dynamicVarId = dynamicVarId;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setMeasurementDynamicVariable(MeasurementDynamicVariable measurementDynamicVariable) {
        this.measurementDynamicVariable = measurementDynamicVariable;
    }
    //end

    @Override
    public String toString() {
        return "MeasurementsDetails{" +
                "measurementsDetailsId=" + measurementsDetailsId +
                ", measurementId=" + measurementId +
                ", dynamicVarId=" + dynamicVarId +
                ", value='" + value + '\'' +
                ", measurementDynamicVariable=" + measurementDynamicVariable +
                '}';
    }
}
