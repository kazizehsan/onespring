package com.lessons.onespring.aop;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@NoArgsConstructor
public class AspectEventDetails {
    private String resource_id = "";
    private String resource_name = "";
    private String path = "";
    private HashMap<String, Object> params = new HashMap<>();
    private HashMap<String, Object> previous_values = new HashMap<>();
    private HashMap<String, Object> new_values = new HashMap<>();


    public void addParam(String key, Object value) {
        params.put(key, value);
    }

    public void addPreviousValue(String key, Object value) {
        previous_values.put(key, value);
    }

    public void addNewValue(String key, Object value) {
        new_values.put(key, value);
    }

    public String getJson() throws JsonProcessingException {
        HashMap<String, Object> map = new HashMap<>();
        if (!resource_id.isBlank()) {
            map.put("resource_id", resource_id);
        }
        if (!resource_name.isBlank()) {
            map.put("resource_name", resource_name);
        }
        if (!path.isBlank()) {
            map.put("path", path);
        }
        if (!params.isEmpty()) {
            map.putAll(params);
        }
        if (!previous_values.isEmpty()) {
            map.put("previous_values", previous_values);
        }
        if (!new_values.isEmpty()) {
            map.put("new_values", new_values);
        }
        return new ObjectMapper().writeValueAsString(map);
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    public void setResource_name(String resource_name) {
        this.resource_name = resource_name;
    }

    public void setPath(String path) {
        this.path = path;
    }
}