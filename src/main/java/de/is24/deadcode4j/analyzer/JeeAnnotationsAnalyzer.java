package de.is24.deadcode4j.analyzer;

import de.is24.deadcode4j.Analyzer;

/**
 * Analyzes class files: marks a class as being in use if it is annotated with one of those JEE annotations:
 * <ul>
 * <li>javax.annotation.ManagedBean</li>
 * <li>javax.inject.Named</li>
 * <li>javax.persistence.metamodel.StaticMetamodel</li>
 * <li>javax.xml.bind.annotation.XmlSchema</li>
 * </ul>
 *
 * @since 1.3
 */
public final class JeeAnnotationsAnalyzer extends AnnotationsAnalyzer implements Analyzer {

    public JeeAnnotationsAnalyzer() {
        super("_JEE-Annotation_",
                "javax.annotation.ManagedBean",
                "javax.faces.component.behavior.FacesBehavior",
                "javax.faces.convert.FacesConverter",
                "javax.faces.event.ListenerFor",
                "javax.faces.event.ListenersFor",
                "javax.faces.event.NamedEvent",
                "javax.faces.render.FacesBehaviorRenderer",
                "javax.faces.render.FacesRenderer",
                "javax.faces.validator.FacesValidator",
                "javax.faces.view.facelets.FaceletsResourceResolver",
                "javax.inject.Named",
                "javax.persistence.metamodel.StaticMetamodel",
                "javax.xml.bind.annotation.XmlSchema");
    }

}
