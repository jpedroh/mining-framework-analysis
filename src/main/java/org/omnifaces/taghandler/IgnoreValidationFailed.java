/*
 * Copyright 2012 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.taghandler;

import static org.omnifaces.util.Components.getClosestParent;
import static org.omnifaces.util.Components.hasInvokedSubmit;
import static org.omnifaces.util.Events.addBeforePhaseListener;
import static org.omnifaces.util.Faces.setContextAttribute;
import static javax.faces.event.PhaseId.RESTORE_VIEW;
import static org.omnifaces.util.Events.subscribeToRequestAfterPhase;

import java.io.IOException;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

import org.omnifaces.component.input.Form;
import org.omnifaces.util.Callback;

/**
 * <p>
 * The <code>&lt;o:ignoreValidationFailed&gt;</code> taghandler allows the developer to ignore validation failures when
 * executing an {@link UICommand} action. This taghandler must be placed inside an {@link UICommand} component and the
 * parent {@link UIForm} must be an <code>&lt;o:form&gt;</code>. When executing an ajax action, make sure that the
 * parent {@link UIForm} is also included in the <code>&lt;f:ajax execute&gt;</code>.
 *
 * <h3>Usage</h3>
 * <p>
 * For example:
 * <pre>
 * &lt;o:form&gt;
 *     ...
 *     &lt;h:commandButton value="save valid data" action="#{bean.saveValidData}"&gt;
 *         &lt;o:ignoreValidationFailed /&gt;
 *         &lt;f:ajax execute="@form" /&gt;
 *     &lt;/h:commandButton&gt;
 * &lt;/o:form&gt;
 * </pre>
 * <p>
 * Note that the model values will (obviously) only be updated for components which have actually passed the validation.
 * Also the validation messages will still be displayed. If you prefer to not display them, then you'd need to exclude
 * them from rendering by <code>&lt;f:ajax render&gt;</code>, or to put a proper condition in the <code>rendered</code>
 * attribute.
 *
 * @author Bauke Scholtz
 * @see Form
 */
public class IgnoreValidationFailed extends TagHandler {

	// Constants ------------------------------------------------------------------------------------------------------

	private static final String ERROR_INVALID_PARENT =
		"Parent component of o:ignoreValidationFailed must be an instance of UICommand.";
	private static final String ERROR_INVALID_FORM =
		"Parent form of o:ignoreValidationFailed must be an o:form, not h:form.";

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * The tag constructor.
	 * @param config The tag config.
	 */
	public IgnoreValidationFailed(TagConfig config) {
		super(config);
	}

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * If the parent component is an instance of {@link UICommand} and is new and we're in the restore view phase of
	 * a postback, then delegate to {@link #processIgnoreValidationFailed(UICommand)}.
	 * @throws IllegalArgumentException When the parent component is not an instance of {@link UICommand}.
	 */
	@Override
	public void apply(FaceletContext context, final UIComponent parent) throws IOException {
		if (!(parent instanceof UICommand)) {
			throw new IllegalArgumentException(ERROR_INVALID_PARENT);
		}

<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/IgnoreValidationFailed.java/left.java
		if (ComponentHandler.isNew(parent)) {
			addBeforePhaseListener(PhaseId.PROCESS_VALIDATIONS, new Callback.Void() {
||||||| /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/IgnoreValidationFailed.java/base.java
		if (ComponentHandler.isNew(parent)) {
			Events.addBeforePhaseListener(PhaseId.PROCESS_VALIDATIONS, new Callback.Void() {
=======
		FacesContext facesContext = context.getFacesContext();
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/IgnoreValidationFailed.java/right.java

<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/IgnoreValidationFailed.java/left.java
				@Override
				public void invoke() {
					if (hasInvokedSubmit(parent)) {
						setContextAttribute(IgnoreValidationFailed.class.getName(), true);
					}
				}
			});
||||||| /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/IgnoreValidationFailed.java/base.java
				@Override
				public void invoke() {
					if (Components.hasInvokedSubmit(parent)) {
						Faces.setContextAttribute(IgnoreValidationFailed.class.getName(), true);
					}
				}
			});
=======
		if (!(ComponentHandler.isNew(parent) && facesContext.isPostback() && facesContext.getCurrentPhaseId() == RESTORE_VIEW)) {
			return;
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/IgnoreValidationFailed.java/right.java
		}
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/IgnoreValidationFailed.java/left.java
		else {
			if (getClosestParent(parent, Form.class) == null) {
				throw new IllegalArgumentException(ERROR_INVALID_FORM);
			}
||||||| /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/IgnoreValidationFailed.java/base.java
		else {
			if (Components.getClosestParent(parent, Form.class) == null) {
				throw new IllegalArgumentException(ERROR_INVALID_FORM);
			}
=======

		// We can't use hasInvokedSubmit() before the component is added to view, because the client ID isn't available.
		// Hence, we subscribe this check to after phase of restore view.
		subscribeToRequestAfterPhase(RESTORE_VIEW, new Callback.Void() { private static final long serialVersionUID = 1L; @Override public void invoke() {
			processIgnoreValidationFailed((UICommand) parent);
		}});
	}

	/**
	 * Check if the given command component has been invoked during the current request and if so, then instruct the
	 * parent <code>&lt;o:form&gt;</code> to ignore the validation.
	 * @param command The command component.
	 * @throws IllegalArgumentException When the given command component is not inside a <code>&lt;o:form&gt;</code>.
	 */
	protected void processIgnoreValidationFailed(UICommand command) {
		if (!hasInvokedSubmit(command)) {
			return;
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/IgnoreValidationFailed.java/right.java
		}

		Form form = getClosestParent(command, Form.class);

		if (form == null) {
			throw new IllegalArgumentException(ERROR_INVALID_FORM);
		}

		form.setIgnoreValidationFailed(true);
	}

}
