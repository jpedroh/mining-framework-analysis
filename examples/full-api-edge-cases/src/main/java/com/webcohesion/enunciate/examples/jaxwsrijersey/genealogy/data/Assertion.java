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

import com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.cite.InfoSet;
import com.webcohesion.enunciate.metadata.Ignore;

import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * An assertion of a piece of information, usually associated with a source.
 *
 * @author Ryan Heaton
 */
@XmlSeeAlso({Gender.class,Name.class})
@XmlType(
  propOrder = {"id", "note", "infoSet", "contributors"}
)
@Ignore
public abstract class Assertion {

  private String id;
  private String note;
  private InfoSet infoSet;
  private List<Contributor> contributors;

  /**
   * The id of the assertion.
   *
   * @return The id of the assertion.
   */
  @XmlID
  @XmlAttribute
  public String getId() {
    return id;
  }

  /**
   * The id of the assertion.
   *
   * @param id The id of the assertion.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * A note associated with this assertion.
   *
   * @return A note associated with this assertion.
   */
  public String getNote() {
    return note;
  }

  /**
   * A note associated with this assertion.
   *
   * @param note A note associated with this assertion.
   */
  public void setNote(String note) {
    this.note = note;
  }

  /**
   * The infoset from which this assertion was made.
   *
   * @return The infoset from which this assertion was made.
   */
  @XmlIDREF
  public InfoSet getInfoSet() {
    return infoSet;
  }

  /**
   * The infoset from which this assertion was made.
   *
   * @param infoSet The infoset from which this assertion was made.
   */
  public void setInfoSet(InfoSet infoSet) {
    this.infoSet = infoSet;
  }

  /**
   * List of contacts for this assertion.
   *
   * @return List of contacts for this assertion.
   */
  public List<Contributor> getContributors() {
    return contributors;
  }

  /**
   * List of contacts for this assertion.
   *
   * @param contributors List of contacts for this assertion.
   */
  public void setContributors(List<Contributor> contributors) {
    this.contributors = contributors;
  }
}
