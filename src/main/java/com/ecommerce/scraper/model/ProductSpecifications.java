package com.ecommerce.scraper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class ProductSpecifications {

    @JsonProperty("operating_system")
    private String operatingSystem;

    @JsonProperty("processor")
    private String processor;

    @JsonProperty("max_processor_speed")
    private String maxProcessorSpeed;

    @JsonProperty("processor_count")
    private String processorCount;

    @JsonProperty("graphic_processor")
    private String graphicProcessor;

    @JsonProperty("screen_size")
    private String screenSize;

    @JsonProperty("screen_type")
    private String screenType;

    @JsonProperty("refresh_rate")
    private String refreshRate;

    @JsonProperty("resolution")
    private String resolution;

    @JsonProperty("back_camera_mp")
    private String backCameraMp;

    @JsonProperty("back_camera_count")
    private String backCameraCount;

    @JsonProperty("front_camera_mp")
    private String frontCameraMp;

    @JsonProperty("video_resolution")
    private String videoResolution;

    @JsonProperty("ram")
    private String ram;

    @JsonProperty("battery")
    private String battery;

    @JsonProperty("weight")
    private String weight;

    @JsonProperty("dimensions")
    private Map<String, String> dimensions = new HashMap<>();

    @JsonProperty("additional_specs")
    private Map<String, String> additionalSpecs = new HashMap<>();

    // Constructors
    public ProductSpecifications() {}

    // Getters and Setters
    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getMaxProcessorSpeed() {
        return maxProcessorSpeed;
    }

    public void setMaxProcessorSpeed(String maxProcessorSpeed) {
        this.maxProcessorSpeed = maxProcessorSpeed;
    }

    public String getProcessorCount() {
        return processorCount;
    }

    public void setProcessorCount(String processorCount) {
        this.processorCount = processorCount;
    }

    public String getGraphicProcessor() {
        return graphicProcessor;
    }

    public void setGraphicProcessor(String graphicProcessor) {
        this.graphicProcessor = graphicProcessor;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public String getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(String refreshRate) {
        this.refreshRate = refreshRate;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getBackCameraMp() {
        return backCameraMp;
    }

    public void setBackCameraMp(String backCameraMp) {
        this.backCameraMp = backCameraMp;
    }

    public String getBackCameraCount() {
        return backCameraCount;
    }

    public void setBackCameraCount(String backCameraCount) {
        this.backCameraCount = backCameraCount;
    }

    public String getFrontCameraMp() {
        return frontCameraMp;
    }

    public void setFrontCameraMp(String frontCameraMp) {
        this.frontCameraMp = frontCameraMp;
    }

    public String getVideoResolution() {
        return videoResolution;
    }

    public void setVideoResolution(String videoResolution) {
        this.videoResolution = videoResolution;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Map<String, String> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Map<String, String> dimensions) {
        this.dimensions = dimensions;
    }

    public Map<String, String> getAdditionalSpecs() {
        return additionalSpecs;
    }

    public void setAdditionalSpecs(Map<String, String> additionalSpecs) {
        this.additionalSpecs = additionalSpecs;
    }
}