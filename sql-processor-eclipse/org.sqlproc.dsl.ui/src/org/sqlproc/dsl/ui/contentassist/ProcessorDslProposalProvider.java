/*
 * generated by Xtext
 */
package org.sqlproc.dsl.ui.contentassist;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.sqlproc.dsl.processorDsl.Artifacts;
import org.sqlproc.dsl.processorDsl.ColumnUsage;
import org.sqlproc.dsl.processorDsl.ConstantUsage;
import org.sqlproc.dsl.processorDsl.IdentifierUsage;
import org.sqlproc.dsl.processorDsl.MappingRule;
import org.sqlproc.dsl.processorDsl.MappingUsage;
import org.sqlproc.dsl.processorDsl.MetaStatement;
import org.sqlproc.dsl.processorDsl.PojoDefinition;
import org.sqlproc.dsl.processorDsl.PojoUsage;
import org.sqlproc.dsl.processorDsl.ProcessorDslPackage;
import org.sqlproc.dsl.resolver.PojoResolver;

import com.google.inject.Inject;

/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class ProcessorDslProposalProvider extends AbstractProcessorDslProposalProvider {

    @Inject
    PojoResolver pojoResolver;

    @Override
    public void completeColumn_Name(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        if (!completeUsage(model, assignment, context, acceptor, ProcessorDslPackage.Literals.COLUMN_USAGE.getName()))
            super.completeColumn_Name(model, assignment, context, acceptor);
    }

    @Override
    public void completeConstant_Name(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        if (!completeUsage(model, assignment, context, acceptor, ProcessorDslPackage.Literals.CONSTANT_USAGE.getName()))
            super.completeConstant_Name(model, assignment, context, acceptor);
    }

    @Override
    public void completeIdentifier_Name(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        if (!completeUsage(model, assignment, context, acceptor,
                ProcessorDslPackage.Literals.IDENTIFIER_USAGE.getName()))
            super.completeIdentifier_Name(model, assignment, context, acceptor);
    }

    public boolean completeUsage(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor, String name) {
        MetaStatement metaStatement = EcoreUtil2.getContainerOfType(model, MetaStatement.class);
        Artifacts artifacts = EcoreUtil2.getContainerOfType(metaStatement, Artifacts.class);
        IScope scope = getScopeProvider().getScope(artifacts, ProcessorDslPackage.Literals.ARTIFACTS__USAGES);
        PojoDefinition pojoDefinition = findPojo(artifacts.eResource().getResourceSet(), scope, name,
                metaStatement.getName());
        if (pojoDefinition == null) {
            String proposal = getValueConverter().toString("Error: I can't load pojo for " + model, "IDENT");
            ICompletionProposal completionProposal = createCompletionProposal(proposal, context);
            acceptor.accept(completionProposal);
            return true;
        }
        String prefix = context.getPrefix();
        int pos = prefix.lastIndexOf('.');
        if (pos > 0) {
            prefix = prefix.substring(0, pos + 1);
        } else {
            prefix = "";
        }
        String clazz = getClassName(pojoDefinition.getClass_(), prefix);
        if (clazz == null)
            return false;
        PropertyDescriptor[] descriptors = pojoResolver.getPropertyDescriptors(clazz);
        if (descriptors == null) {
            return false;
        } else {
            for (PropertyDescriptor descriptor : descriptors) {
                if ("class".equals(descriptor.getName()))
                    continue;
                String proposal = getValueConverter().toString(descriptor.getName(), "IDENT");
                ICompletionProposal completionProposal = createCompletionProposal(prefix + proposal, context);
                acceptor.accept(completionProposal);
            }
            return true;
        }
    }

    @Override
    public void completeMappingColumn_Name(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        MappingRule mappingRule = EcoreUtil2.getContainerOfType(model, MappingRule.class);
        Artifacts artifacts = EcoreUtil2.getContainerOfType(mappingRule, Artifacts.class);
        IScope scope = getScopeProvider().getScope(artifacts, ProcessorDslPackage.Literals.ARTIFACTS__USAGES);
        PojoDefinition pojoDefinition = findPojo(artifacts.eResource().getResourceSet(), scope,
                ProcessorDslPackage.Literals.MAPPING_USAGE.getName(), mappingRule.getName());
        if (pojoDefinition == null) {
            String proposal = getValueConverter().toString("Error: I can't load pojo for " + model, "IDENT");
            ICompletionProposal completionProposal = createCompletionProposal(proposal, context);
            acceptor.accept(completionProposal);
        }
        String prefix = context.getPrefix();
        int pos = prefix.lastIndexOf('.');
        if (pos > 0) {
            prefix = prefix.substring(0, pos + 1);
        } else {
            prefix = "";
        }
        String clazz = getClassName(pojoDefinition.getClass_(), prefix);
        if (clazz == null)
            return;
        PropertyDescriptor[] descriptors = pojoResolver.getPropertyDescriptors(clazz);
        if (descriptors == null) {
            super.completeMappingColumn_Name(model, assignment, context, acceptor);
        } else {
            for (PropertyDescriptor descriptor : descriptors) {
                if ("class".equals(descriptor.getName()))
                    continue;
                String proposal = getValueConverter().toString(descriptor.getName(), "IDENT");
                ICompletionProposal completionProposal = createCompletionProposal(prefix + proposal, context);
                acceptor.accept(completionProposal);
            }
        }
    }

    protected PojoDefinition findPojo(ResourceSet resourceSet, IScope scope, String typeName, String name) {
        Iterable<IEObjectDescription> iterable = scope.getAllElements();
        for (Iterator<IEObjectDescription> iter = iterable.iterator(); iter.hasNext();) {
            IEObjectDescription description = iter.next();
            if (typeName.equals(description.getEClass().getName())) {
                PojoUsage pojoUsage = (PojoUsage) resourceSet.getEObject(description.getEObjectURI(), true);
                if (name.equals(getUsageName(pojoUsage))) {
                    // return ((PojoUsage) description.getEObjectOrProxy()).getPojo(); // neni inicializovan!
                    return pojoUsage.getPojo();
                }
            }
        }
        return null;
    }

    protected String getUsageName(EObject pojoUsage) {
        if (pojoUsage instanceof ColumnUsage)
            return ((ColumnUsage) pojoUsage).getStatement().getName();
        if (pojoUsage instanceof IdentifierUsage)
            return ((IdentifierUsage) pojoUsage).getStatement().getName();
        if (pojoUsage instanceof ConstantUsage)
            return ((ConstantUsage) pojoUsage).getStatement().getName();
        if (pojoUsage instanceof MappingUsage)
            return ((MappingUsage) pojoUsage).getStatement().getName();
        return "";
    }

    protected boolean isPrimitive(Class<?> clazz) {
        if (clazz == null)
            return true;
        if (clazz.isPrimitive())
            return true;
        if (clazz == String.class)
            return true;
        if (clazz == java.util.Date.class)
            return true;
        if (clazz == java.sql.Date.class)
            return true;
        if (clazz == java.sql.Time.class)
            return true;
        if (clazz == java.sql.Timestamp.class)
            return true;
        if (clazz == java.sql.Blob.class)
            return true;
        if (clazz == java.sql.Clob.class)
            return true;
        if (clazz == java.math.BigDecimal.class)
            return true;
        if (clazz == java.math.BigInteger.class)
            return true;
        return false;
    }

    protected String getClassName(String baseClass, String property) {
        if (baseClass == null || property == null)
            return baseClass;
        int pos1 = property.indexOf('.');
        if (pos1 == -1)
            return baseClass;
        String checkProperty = property;
        pos1 = checkProperty.indexOf('=');
        if (pos1 > 0) {
            int pos2 = checkProperty.indexOf('.', pos1);
            if (pos2 > pos1)
                checkProperty = checkProperty.substring(0, pos1) + checkProperty.substring(pos2);
        }
        String innerProperty = null;
        pos1 = checkProperty.indexOf('.');
        if (pos1 > 0) {
            innerProperty = checkProperty.substring(pos1 + 1);
            checkProperty = checkProperty.substring(0, pos1);
        }
        PropertyDescriptor[] descriptors = pojoResolver.getPropertyDescriptors(baseClass);
        if (descriptors == null)
            return null;
        PropertyDescriptor innerDesriptor = null;
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getName().equals(checkProperty)) {
                innerDesriptor = descriptor;
                break;
            }
        }
        if (innerDesriptor == null)
            return null;
        Class<?> innerClass = innerDesriptor.getPropertyType();
        if (innerClass.isArray()) {
            ParameterizedType type = (ParameterizedType) innerDesriptor.getReadMethod().getGenericReturnType();
            if (type.getActualTypeArguments() == null || type.getActualTypeArguments().length == 0)
                return null;
            innerClass = (Class<?>) type.getActualTypeArguments()[0];
            if (isPrimitive(innerClass))
                return null;
            return getClassName(innerClass.getName(), innerProperty);
        } else if (Collection.class.isAssignableFrom(innerClass)) {
            ParameterizedType type = (ParameterizedType) innerDesriptor.getReadMethod().getGenericReturnType();
            if (type.getActualTypeArguments() == null || type.getActualTypeArguments().length == 0)
                return null;
            innerClass = (Class<?>) type.getActualTypeArguments()[0];
            if (isPrimitive(innerClass))
                return null;
            return getClassName(innerClass.getName(), innerProperty);
        } else {
            if (isPrimitive(innerClass))
                return null;
            return getClassName(innerClass.getName(), innerProperty);
        }
    }
}
