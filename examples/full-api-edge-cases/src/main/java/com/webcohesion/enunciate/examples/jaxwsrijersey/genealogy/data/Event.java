/**
 * Copyright © 2006-2016 Web Cohesion (info@webcohesion.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An event assertion.
 *
 * @author Ryan Heaton
 */
@XmlRootElement
public class Event extends OccurringAssertion {

  private EventType type;
  private String description;
  private final List<String> tags = new ArrayList<String>();
  private String explanation;
  private List<EventAttribute> attributes;
  private ContributorImpl contributorImpl;

  /**
   * The type of this event.
   *
   * @return The type of this event.
   */
  @XmlAttribute
  public EventType getType() {
    return type;
  }

  /**
   * The type of this event.
   *
   * @param type The type of this event.
   */
  public void setType(EventType type) {
    this.type = type;
  }

  /**
   * A description of this event.
   *
   * @return A description of this event.
   */
  @NotNull
  @Size (max = 25, min = 2)
  public String getDescription() {
    return description;
  }

  /**
   * A description of this event.
   *
   * @param description A description of this event.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public String[] getTags() {
    return tags.toArray(new String[tags.size()]);
  }

  public void setTags(String tags[]) {
    this.tags.clear();
    this.tags.addAll(Arrays.asList(tags));
  }

  public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

  @XmlElementWrapper ( name = "attributes" )
  @XmlElement ( name = "attribute" )
  public List<EventAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<EventAttribute> attributes) {
    this.attributes = attributes;
  }

  public ContributorImpl getContributorImpl() {
    return contributorImpl;
  }

  public void setContributorImpl(ContributorImpl contributorImpl) {
    this.contributorImpl = contributorImpl;
  }
}
