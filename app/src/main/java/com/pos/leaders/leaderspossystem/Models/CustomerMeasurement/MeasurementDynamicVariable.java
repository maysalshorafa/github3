package com.pos.leaders.leaderspossystem.Models.CustomerMeasurement;

/**
 * Created by Win8.1 on 12/14/2017.
 */

public class MeasurementDynamicVariable {
    private long measurementDynamicVariableId;
    private String name;
    private String type;
    private String unit;
    private boolean hide;

    // Constructors

    public MeasurementDynamicVariable() {
    }

    public MeasurementDynamicVariable(long measurementDynamicVariableId, String name, String type, String unit, boolean hide) {
        this.measurementDynamicVariableId = measurementDynamicVariableId;
        this.name = name;
        this.type = type;
        this.unit = unit;
        this.hide = hide;
    }

    public MeasurementDynamicVariable(long measurementDynamicVariableId, String name, String type, String unit) {
        this.measurementDynamicVariableId = measurementDynamicVariableId;
        this.name = name;
        this.type = type;
        this.unit = unit;
    }
    public MeasurementDynamicVariable(MeasurementDynamicVariable measurementDynamicVariable) {
        this(measurementDynamicVariable.getMeasurementDynamicVariableId(),measurementDynamicVariable.getName(),measurementDynamicVariable.getType(),measurementDynamicVariable.getUnit());
    }
    //end Constructors
    // Getters

    public long getMeasurementDynamicVariableId() {
        return measurementDynamicVariableId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }

    //end Getters
    //Setters

    public void setMeasurementDynamicVariableId(long measurementDynamicVariableId) {
        this.measurementDynamicVariableId = measurementDynamicVariableId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }
    public boolean isHide() {
        return hide;
    }
    //end Setters

    @Override
    public String toString() {
        return "MeasurementDynamicVariable{" +
                "measurementDynamicVariableId=" + measurementDynamicVariableId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", unit='" + unit + '\'' +
                ", hide=" + hide +
                '}';
    }
    // isValidValue Method return boolean Value
}
