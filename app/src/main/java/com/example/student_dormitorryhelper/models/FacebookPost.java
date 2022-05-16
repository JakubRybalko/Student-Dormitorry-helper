package com.example.student_dormitorryhelper.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacebookPost {
  @JsonProperty("created_time")
  private Date createdTime;
  @JsonProperty("id")
  private String id;
  @JsonProperty("message")
  private String message;
  @JsonProperty("full_picture")
  private String full_picture;

  public Date getCreatedTime() {
    return createdTime;
  }

  public String getId() {
    return id;
  }

  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setFull_picture(String full_picture) {
    this.full_picture = full_picture;
  }

  public String getMessage() {
    return message;
  }

  public String getFull_picture() {
    return full_picture;
  }


  @JsonProperty("from")
  private Map<String, String> from;

  public void setFrom(Map<String, String> from) {
    this.from = from;
  }

  public Map<String, String> getFrom() {
    return from;
  }
}
