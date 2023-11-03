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
package com.webcohesion.enunciate.modules.jaxws.model;

import com.webcohesion.enunciate.javac.decorations.TypeMirrorDecorator;
import com.webcohesion.enunciate.javac.decorations.type.DecoratedTypeMirror;
import com.webcohesion.enunciate.modules.jaxb.model.ImplicitChildElement;
import com.webcohesion.enunciate.modules.jaxb.model.adapters.Adaptable;
import com.webcohesion.enunciate.modules.jaxb.model.adapters.AdapterType;
import com.webcohesion.enunciate.modules.jaxb.model.types.XmlType;
import com.webcohesion.enunciate.modules.jaxb.model.types.XmlTypeFactory;
import com.webcohesion.enunciate.modules.jaxb.model.util.JAXBUtil;
import com.webcohesion.enunciate.modules.jaxws.EnunciateJaxwsContext;
import com.webcohesion.enunciate.util.HasClientConvertibleType;

import jakarta.jws.soap.SOAPBinding;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A decorated type mirror that is a web result.
 *
 * @author Ryan Heaton
 */
public class WebResult extends DecoratedTypeMirror<TypeMirror> implements Adaptable, WebMessage, WebMessagePart, ImplicitChildElement, HasClientConvertibleType {

  private final boolean header;
  private final String name;
  private final String elementName;
  private final String partName;
  private final WebMethod method;
  private final AdapterType adapterType;
  private final jakarta.jws.WebResult annotation;
  private final EnunciateJaxwsContext context;

  protected WebResult(TypeMirror delegate, WebMethod method, EnunciateJaxwsContext context) {
    super(delegate, context.getContext().getProcessingEnvironment());
    this.context = context;
    this.method = method;

    this.annotation = method.getAnnotation(jakarta.jws.WebResult.class);

    String partName = "return";
    if ((this.annotation != null) && (!"".equals(this.annotation.partName()))) {
      partName = this.annotation.partName();
    }
    this.partName = partName;
    this.header = ((this.annotation != null) && (this.annotation.header()));
    this.adapterType = JAXBUtil.findAdapterType(method, context.getJaxbContext());

    String name = "return";
    if ((this.annotation != null) && (this.annotation.name() != null) && (!"".equals(this.annotation.name()))) {
      name = this.annotation.name();
      this.elementName = name;
    }
    else if (!isHeader() && isImplicitSchemaElement()) {
      this.elementName = method.getSimpleName() + "Response";
    }
    else if (this.header) {
      this.elementName = "";
    }
    else {
      this.elementName = name;
    }
    this.name = name;
  }

  /**
   * The name of the web result.
   *
   * @return The name of the web result.
   */
  public String getName() {
    return name;
  }

  /**
   * The namespace of the web result.
   *
   * @return The namespace of the web result.
   */
  public String getTargetNamespace() {
    String targetNamespace = isImplicitSchemaElement() ? method.getDeclaringEndpointInterface().getTargetNamespace() : "";
    if ((this.annotation != null) && (this.annotation.targetNamespace() != null) && (!"".equals(this.annotation.targetNamespace()))) {
      targetNamespace = this.annotation.targetNamespace();
    }
    return targetNamespace;
  }

  /**
   * The part name.
   *
   * @return The part name.
   */
  public String getPartName() {
    return partName;
  }

  /**
   * The web method.
   *
   * @return The web method.
   */
  public WebMethod getWebMethod() {
    return method;
  }

  /**
   * Whether this is a bare web result.
   *
   * @return Whether this is a bare web result.
   */
  public boolean isBare() {
    return method.getSoapParameterStyle() == SOAPBinding.ParameterStyle.BARE;
  }

  /**
   * The mode of this web result.
   *
   * @return The mode of this web result.
   */
  public jakarta.jws.WebParam.Mode getMode() {
    return jakarta.jws.WebParam.Mode.OUT;
  }

  /**
   * The message name in the case of a document/bare service.
   *
   * @return The message name in the case of a document/bare service.
   */
  public String getMessageName() {
    String messageName = null;

    if (isBare()) {
      messageName = method.getDeclaringEndpointInterface().getSimpleName() + "." + method.getSimpleName() + "Response";
    }
    else if (isHeader()) {
      messageName = method.getDeclaringEndpointInterface().getSimpleName() + "." + method.getSimpleName() + "." + getName();
    }

    return messageName;
  }

  /**
   * There is only message documentation if this web result is BARE.
   *
   * @return The documentation if BARE, null otherwise.
   */
  public String getMessageDocs() {
    if (isBare()) {
      return getDocComment();
    }

    return null;
  }

  // Inherited.
  public boolean isInput() {
    return false;
  }

  // Inherited.
  public boolean isOutput() {
    return true;
  }

  // Inherited.
  public boolean isHeader() {
    return header;
  }

  // Inherited.
  public boolean isFault() {
    return false;
  }

  /**
   * If this web result is a part, the comments for the result.
   *
   * @return The part docs.
   */
  public String getPartDocs() {
    if (isBare()) {
      return null;
    }

    return getDocComment();
  }

  /**
   * If the web method style is RPC, the particle type is TYPE.  Otherwise, it's ELEMENT.
   *
   * @return The particle type.
   */
  public ParticleType getParticleType() {
    return this.method.getSoapBindingStyle() == SOAPBinding.Style.RPC ? isHeader() ? ParticleType.ELEMENT : ParticleType.TYPE : ParticleType.ELEMENT;
  }

  /**
   * The qname of the particle for this web result.  If the {@link #getParticleType() particle type} is
   * TYPE then it's the qname of the xml type.  Otherwise, it's the qname of the implicit schema
   * element.
   *
   * @return The qname of the particle for this web result as a part.
   */
  public QName getParticleQName() {
    if (method.getSoapBindingStyle() == SOAPBinding.Style.RPC && !isHeader()) {
      return getTypeQName();
    }
    else {      
      return new QName(getTargetNamespace(), getElementName());
    }
  }

  /**
   * This web result defines an implicit schema element if it is of DOCUMENT binding style and it is
   * either BARE or a header.
   *
   * @return Whether this web result is an implicit schema element.
   */
  public boolean isImplicitSchemaElement() {
    return isHeader() || (method.getSoapBindingStyle() != SOAPBinding.Style.RPC && method.getSoapParameterStyle() == SOAPBinding.ParameterStyle.BARE);
  }

  // Inherited.
  public Collection<WebMessagePart> getParts() {
    if (!isBare() && !isHeader()) {
      throw new UnsupportedOperationException("Web result doesn't represent a complex method input/output.");
    }

    return new ArrayList<WebMessagePart>(Arrays.asList(this));
  }

  /**
   * The qname of the type of this result as an implicit schema element.
   *
   * @return The qname of the type of this result.
   */
  public QName getTypeQName() {
    return getXmlType().getQname();
  }

  /**
   * Gets the xml type of this result.
   *
   * @return The xml type of this result.
   */
  public XmlType getXmlType() {
    XmlType xmlType = XmlTypeFactory.findSpecifiedType(this, this.context.getJaxbContext());
    if (xmlType == null) {
      xmlType = XmlTypeFactory.getXmlType(getType(), this.context.getJaxbContext());
    }
    return xmlType;
  }

  public String getMimeType() {
    return null;
  }

  public boolean isSwaRef() {
    return false;
  }

  /**
   * The min occurs of a web result.
   *
   * @return 1 if primitive.  0 otherwise.
   */
  public int getMinOccurs() {
    DecoratedTypeMirror typeMirror = (DecoratedTypeMirror) TypeMirrorDecorator.decorate(this.delegate, this.env);
    return typeMirror.isPrimitive() ? 1 : 0;
  }

  /**
   * The max occurs of the web result.
   *
   * @return The max occurs.
   */
  public String getMaxOccurs() {
    DecoratedTypeMirror typeMirror = (DecoratedTypeMirror) TypeMirrorDecorator.decorate(this.delegate, this.env);
    boolean unbounded = typeMirror.isCollection() || typeMirror.isArray();
    if (typeMirror.isArray()) {
      TypeMirror componentType = ((ArrayType) typeMirror).getComponentType();
      //special case for byte[]
      if (componentType.getKind() == TypeKind.BYTE) {
        unbounded = false;
      }
    }
    return unbounded ? "unbounded" : "1";
  }

  /**
   * The element name.
   *
   * @return The element name.
   */
  public String getElementName() {
    return this.elementName;
  }

  /**
   * The element docs.
   *
   * @return The element docs.
   */
  public String getElementDocs() {
    return getDocComment();
  }

  /**
   * Used when treating this as a parameter.
   *
   * @return The delegate.
   */
  public TypeMirror getType() {
    return TypeMirrorDecorator.decorate(this.delegate, this.env);
  }

  // Inherited.
  public boolean isAdapted() {
    return this.adapterType != null;
  }

  // Inherited.
  public AdapterType getAdapterType() {
    return this.adapterType;
  }

  @Override
  public boolean isVoid() {
    return this.delegate.getKind() == TypeKind.VOID;
  }

  @Override
  public TypeMirror getClientConvertibleType() {
    return getType();
  }
}
