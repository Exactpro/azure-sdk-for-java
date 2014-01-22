/**
 * 
 * Copyright (c) Microsoft and contributors.  All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

// Warning: This code was generated by a tool.
// 
// Changes to this file may cause incorrect behavior and will be lost if the
// code is regenerated.

package com.microsoft.windowsazure.management.monitoring.metrics.models;

import java.util.ArrayList;
import javax.xml.datatype.Duration;

/**
* Metric definition class specifies the metadata for a metric.
*/
public class MetricDefinition
{
    private String displayName;
    
    /**
    * Metric display name.
    * @return The DisplayName value.
    */
    public String getDisplayName()
    {
        return this.displayName;
    }
    
    /**
    * Metric display name.
    * @param displayNameValue The DisplayName value.
    */
    public void setDisplayName(final String displayNameValue)
    {
        this.displayName = displayNameValue;
    }
    
    private boolean isAlertable;
    
    /**
    * Specifies if the metric is alertable. Alerts can be defined on a metric
    * only if this property is true.
    * @return The IsAlertable value.
    */
    public boolean isAlertable()
    {
        return this.isAlertable;
    }
    
    /**
    * Specifies if the metric is alertable. Alerts can be defined on a metric
    * only if this property is true.
    * @param isAlertableValue The IsAlertable value.
    */
    public void setIsAlertable(final boolean isAlertableValue)
    {
        this.isAlertable = isAlertableValue;
    }
    
    private ArrayList<MetricAvailability> metricAvailabilities;
    
    /**
    * Metric availability specifies the time grain (aggregation interval) and
    * the retention period for the metric in a timegrain.
    * @return The MetricAvailabilities value.
    */
    public ArrayList<MetricAvailability> getMetricAvailabilities()
    {
        return this.metricAvailabilities;
    }
    
    /**
    * Metric availability specifies the time grain (aggregation interval) and
    * the retention period for the metric in a timegrain.
    * @param metricAvailabilitiesValue The MetricAvailabilities value.
    */
    public void setMetricAvailabilities(final ArrayList<MetricAvailability> metricAvailabilitiesValue)
    {
        this.metricAvailabilities = metricAvailabilitiesValue;
    }
    
    private Duration minimumAlertableTimeWindow;
    
    /**
    * Specifies the minimum alertable time window for the metric.
    * @return The MinimumAlertableTimeWindow value.
    */
    public Duration getMinimumAlertableTimeWindow()
    {
        return this.minimumAlertableTimeWindow;
    }
    
    /**
    * Specifies the minimum alertable time window for the metric.
    * @param minimumAlertableTimeWindowValue The MinimumAlertableTimeWindow
    * value.
    */
    public void setMinimumAlertableTimeWindow(final Duration minimumAlertableTimeWindowValue)
    {
        this.minimumAlertableTimeWindow = minimumAlertableTimeWindowValue;
    }
    
    private String name;
    
    /**
    * Get the metric name.
    * @return The Name value.
    */
    public String getName()
    {
        return this.name;
    }
    
    /**
    * Get the metric name.
    * @param nameValue The Name value.
    */
    public void setName(final String nameValue)
    {
        this.name = nameValue;
    }
    
    private String namespace;
    
    /**
    * Get the metric namespace.
    * @return The Namespace value.
    */
    public String getNamespace()
    {
        return this.namespace;
    }
    
    /**
    * Get the metric namespace.
    * @param namespaceValue The Namespace value.
    */
    public void setNamespace(final String namespaceValue)
    {
        this.namespace = namespaceValue;
    }
    
    private String primaryAggregation;
    
    /**
    * Metric primary aggregation specifies the default type for the metrics.
    * This indicates if the metric is of type average, total, minimum or
    * maximum.
    * @return The PrimaryAggregation value.
    */
    public String getPrimaryAggregation()
    {
        return this.primaryAggregation;
    }
    
    /**
    * Metric primary aggregation specifies the default type for the metrics.
    * This indicates if the metric is of type average, total, minimum or
    * maximum.
    * @param primaryAggregationValue The PrimaryAggregation value.
    */
    public void setPrimaryAggregation(final String primaryAggregationValue)
    {
        this.primaryAggregation = primaryAggregationValue;
    }
    
    private String resourceIdSuffix;
    
    /**
    * Metric resource id suffix specfies the sub-resource path within the the
    * resource for the metric.
    * @return The ResourceIdSuffix value.
    */
    public String getResourceIdSuffix()
    {
        return this.resourceIdSuffix;
    }
    
    /**
    * Metric resource id suffix specfies the sub-resource path within the the
    * resource for the metric.
    * @param resourceIdSuffixValue The ResourceIdSuffix value.
    */
    public void setResourceIdSuffix(final String resourceIdSuffixValue)
    {
        this.resourceIdSuffix = resourceIdSuffixValue;
    }
    
    private String unit;
    
    /**
    * The unit for the metric.
    * @return The Unit value.
    */
    public String getUnit()
    {
        return this.unit;
    }
    
    /**
    * The unit for the metric.
    * @param unitValue The Unit value.
    */
    public void setUnit(final String unitValue)
    {
        this.unit = unitValue;
    }
    
    /**
    * Initializes a new instance of the MetricDefinition class.
    *
    */
    public MetricDefinition()
    {
        this.metricAvailabilities = new ArrayList<MetricAvailability>();
    }
}